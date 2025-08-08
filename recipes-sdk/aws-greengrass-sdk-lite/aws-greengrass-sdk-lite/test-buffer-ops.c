#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>
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

void test_buffer_creation(void) {
    printf("Testing buffer creation...\n");
    
    char test_str[] = "Hello, World!";
    GglBuffer buf = ggl_buffer_from_null_term(test_str);
    
    TEST_ASSERT(buf.data != NULL, "Buffer should have valid data pointer");
    TEST_ASSERT(buf.len == strlen(test_str), "Buffer length should be correct");
    
    // Test buffer content access
    if (buf.len > 0) {
        TEST_ASSERT(buf.data[0] == 'H', "First character should be 'H'");
    }
    
    if (buf.len > 6) {
        TEST_ASSERT(buf.data[6] == ' ', "Character at index 6 should be space");
    }
}

void test_buffer_comparison(void) {
    printf("Testing buffer comparison...\n");
    
    char str1[] = "identical";
    char str2[] = "identical";
    char str3[] = "different";
    
    GglBuffer buf1 = ggl_buffer_from_null_term(str1);
    GglBuffer buf2 = ggl_buffer_from_null_term(str2);
    GglBuffer buf3 = ggl_buffer_from_null_term(str3);
    
    // Manual comparison
    bool buf1_eq_buf2 = (buf1.len == buf2.len) && (memcmp(buf1.data, buf2.data, buf1.len) == 0);
    bool buf1_eq_buf3 = (buf1.len == buf3.len) && (memcmp(buf1.data, buf3.data, buf1.len) == 0);
    
    TEST_ASSERT(buf1_eq_buf2, "Identical buffers should compare equal");
    TEST_ASSERT(!buf1_eq_buf3, "Different buffers should not compare equal");
}

void test_empty_buffers(void) {
    printf("Testing empty buffers...\n");
    
    char empty_str[] = "";
    GglBuffer empty_buf = ggl_buffer_from_null_term(empty_str);
    
    TEST_ASSERT(empty_buf.len == 0, "Empty string buffer should have zero length");
}

void test_buffer_bounds(void) {
    printf("Testing buffer bounds...\n");
    
    char test_str[] = "ABCDEFGHIJ";
    GglBuffer buf = ggl_buffer_from_null_term(test_str);
    
    TEST_ASSERT(buf.len == 10, "Test string should have length 10");
    
    // Test accessing valid indices
    if (buf.len > 0) {
        TEST_ASSERT(buf.data[0] == 'A', "First character should be 'A'");
        TEST_ASSERT(buf.data[buf.len - 1] == 'J', "Last character should be 'J'");
    }
    
    // Test that we don't access beyond bounds
    TEST_ASSERT(buf.len <= strlen(test_str), "Buffer length should not exceed string length");
}

void test_buffer_substr(void) {
    printf("Testing buffer substring operations...\n");
    
    char test_str[] = "Hello, World!";
    GglBuffer buf = ggl_buffer_from_null_term(test_str);
    
    // Test substring from beginning
    GglBuffer substr1 = ggl_buffer_substr(buf, 0, 5);
    TEST_ASSERT(substr1.len == 5, "Substring should have correct length");
    TEST_ASSERT(memcmp(substr1.data, "Hello", 5) == 0, "Substring should have correct content");
    
    // Test substring from middle
    GglBuffer substr2 = ggl_buffer_substr(buf, 7, 5);
    TEST_ASSERT(substr2.len == 5, "Middle substring should have correct length");
    TEST_ASSERT(memcmp(substr2.data, "World", 5) == 0, "Middle substring should have correct content");
}

int main(void) {
    printf("=== AWS Greengrass SDK Lite Buffer Operations Tests ===\n\n");
    
    test_buffer_creation();
    test_buffer_comparison();
    test_empty_buffers();
    test_buffer_bounds();
    test_buffer_substr();
    
    printf("\n=== Test Results ===\n");
    printf("Total tests: %d\n", test_count);
    printf("Passed: %d\n", passed_count);
    printf("Failed: %d\n", test_count - passed_count);
    
    if (passed_count == test_count) {
        printf("All buffer operations tests passed!\n");
        return 0;
    } else {
        printf("Some buffer operations tests failed!\n");
        return 1;
    }
}
