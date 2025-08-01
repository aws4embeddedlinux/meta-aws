#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>
#include <ggl/sdk.h>
#include <ggl/buffer.h>
#include <ggl/object.h>
#include <ggl/error.h>

// Test counter
static int test_count = 0;
static int passed_count = 0;

#define TEST_ASSERT(condition, message) do { \
    test_count++; \
    if (condition) { \
        printf("  ✓ %s\n", message); \
        passed_count++; \
    } else { \
        printf("  ✗ %s\n", message); \
    } \
} while(0)

void test_message_creation(void) {
    printf("Testing message creation...\n");
    
    // Test creating a simple JSON message like our component does
    const char* expected_message = "{\"message\": \"Hello from Greengrass component!\", \"timestamp\": 1234567890}";
    GglBuffer message_buf = ggl_buffer_from_str(expected_message);
    
    TEST_ASSERT(message_buf.data != NULL, "Message buffer should not be NULL");
    TEST_ASSERT(message_buf.len > 0, "Message buffer should have positive length");
    TEST_ASSERT(message_buf.len == strlen(expected_message), "Message buffer length should match string length");
    
    // Verify message content
    TEST_ASSERT(strncmp((char*)message_buf.data, "{\"message\":", 11) == 0, 
                "Message should start with JSON object");
}

void test_topic_creation(void) {
    printf("Testing topic creation...\n");
    
    // Test creating topic buffer like our component does
    const char* topic = "hello/world";
    GglBuffer topic_buf = ggl_buffer_from_str(topic);
    
    TEST_ASSERT(topic_buf.data != NULL, "Topic buffer should not be NULL");
    TEST_ASSERT(topic_buf.len == strlen(topic), "Topic buffer length should match string length");
    TEST_ASSERT(strncmp((char*)topic_buf.data, "hello/world", topic_buf.len) == 0, 
                "Topic buffer should contain correct topic");
}

void test_config_path_creation(void) {
    printf("Testing config path creation...\n");
    
    // Test creating config path like our component does
    const char* config_path = "componentName";
    GglBuffer config_path_buf = ggl_buffer_from_str(config_path);
    
    TEST_ASSERT(config_path_buf.data != NULL, "Config path buffer should not be NULL");
    TEST_ASSERT(config_path_buf.len == strlen(config_path), "Config path buffer length should match");
    TEST_ASSERT(strncmp((char*)config_path_buf.data, "componentName", config_path_buf.len) == 0,
                "Config path buffer should contain correct path");
}

void test_error_handling(void) {
    printf("Testing error handling...\n");
    
    // Test that error codes are properly defined
    TEST_ASSERT(GGL_ERR_OK == 0, "GGL_ERR_OK should be 0 for success");
    TEST_ASSERT(GGL_ERR_FAILURE != 0, "GGL_ERR_FAILURE should be non-zero");
    
    // Test error code comparison
    GglError success = GGL_ERR_OK;
    GglError failure = GGL_ERR_FAILURE;
    
    TEST_ASSERT(success == GGL_ERR_OK, "Success should equal GGL_ERR_OK");
    TEST_ASSERT(failure != GGL_ERR_OK, "Failure should not equal GGL_ERR_OK");
}

void test_object_handling(void) {
    printf("Testing object handling...\n");
    
    // Test creating objects like our component might receive from config
    GglObject test_obj;
    
    // Test string object (like component name from config)
    const char* component_name = "com.example.HelloWorld";
    test_obj.type = GGL_OBJ_TYPE_STR;
    test_obj.str = ggl_buffer_from_str(component_name);
    
    TEST_ASSERT(test_obj.type == GGL_OBJ_TYPE_STR, "Object should have string type");
    TEST_ASSERT(test_obj.str.data != NULL, "String object should have valid data");
    TEST_ASSERT(test_obj.str.len == strlen(component_name), "String object should have correct length");
    
    // Test integer object (like message count)
    test_obj.type = GGL_OBJ_TYPE_I64;
    test_obj.i64 = 10;
    
    TEST_ASSERT(test_obj.type == GGL_OBJ_TYPE_I64, "Object should have integer type");
    TEST_ASSERT(test_obj.i64 == 10, "Integer object should store correct value");
}

void test_buffer_operations(void) {
    printf("Testing buffer operations used by component...\n");
    
    // Test operations that our component performs
    const char* test_strings[] = {
        "hello/world",
        "componentName", 
        "{\"message\": \"test\"}",
        "com.example.HelloWorld"
    };
    
    int num_strings = sizeof(test_strings) / sizeof(test_strings[0]);
    
    for (int i = 0; i < num_strings; i++) {
        GglBuffer buf = ggl_buffer_from_str(test_strings[i]);
        
        TEST_ASSERT(buf.data != NULL, "Buffer should have valid data pointer");
        TEST_ASSERT(buf.len == strlen(test_strings[i]), "Buffer length should match string length");
        TEST_ASSERT(memcmp(buf.data, test_strings[i], buf.len) == 0, "Buffer content should match string");
    }
}

int main(void) {
    printf("=== Example Greengrass Component Unit Tests ===\n\n");
    
    test_message_creation();
    test_topic_creation();
    test_config_path_creation();
    test_error_handling();
    test_object_handling();
    test_buffer_operations();
    
    printf("\n=== Test Results ===\n");
    printf("Total tests: %d\n", test_count);
    printf("Passed: %d\n", passed_count);
    printf("Failed: %d\n", test_count - passed_count);
    
    if (passed_count == test_count) {
        printf("All component unit tests passed!\n");
        return 0;
    } else {
        printf("Some component unit tests failed!\n");
        return 1;
    }
}
