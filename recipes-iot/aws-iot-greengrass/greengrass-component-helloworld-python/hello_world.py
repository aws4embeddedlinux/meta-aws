#!/usr/bin/env python3
#
# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
# Adapted for AWS IoT Greengrass v2 with IPC support
#

import json
import logging
import platform
import sys
import time
import os
import glob
from datetime import datetime

# Setup logging to stdout
logger = logging.getLogger(__name__)
logging.basicConfig(stream=sys.stdout, level=logging.INFO)

# Try to import Greengrass IPC libraries
try:
    import awsiot.greengrasscoreipc
    import awsiot.greengrasscoreipc.client as client
    from awsiot.greengrasscoreipc.model import (
        IoTCoreMessage,
        PublishToIoTCoreRequest,
        QOS
    )
    GREENGRASS_IPC_AVAILABLE = True
    logger.info("Greengrass IPC libraries loaded successfully")
except ImportError as e:
    GREENGRASS_IPC_AVAILABLE = False
    logger.warning(f"Greengrass IPC libraries not available: {e}")

class HelloWorldComponent:
    def __init__(self):
        # Default configuration
        self.message = "Hello world! Sent from Greengrass Core."
        self.publish_topic = "hello/world/builtin"
        self.publish_interval = 5
        self.thing_name = "unknown"
        self.thing_name_retry_count = 0
        self.max_thing_name_retries = 60  # Try for 5 minutes (60 * 5 seconds)
        
        # Initialize IPC client
        self.ipc_client = None
        if GREENGRASS_IPC_AVAILABLE:
            try:
                self.ipc_client = awsiot.greengrasscoreipc.connect()
                logger.info("Successfully connected to Greengrass IPC")
            except Exception as e:
                logger.error(f"Failed to connect to Greengrass IPC: {e}")
                self.ipc_client = None
        
        # Get Thing Name using multiple methods with retry
        self.thing_name = self.get_thing_name_with_retry()
    
    def get_thing_name_with_retry(self):
        """Get Thing Name with retry logic for fleet provisioning completion"""
        logger.info("=== Attempting to retrieve Thing Name with retry logic ===")
        
        for attempt in range(self.max_thing_name_retries):
            thing_name = self.get_thing_name()
            
            if thing_name != "unknown" and thing_name.startswith("gg_") and len(thing_name) > 5:
                logger.info(f"Successfully retrieved valid Thing Name: {thing_name} (attempt {attempt + 1})")
                return thing_name
            
            if attempt < self.max_thing_name_retries - 1:
                logger.info(f"Thing Name not ready yet (attempt {attempt + 1}/{self.max_thing_name_retries}), waiting 5 seconds...")
                time.sleep(5)
            else:
                logger.warning(f"Failed to get valid Thing Name after {self.max_thing_name_retries} attempts")
        
        return thing_name  # Return whatever we got on the last attempt
    
    def get_thing_name(self):
        """Get the Thing Name using multiple fallback methods"""
        
        # Method 1: Check environment variable (most reliable after fleet provisioning)
        thing_name = os.environ.get('AWS_IOT_THING_NAME', '')
        if thing_name and thing_name.startswith('gg_'):
            logger.info(f"Found Thing Name in AWS_IOT_THING_NAME: {thing_name}")
            return thing_name
        
        # Method 2: Check other environment variables
        env_vars = ['THING_NAME', 'GG_THING_NAME']
        for var in env_vars:
            value = os.environ.get(var, '')
            if value and value.startswith('gg_'):
                logger.info(f"Found Thing Name in {var}: {value}")
                return value
        
        # Method 3: Check config transaction log (your discovery!)
        tlog_files = [
            '/greengrass/v2/config/config.tlog',
            '/greengrass/config/config.tlog'
        ]
        
        for tlog_file in tlog_files:
            if os.path.exists(tlog_file):
                try:
                    with open(tlog_file, 'r') as f:
                        content = f.read()
                        # Look for AWS_IOT_THING_NAME entries
                        lines = content.split('\n')
                        for line in reversed(lines):  # Check most recent entries first
                            if 'AWS_IOT_THING_NAME' in line and 'gg_' in line:
                                # Try to extract the thing name from JSON
                                try:
                                    import re
                                    match = re.search(r'"V":"(gg_[^"]+)"', line)
                                    if match:
                                        thing_name = match.group(1)
                                        logger.info(f"Found Thing Name in {tlog_file}: {thing_name}")
                                        return thing_name
                                except Exception as e:
                                    logger.debug(f"Error parsing tlog line: {e}")
                except Exception as e:
                    logger.debug(f"Error reading {tlog_file}: {e}")
        
        # Method 4: Check Greengrass config files
        config_files = [
            '/greengrass/v2/config/effectiveConfig.yaml',
            '/greengrass/v2/config/config.yaml'
        ]
        
        for config_file in config_files:
            if os.path.exists(config_file):
                try:
                    with open(config_file, 'r') as f:
                        content = f.read()
                        
                        # Look for thingName patterns
                        patterns = ['thingName:', 'thing-name:', 'THING_NAME:']
                        for pattern in patterns:
                            if pattern in content:
                                lines = content.split('\n')
                                for line in lines:
                                    if pattern in line and not line.strip().startswith('#'):
                                        # Extract the value after the colon
                                        parts = line.split(pattern)
                                        if len(parts) > 1:
                                            thing_name = parts[1].strip().strip('"\'')
                                            if thing_name and thing_name.startswith('gg_'):
                                                logger.info(f"Found Thing Name in {config_file}: {thing_name}")
                                                return thing_name
                except Exception as e:
                    logger.debug(f"Error reading {config_file}: {e}")
        
        # Method 5: Use MAC address as fallback - format without underscores
        try:
            import subprocess
            result = subprocess.run(['cat', '/sys/class/net/eth0/address'], 
                                  capture_output=True, text=True)
            if result.returncode == 0:
                mac = result.stdout.strip()
                # Use format without underscores (matches gg_525400123402)
                mac_no_underscores = mac.replace(':', '')
                thing_name_no_underscores = f"gg_{mac_no_underscores}"
                logger.info(f"Generated Thing Name from MAC (no underscores): {thing_name_no_underscores}")
                return thing_name_no_underscores
        except Exception as e:
            logger.debug(f"Failed to get MAC address: {e}")
        
        return "unknown"
    
    def refresh_thing_name(self):
        """Periodically refresh Thing Name in case it becomes available later"""
        if self.thing_name == "unknown" or not self.thing_name.startswith("gg_"):
            new_thing_name = self.get_thing_name()
            if new_thing_name != self.thing_name and new_thing_name.startswith("gg_"):
                logger.info(f"Thing Name updated from '{self.thing_name}' to '{new_thing_name}'")
                self.thing_name = new_thing_name
                return True
        return False
        
    def publish_to_iot_core(self, payload):
        """Publish message to AWS IoT Core via Greengrass IPC"""
        if not self.ipc_client:
            return False
            
        try:
            # Create the publish request
            request = PublishToIoTCoreRequest()
            request.topic_name = self.publish_topic
            request.payload = json.dumps(payload).encode('utf-8')
            request.qos = QOS.AT_LEAST_ONCE
            
            # Publish the message
            operation = self.ipc_client.new_publish_to_iot_core()
            operation.activate(request)
            future = operation.get_response()
            
            # Wait for response with timeout
            future.result(timeout=10.0)
            logger.info(f"Successfully published message to IoT Core topic: {self.publish_topic}")
            return True
            
        except Exception as e:
            logger.error(f"Failed to publish to IoT Core: {e}")
            return False
    
    def publish_hello_world(self, counter):
        """
        Publish a hello world message with platform information
        """
        try:
            # Periodically refresh Thing Name (every 10 messages)
            if counter % 10 == 0:
                self.refresh_thing_name()
            
            # Get platform information
            my_platform = platform.platform()
            
            # Create message payload
            message = {
                "message": self.message,
                "platform": my_platform,
                "timestamp": datetime.now().isoformat(),
                "counter": counter,
                "component": "com.example.HelloWorldPython",
                "version": "1.0.0",
                "device_id": self.thing_name,
                "thing_name": self.thing_name,
                "ipc_available": GREENGRASS_IPC_AVAILABLE
            }
            
            # Log the message (only first few times to avoid spam)
            if counter <= 3 or counter % 20 == 0:
                logger.info(f"Publishing message: {json.dumps(message, indent=2)}")
            
            # Try to publish to IoT Core
            if GREENGRASS_IPC_AVAILABLE and self.ipc_client:
                success = self.publish_to_iot_core(message)
                if success:
                    if counter <= 3 or counter % 20 == 0:
                        logger.info(f"Message published to IoT Core topic: {self.publish_topic}")
                else:
                    if counter <= 3:  # Only log first few failures
                        logger.warning(f"Failed to publish to IoT Core, message logged only")
            else:
                logger.info(f"IPC not available - message logged to topic: {self.publish_topic}")
            
        except Exception as e:
            logger.error(f"Failed to publish message: {repr(e)}")
    
    def run(self):
        """
        Main function that runs the hello world component
        """
        logger.info("=== Starting Hello World Greengrass Component ===")
        logger.info(f"Message: {self.message}")
        logger.info(f"Publish Topic: {self.publish_topic}")
        logger.info(f"Publish Interval: {self.publish_interval} seconds")
        logger.info(f"Greengrass IPC Available: {GREENGRASS_IPC_AVAILABLE}")
        logger.info(f"Final Thing Name: {self.thing_name}")
        
        counter = 0
        
        try:
            while True:
                counter += 1
                self.publish_hello_world(counter)
                # Wait before next message
                time.sleep(self.publish_interval)
                
        except KeyboardInterrupt:
            logger.info("Component stopped by user")
        except Exception as e:
            logger.error(f"Component error: {repr(e)}")
            raise
        finally:
            logger.info("Hello World Component stopped")

def main():
    """Main entry point"""
    try:
        component = HelloWorldComponent()
        component.run()
    except Exception as e:
        logger.error(f"Fatal error in Hello World Component: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()
