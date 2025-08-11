#include <stdio.h>
#include <stdint.h>

int main() {
    uint32_t test_val = 0x12345678;
    uint8_t *bytes = (uint8_t*)&test_val;
    
    printf("Testing endianness...\n");
    printf("Test value: 0x%08x\n", test_val);
    printf("Byte order: 0x%02x 0x%02x 0x%02x 0x%02x\n", 
           bytes[0], bytes[1], bytes[2], bytes[3]);
    
    if (bytes[0] == 0x78) {
        printf("Little-endian detected\n");
    } else if (bytes[0] == 0x12) {
        printf("Big-endian detected\n");
    } else {
        printf("Unknown endianness\n");
        return 1;
    }
    
    return 0;
}
