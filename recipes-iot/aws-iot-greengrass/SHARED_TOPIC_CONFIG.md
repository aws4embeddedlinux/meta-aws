# Shared Topic Configuration

Both Greengrass components now publish to the same IoT Core topic for easy monitoring.

## Topic Configuration

**Shared Topic**: `hello`

Both components publish to this topic, making it easy to see both working together in IoT Core or CloudWatch.

## Component Messages

### Python Component (`com.example.HelloWorldPython`)
- **Topic**: `hello`
- **Message Format**: Simple string
- **Content**: `"Hello world! Sent from Greengrass Core."`
- **Interval**: 5 seconds
- **Permissions**: Publish only

### SDK Lite Component (`com.example.HelloWorldSDKLite`)
- **Topic**: `hello`
- **Message Format**: JSON
- **Content**: 
  ```json
  {
    "message": "Hello from Greengrass SDK Lite!",
    "component": "com.example.HelloWorldSDKLite",
    "version": "1.0.0",
    "counter": 123,
    "timestamp": "2025-08-06 20:45:30",
    "stats": {
      "successful_publishes": 45,
      "failed_publishes": 0
    }
  }
  ```
- **Interval**: 15 seconds
- **Permissions**: Publish and Subscribe

## Monitoring

To monitor both components publishing to the same topic:

### AWS IoT Core Console
1. Go to AWS IoT Core → Test → MQTT test client
2. Subscribe to topic: `hello`
3. You'll see messages from both components

### CloudWatch Logs
Both components log their publish activities to CloudWatch, making it easy to verify they're working.

### Device Logs
```bash
# Python component logs
journalctl -u ggl.com.example.HelloWorldPython.service -f

# SDK Lite component logs  
journalctl -u ggl.com.example.HelloWorldSDKLite.service -f
```

## Message Identification

You can easily distinguish between the components:
- **Python**: Simple string messages every 5 seconds
- **SDK Lite**: JSON messages with detailed stats every 15 seconds

This configuration makes it perfect for demonstrating both components working together on the same device!
