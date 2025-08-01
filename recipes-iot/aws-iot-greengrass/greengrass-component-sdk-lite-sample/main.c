#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <ggl/sdk.h>
#include <ggl/ipc/client.h>
#include <ggl/object.h>
#include <ggl/buffer.h>
#include <ggl/error.h>

static void log_message(const char* message) {
    printf("[HelloWorld] %s\n", message);
    fflush(stdout);
}

static GglError publish_hello_message(void) {
    GglError ret = GGL_ERR_OK;
    
    // Create a simple JSON message
    const char* message = "{\"message\": \"Hello from Greengrass component!\", \"timestamp\": 1234567890}";
    GglBuffer message_buf = ggl_buffer_from_str(message);
    
    // Topic to publish to
    const char* topic = "hello/world";
    GglBuffer topic_buf = ggl_buffer_from_str(topic);
    
    log_message("Publishing hello message to local topic");
    
    // Publish to local topic using Greengrass IPC
    ret = ggl_ipc_publish_to_topic(topic_buf, message_buf);
    if (ret != GGL_ERR_OK) {
        printf("[HelloWorld] Failed to publish message: %d\n", ret);
        return ret;
    }
    
    log_message("Message published successfully");
    return GGL_ERR_OK;
}

static GglError get_component_config(void) {
    GglError ret = GGL_ERR_OK;
    
    // Get component configuration
    const char* config_path = "componentName";
    GglBuffer config_path_buf = ggl_buffer_from_str(config_path);
    
    GglObject config_value;
    ret = ggl_ipc_get_config(config_path_buf, &config_value);
    if (ret != GGL_ERR_OK) {
        printf("[HelloWorld] Failed to get config: %d\n", ret);
        return ret;
    }
    
    if (config_value.type == GGL_OBJ_TYPE_STR) {
        printf("[HelloWorld] Component name from config: %.*s\n", 
               (int)config_value.str.len, config_value.str.data);
    }
    
    return GGL_ERR_OK;
}

int main(int argc, char* argv[]) {
    (void)argc;
    (void)argv;
    
    GglError ret = GGL_ERR_OK;
    
    log_message("Starting Hello World Greengrass component");
    
    // Initialize the Greengrass SDK
    ret = ggl_init();
    if (ret != GGL_ERR_OK) {
        printf("[HelloWorld] Failed to initialize GGL SDK: %d\n", ret);
        return EXIT_FAILURE;
    }
    
    log_message("GGL SDK initialized successfully");
    
    // Try to get component configuration
    ret = get_component_config();
    if (ret != GGL_ERR_OK) {
        log_message("Warning: Could not retrieve component configuration");
    }
    
    // Main component loop
    int message_count = 0;
    while (message_count < 10) {
        // Publish a hello message
        ret = publish_hello_message();
        if (ret != GGL_ERR_OK) {
            log_message("Warning: Failed to publish message");
        }
        
        message_count++;
        
        // Wait 30 seconds before next message
        log_message("Waiting 30 seconds before next message...");
        sleep(30);
    }
    
    log_message("Hello World component completed successfully");
    return EXIT_SUCCESS;
}
