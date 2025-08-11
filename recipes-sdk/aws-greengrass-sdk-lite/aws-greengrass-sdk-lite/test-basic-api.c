#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>
#include <ggl/error.h>
#include <ggl/buffer.h>
#include <ggl/object.h>

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

void test_error_codes(void) {
    printf("Testing error codes...\n");
    
    // Test that error codes are defined
    TEST_ASSERT(GGL_ERR_OK == 0, "GGL_ERR_OK should be 0");
    TEST_ASSERT(GGL_ERR_FAILURE != 0, "GGL_ERR_FAILURE should not be 0");
    TEST_ASSERT(GGL_ERR_INVALID != 0, "GGL_ERR_INVALID should not be 0");
}

void test_buffer_creation(void) {
    printf("Testing buffer creation...\n");
    
    // Test buffer from null-terminated string
    char test_str[] = "Hello, World!";
    GglBuffer buf = ggl_buffer_from_null_term(test_str);
    
    TEST_ASSERT(buf.data != NULL, "Buffer data should not be NULL");
    TEST_ASSERT(buf.len == strlen(test_str), "Buffer length should match string length");
    TEST_ASSERT(memcmp(buf.data, test_str, buf.len) == 0, "Buffer content should match string");
    
    // Test empty buffer
    char empty_str[] = "";
    GglBuffer empty_buf = ggl_buffer_from_null_term(empty_str);
    TEST_ASSERT(empty_buf.len == 0, "Empty buffer should have zero length");
}

void test_object_types(void) {
    printf("Testing object types...\n");
    
    // Test that object types are defined
    TEST_ASSERT(GGL_TYPE_NULL >= 0, "GGL_TYPE_NULL should be defined");
    TEST_ASSERT(GGL_TYPE_BOOLEAN >= 0, "GGL_TYPE_BOOLEAN should be defined");
    TEST_ASSERT(GGL_TYPE_I64 >= 0, "GGL_TYPE_I64 should be defined");
    TEST_ASSERT(GGL_TYPE_F64 >= 0, "GGL_TYPE_F64 should be defined");
    TEST_ASSERT(GGL_TYPE_BUF >= 0, "GGL_TYPE_BUF should be defined");
    TEST_ASSERT(GGL_TYPE_MAP >= 0, "GGL_TYPE_MAP should be defined");
    TEST_ASSERT(GGL_TYPE_LIST >= 0, "GGL_TYPE_LIST should be defined");
}

void test_buffer_operations(void) {
    printf("Testing buffer operations...\n");
    
    char str1[] = "Hello";
    char str2[] = "Hello";
    char str3[] = "World";
    
    GglBuffer buf1 = ggl_buffer_from_null_term(str1);
    GglBuffer buf2 = ggl_buffer_from_null_term(str2);
    GglBuffer buf3 = ggl_buffer_from_null_term(str3);
    
    // Test buffer equality
    TEST_ASSERT(buf1.len == buf2.len && memcmp(buf1.data, buf2.data, buf1.len) == 0, 
                "Equal buffers should compare equal");
    TEST_ASSERT(!(buf1.len == buf3.len && memcmp(buf1.data, buf3.data, buf1.len) == 0), 
                "Different buffers should not compare equal");
}

void test_object_creation(void) {
    printf("Testing object creation...\n");
    
    // Test null object
    GglObject null_obj = GGL_OBJ_NULL;
    TEST_ASSERT(ggl_obj_type(null_obj) == GGL_TYPE_NULL, "Null object should have NULL type");
    
    // Test boolean object
    GglObject bool_obj = ggl_obj_bool(true);
    TEST_ASSERT(ggl_obj_type(bool_obj) == GGL_TYPE_BOOLEAN, "Boolean object should have BOOLEAN type");
    TEST_ASSERT(ggl_obj_into_bool(bool_obj) == true, "Boolean object should store true value");
    
    // Test integer object
    GglObject int_obj = ggl_obj_i64(42);
    TEST_ASSERT(ggl_obj_type(int_obj) == GGL_TYPE_I64, "Integer object should have I64 type");
    TEST_ASSERT(ggl_obj_into_i64(int_obj) == 42, "Integer object should store correct value");
    
    // Test float object
    GglObject float_obj = ggl_obj_f64(3.14159);
    TEST_ASSERT(ggl_obj_type(float_obj) == GGL_TYPE_F64, "Float object should have F64 type");
    double f_val = ggl_obj_into_f64(float_obj);
    TEST_ASSERT(f_val > 3.14 && f_val < 3.15, "Float object should store approximate value");
}

int main(void) {
    printf("=== AWS Greengrass SDK Lite Basic API Tests ===\n\n");
    
    test_error_codes();
    test_buffer_creation();
    test_object_types();
    test_buffer_operations();
    test_object_creation();
    
    printf("\n=== Test Results ===\n");
    printf("Total tests: %d\n", test_count);
    printf("Passed: %d\n", passed_count);
    printf("Failed: %d\n", test_count - passed_count);
    
    if (passed_count == test_count) {
        printf("All basic API tests passed!\n");
        return 0;
    } else {
        printf("Some basic API tests failed!\n");
        return 1;
    }
}
