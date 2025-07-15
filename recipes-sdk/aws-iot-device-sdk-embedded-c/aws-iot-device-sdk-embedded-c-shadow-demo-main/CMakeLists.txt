cmake_minimum_required(VERSION 3.0)

set( DEMO_NAME "shadow_demo_main" )

# Find required packages
find_package(backoffalgorithm REQUIRED)
find_package(shadow REQUIRED)
find_package(core_mqtt REQUIRED)
find_package(core_json REQUIRED)

include(${CMAKE_CURRENT_SOURCE_DIR}/demos/logging-stack/logging.cmake)

file( GLOB DEMO_FILE "${DEMO_NAME}.c*" )

# Add source files
add_executable(${DEMO_NAME}
    ${CMAKE_CURRENT_SOURCE_DIR}/platform/posix/clock_posix.c
    ${CMAKE_CURRENT_SOURCE_DIR}/platform/posix/transport/src/plaintext_posix.c
    ${CMAKE_CURRENT_SOURCE_DIR}/platform/posix/transport/src/sockets_posix.c
    ${CMAKE_CURRENT_SOURCE_DIR}/platform/posix/transport/src/openssl_posix.c
    ${CMAKE_CURRENT_SOURCE_DIR}/demos/shadow/shadow_demo_main/${DEMO_NAME}.c
    ${CMAKE_CURRENT_SOURCE_DIR}/demos/shadow/shadow_demo_main/shadow_demo_helpers.c
)

# Add include directories for platform specific files
target_include_directories(${DEMO_NAME} PRIVATE
    ${CMAKE_CURRENT_SOURCE_DIR}/demos/shadow/shadow_demo_main
    ${CMAKE_CURRENT_SOURCE_DIR}/demos/include
    ${CMAKE_CURRENT_SOURCE_DIR}/platform/posix/transport/include
    ${CMAKE_CURRENT_SOURCE_DIR}/platform/include
    PUBLIC
        ${CMAKE_CURRENT_LIST_DIR}
        ${LOGGING_INCLUDE_DIRS}
)


# Link libraries
target_link_libraries(${DEMO_NAME}
    PRIVATE
        shadow::shadow
        core_mqtt::core_mqtt
        core_json::core_json
        backoffalgorithm::backoffalgorithm
        pthread
        ssl
        crypto
)

# Add compile definitions
target_compile_definitions(${DEMO_NAME} PRIVATE
    AWS_IOT_ENDPOINT=""
    ROOT_CA_CERT_PATH="/etc/ssl/certs/ca-certificates.crt"
    CLIENT_PRIVATE_KEY_PATH=""
    CLIENT_CERT_PATH=""
    THING_NAME=""
    CLIENT_IDENTIFIER=""
    OS_NAME="Yocto"
    OS_VERSION="Yocto"
)
