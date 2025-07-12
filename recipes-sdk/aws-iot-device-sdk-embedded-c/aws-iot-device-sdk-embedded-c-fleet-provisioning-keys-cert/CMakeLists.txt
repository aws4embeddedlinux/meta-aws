cmake_minimum_required(VERSION 3.0)
set( DEMO_NAME "fleet_provisioning_keys_cert" )

# Find required packages
find_package(backoffalgorithm REQUIRED)

find_package(core_mqtt REQUIRED)

find_package(fleetprovisioning REQUIRED)

include(${CMAKE_CURRENT_SOURCE_DIR}/demos/logging-stack/logging.cmake)

### tincycbor

add_library(tinycbor STATIC
    "${CMAKE_CURRENT_SOURCE_DIR}/libraries/3rdparty/tinycbor/src/cborpretty.c"
    "${CMAKE_CURRENT_SOURCE_DIR}/libraries/3rdparty/tinycbor/src/cborpretty_stdio.c"
    "${CMAKE_CURRENT_SOURCE_DIR}/libraries/3rdparty/tinycbor/src/cborencoder.c"
    "${CMAKE_CURRENT_SOURCE_DIR}/libraries/3rdparty/tinycbor/src/cborencoder_close_container_checked.c"
    "${CMAKE_CURRENT_SOURCE_DIR}/libraries/3rdparty/tinycbor/src/cborerrorstrings.c"
    "${CMAKE_CURRENT_SOURCE_DIR}/libraries/3rdparty/tinycbor/src/cborparser.c"
    "${CMAKE_CURRENT_SOURCE_DIR}/libraries/3rdparty/tinycbor/src/cborparser_dup_string.c"
)

set_target_properties(tinycbor PROPERTIES
    ARCHIVE_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/lib
    POSITION_INDEPENDENT_CODE ON
)

# Use C99 for tinycbor as it is incompatible with C90
if(CMAKE_C_STANDARD LESS 99)
    set_target_properties(tinycbor PROPERTIES C_STANDARD 99)
endif()

target_include_directories(tinycbor PUBLIC
    "${CMAKE_CURRENT_SOURCE_DIR}/libraries/3rdparty/tinycbor/src"
)

### DEMO

file( GLOB DEMO_FILE "${DEMO_NAME}.c*" )

# Add source files
add_executable(${DEMO_NAME}
    ${CMAKE_CURRENT_SOURCE_DIR}/demos/fleet_provisioning/${DEMO_NAME}/${DEMO_NAME}_demo.c
    ${CMAKE_CURRENT_SOURCE_DIR}/platform/posix/clock_posix.c
    ${CMAKE_CURRENT_SOURCE_DIR}/platform/posix/transport/src/plaintext_posix.c
    ${CMAKE_CURRENT_SOURCE_DIR}/platform/posix/transport/src/sockets_posix.c
    ${CMAKE_CURRENT_SOURCE_DIR}/platform/posix/transport/src/openssl_posix.c
)

# Add include directories for platform specific files
target_include_directories(${DEMO_NAME} PRIVATE
    ${CMAKE_CURRENT_SOURCE_DIR}/demos/fleet_provisioning/${DEMO_NAME}
    ${CMAKE_CURRENT_SOURCE_DIR}/demos/http/common/include
    ${CMAKE_CURRENT_SOURCE_DIR}/platform/posix/transport/include
    ${CMAKE_CURRENT_SOURCE_DIR}/platform/include
    PUBLIC
        ${CMAKE_CURRENT_LIST_DIR}
        ${LOGGING_INCLUDE_DIRS}
)

# Link libraries
target_link_libraries(${DEMO_NAME}
    PRIVATE
       pthread
       ssl
       crypto
       backoffalgorithm::backoffalgorithm
       core_mqtt::core_mqtt
       fleetprovisioning::fleetprovisioning
       tinycbor
)

# Add compile definitions
target_compile_definitions(${DEMO_NAME} PRIVATE
    DOWNLOADED_CERT_WRITE_PATH=""
    DOWNLOADED_PRIVATE_KEY_WRITE_PATH=""
    AWS_IOT_ENDPOINT=""
    ROOT_CA_CERT_PATH=""
    CLAIM_CERT_PATH=""
    CLAIM_PRIVATE_KEY_PATH=""
    PROVISIONING_TEMPLATE_NAME=""
    DEVICE_SERIAL_NUMBER=""
    CSR_SUBJECT_NAME=""
    CLIENT_IDENTIFIER=""
    OS_NAME="Yocto"
    OS_VERSION="Yocto"
    HARDWARE_PLATFORM_NAME="Yocto"
)
