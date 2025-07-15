cmake_minimum_required(VERSION 3.0)

set( DEMO_NAME "defender_demo" )

# Find required packages
find_package(backoffalgorithm REQUIRED)
find_package(core_http REQUIRED)
find_package(core_mqtt REQUIRED)
find_package(core_json REQUIRED)
find_package(defender REQUIRED)

include(${CMAKE_CURRENT_SOURCE_DIR}/demos/logging-stack/logging.cmake)

file( GLOB DEMO_FILE "${DEMO_NAME}.c*" )

# Add source files
add_executable(${DEMO_NAME}
    ${CMAKE_CURRENT_SOURCE_DIR}/platform/posix/clock_posix.c
    ${CMAKE_CURRENT_SOURCE_DIR}/platform/posix/transport/src/plaintext_posix.c
    ${CMAKE_CURRENT_SOURCE_DIR}/platform/posix/transport/src/sockets_posix.c
    ${CMAKE_CURRENT_SOURCE_DIR}/platform/posix/transport/src/openssl_posix.c
    ${CMAKE_CURRENT_SOURCE_DIR}/demos/defender/defender_demo_json/${DEMO_NAME}.c
    ${CMAKE_CURRENT_SOURCE_DIR}/demos/defender/defender_demo_json/mqtt_operations.c
    ${CMAKE_CURRENT_SOURCE_DIR}/demos/defender/defender_demo_json/metrics_collector.c
    ${CMAKE_CURRENT_SOURCE_DIR}/demos/defender/defender_demo_json/report_builder.c
)

# Add include directories for platform specific files
target_include_directories(${DEMO_NAME} PRIVATE
    ${CMAKE_CURRENT_SOURCE_DIR}/demos/defender/defender_demo_json
    ${CMAKE_CURRENT_SOURCE_DIR}/demos/http/common/include
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
        core_http::core_http
        core_mqtt::core_mqtt
        core_json::core_json
        backoffalgorithm::backoffalgorithm
        defender::defender
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