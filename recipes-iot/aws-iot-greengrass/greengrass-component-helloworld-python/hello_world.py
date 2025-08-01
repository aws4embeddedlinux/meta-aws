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
        # Default configuration - Greengrass will override via environment or other mechanisms
        self.message = "Hello world! Sent from Greengrass Core."
        self.publish_topic = "hello/world/builtin"
        self.publish_interval = 5
        
        # Initialize IPC client
        self.ipc_client = None
        if GREENGRASS_IPC_AVAILABLE:
            try:
                self.ipc_client = awsiot.greengrasscoreipc.connect()
                logger.info("Successfully connected to Greengrass IPC")
            except Exception as e:
                logger.error(f"Failed to connect to Greengrass IPC: {e}")
                self.ipc_client = None
        
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
            # Get platform information
            my_platform = platform.platform()
            
            # Create message payload
            message = {
                "message": self.message,
                "platform": my_platform,
                "timestamp": datetime.now().isoformat(),
                "counter": counter,
                "component": "com.example.HelloWorld",
                "version": "1.0.0",
                "device_id": os.environ.get('AWS_IOT_THING_NAME', 'unknown'),
                "ipc_available": GREENGRASS_IPC_AVAILABLE
            }
            
            # Log the message
            logger.info(f"Publishing message: {json.dumps(message, indent=2)}")
            
            # Try to publish to IoT Core
            if GREENGRASS_IPC_AVAILABLE and self.ipc_client:
                success = self.publish_to_iot_core(message)
                if success:
                    logger.info(f"Message published to IoT Core topic: {self.publish_topic}")
                else:
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
        logger.info(f"Thing Name: {os.environ.get('AWS_IOT_THING_NAME', 'Not set')}")
        
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
