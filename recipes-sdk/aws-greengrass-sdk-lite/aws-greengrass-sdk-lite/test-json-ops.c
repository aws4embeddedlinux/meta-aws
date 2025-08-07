#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>
#include <ggl/object.h>
#include <ggl/buffer.h>

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

void test_buffer_objects(void) {
    printf("Testing buffer objects...\n");
    
    char test_str[] = "Hello, JSON!";
    GglBuffer str_buf = ggl_buffer_from_null_term(test_str);
    
    GglObject buf_obj = ggl_obj_buf(str_buf);
    
    TEST_ASSERT(ggl_obj_type(buf_obj) == GGL_TYPE_BUF, "Buffer object should have BUF type");
    
    GglBuffer retrieved_buf = ggl_obj_into_buf(buf_obj);
    TEST_ASSERT(retrieved_buf.len == strlen(test_str), "Retrieved buffer should have correct length");
    TEST_ASSERT(memcmp(retrieved_buf.data, test_str, retrieved_buf.len) == 0, 
                "Retrieved buffer should contain correct data");
}

void test_object_type_checking(void) {
    printf("Testing object type checking...\n");
    
    // Create objects of different types and verify type checking
    GglObject objects[] = {
        GGL_OBJ_NULL,
        ggl_obj_bool(false),
        ggl_obj_i64(-123),
        ggl_obj_f64(-2.718)
    };
    
    GglObjectType expected_types[] = {
        GGL_TYPE_NULL,
        GGL_TYPE_BOOLEAN,
        GGL_TYPE_I64,
        GGL_TYPE_F64
    };
    
    int obj_count = sizeof(objects) / sizeof(objects[0]);
    
    for (int i = 0; i < obj_count; i++) {
        GglObjectType actual_type = ggl_obj_type(objects[i]);
        TEST_ASSERT(actual_type == expected_types[i], "Object type should match expected");
    }
}

void test_boolean_operations(void) {
    printf("Testing boolean operations...\n");
    
    GglObject true_obj = ggl_obj_bool(true);
    GglObject false_obj = ggl_obj_bool(false);
    
    TEST_ASSERT(ggl_obj_into_bool(true_obj) == true, "True object should return true");
    TEST_ASSERT(ggl_obj_into_bool(false_obj) == false, "False object should return false");
    
    TEST_ASSERT(ggl_obj_type(true_obj) == GGL_TYPE_BOOLEAN, "True object should have boolean type");
    TEST_ASSERT(ggl_obj_type(false_obj) == GGL_TYPE_BOOLEAN, "False object should have boolean type");
}

void test_numeric_operations(void) {
    printf("Testing numeric operations...\n");
    
    // Test integer operations
    GglObject int_obj = ggl_obj_i64(12345);
    TEST_ASSERT(ggl_obj_type(int_obj) == GGL_TYPE_I64, "Integer object should have I64 type");
    TEST_ASSERT(ggl_obj_into_i64(int_obj) == 12345, "Integer object should store correct value");
    
    // Test negative integer
    GglObject neg_int_obj = ggl_obj_i64(-9876);
    TEST_ASSERT(ggl_obj_into_i64(neg_int_obj) == -9876, "Negative integer should be stored correctly");
    
    // Test float operations
    GglObject float_obj = ggl_obj_f64(123.456);
    TEST_ASSERT(ggl_obj_type(float_obj) == GGL_TYPE_F64, "Float object should have F64 type");
    double f_val = ggl_obj_into_f64(float_obj);
    TEST_ASSERT(f_val > 123.4 && f_val < 123.5, "Float object should store approximate value");
}

int main(void) {
    printf("=== AWS Greengrass SDK Lite JSON Operations Tests ===\n\n");
    
    test_object_creation();
    test_buffer_objects();
    test_object_type_checking();
    test_boolean_operations();
    test_numeric_operations();
    
    printf("\n=== Test Results ===\n");
    printf("Total tests: %d\n", test_count);
    printf("Passed: %d\n", passed_count);
    printf("Failed: %d\n", test_count - passed_count);
    
    if (passed_count == test_count) {
        printf("All JSON operations tests passed!\n");
        return 0;
    } else {
        printf("Some JSON operations tests failed!\n");
        return 1;
    }
}
