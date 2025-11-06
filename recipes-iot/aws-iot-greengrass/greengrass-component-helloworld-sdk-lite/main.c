//! Sample component demonstrating pubsub with AWS IoT Core using SDK Lite

#include <ggl/buffer.h>
#include <ggl/error.h>
#include <ggl/ipc/client.h>
#include <ggl/sdk.h>
#include <unistd.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <string.h>

static int message_counter = 0;
static int successful_publishes = 0;
static int failed_publishes = 0;

static void get_timestamp(char *buffer, size_t size) {
    time_t now = time(NULL);
    struct tm *tm_info = gmtime(&now);
    strftime(buffer, size, "%Y-%m-%dT%H:%M:%S.000Z", tm_info);
}

static void response_handler(void *ctx, GglBuffer topic, GglBuffer payload, GgIpcSubscriptionHandle handle) {
    (void) ctx;
    (void) handle;
    char timestamp[32];
    get_timestamp(timestamp, sizeof(timestamp));
    
    printf("[%s] [HelloWorldSDKLite] RECEIVED MESSAGE:\n", timestamp);
    printf("  Topic: [%.*s]\n", (int) topic.len, topic.data);
    printf("  Payload: [%.*s]\n", (int) payload.len, payload.data);
    printf("  Message Length: %zu bytes\n", payload.len);
    fflush(stdout);
}

static void log_publish_success(const char* topic, const char* message) {
    char timestamp[32];
    get_timestamp(timestamp, sizeof(timestamp));
    
    successful_publishes++;
    
    printf("[%s] [HelloWorldSDKLite] PUBLISH SUCCESS #%d:\n", timestamp, successful_publishes);
    printf("  Topic: %s\n", topic);
    printf("  Message: %s\n", message);
    printf("  Total Successful: %d\n", successful_publishes);
    printf("  Total Failed: %d\n", failed_publishes);
    printf("  Success Rate: %.1f%%\n", 
           (successful_publishes * 100.0) / (successful_publishes + failed_publishes));
    printf("  ----------------------------------------\n");
    fflush(stdout);
}

static void log_publish_failure(const char* topic, const char* message, GglError error) {
    char timestamp[32];
    get_timestamp(timestamp, sizeof(timestamp));
    
    failed_publishes++;
    
    printf("[%s] [HelloWorldSDKLite] PUBLISH FAILED #%d:\n", timestamp, failed_publishes);
    printf("  Topic: %s\n", topic);
    printf("  Message: %s\n", message);
    printf("  Error Code: %d\n", error);
    printf("  Total Successful: %d\n", successful_publishes);
    printf("  Total Failed: %d\n", failed_publishes);
    if (successful_publishes + failed_publishes > 0) {
        printf("  Success Rate: %.1f%%\n", 
               (successful_publishes * 100.0) / (successful_publishes + failed_publishes));
    }
    printf("  ----------------------------------------\n");
    fflush(stdout);
}

int main(void) {
    char timestamp[32];
    get_timestamp(timestamp, sizeof(timestamp));
    
    setvbuf(stdout, NULL, _IONBF, 0);

    printf("[%s] [HelloWorldSDKLite] COMPONENT STARTING\n", timestamp);
    printf("  Component: com.example.HelloWorldSDKLite\n");
    printf("  Version: 1.0.0\n");
    printf("  Binary: hello-world-sdk-lite\n");
    printf("  ========================================\n");
    fflush(stdout);

    ggl_sdk_init();

    GglError ret = ggipc_connect();
    if (ret != GGL_ERR_OK) {
        get_timestamp(timestamp, sizeof(timestamp));
        printf("[%s] [HelloWorldSDKLite] FATAL: Failed to connect to GG nucleus (error: %d)\n", timestamp, ret);
        fflush(stderr);
        exit(1);
    }
    
    get_timestamp(timestamp, sizeof(timestamp));
    printf("[%s] [HelloWorldSDKLite] Connected to GG nucleus successfully\n", timestamp);
    fflush(stdout);

    GgIpcSubscriptionHandle sub_handle;
    ret = ggipc_subscribe_to_iot_core(GGL_STR("hello"), 0, &response_handler, NULL, &sub_handle);
    if (ret != GGL_ERR_OK) {
        get_timestamp(timestamp, sizeof(timestamp));
        printf("[%s] [HelloWorldSDKLite] WARNING: Failed to subscribe to IoT Core topic 'hello' (error: %d)\n", timestamp, ret);
        printf("  Continuing with publish-only mode...\n");
        fflush(stdout);
    } else {
        get_timestamp(timestamp, sizeof(timestamp));
        printf("[%s] [HelloWorldSDKLite] Successfully subscribed to IoT Core topic 'hello'\n", timestamp);
        fflush(stdout);
    }

    get_timestamp(timestamp, sizeof(timestamp));
    printf("[%s] [HelloWorldSDKLite] Starting publish loop (15 second intervals)\n", timestamp);
    fflush(stdout);

    while (true) {
        message_counter++;
        
        // Create detailed message with metadata
        char message[512];
        get_timestamp(timestamp, sizeof(timestamp));
        snprintf(message, sizeof(message), 
                "{"
                "\"message\":\"Hello from Greengrass SDK Lite!\","
                "\"component\":\"com.example.HelloWorldSDKLite\","
                "\"version\":\"1.0.0\","
                "\"counter\":%d,"
                "\"timestamp\": \"%s\","
                "\"successful_publishes\":%d,"
                "\"failed_publishes\":%d"
                "}", 
                message_counter, timestamp, successful_publishes, failed_publishes);

        ret = ggipc_publish_to_iot_core(GGL_STR("hello"), (GglBuffer){.data = (uint8_t*)message, .len = strlen(message)}, 0);
        if (ret != GGL_ERR_OK) {
            log_publish_failure("hello", message, ret);
        } else {
            log_publish_success("hello", message);
        }

        // Log periodic statistics
        if (message_counter % 10 == 0) {
            get_timestamp(timestamp, sizeof(timestamp));
            printf("[%s] [HelloWorldSDKLite] PERIODIC STATS:\n", timestamp);
            printf("  Messages Sent: %d\n", message_counter);
            printf("  Successful Publishes: %d\n", successful_publishes);
            printf("  Failed Publishes: %d\n", failed_publishes);
            if (message_counter > 0) {
                printf("  Overall Success Rate: %.1f%%\n", 
                       (successful_publishes * 100.0) / message_counter);
            }
            printf("  ========================================\n");
            fflush(stdout);
        }

        sleep(15);
    }
}
