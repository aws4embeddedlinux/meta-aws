cmake_minimum_required(VERSION 3.0)
set( DEMO_NAME "http_demo_basic_tls" )

# Find required packages
find_package(core_http REQUIRED)
find_package(OpenSSL REQUIRED)
find_package(backoffalgorithm REQUIRED)

include(${CMAKE_CURRENT_SOURCE_DIR}/demos/logging-stack/logging.cmake)

file( GLOB DEMO_FILE "${DEMO_NAME}.c*" )

# Add source files
add_executable(${DEMO_NAME}
    ${CMAKE_CURRENT_SOURCE_DIR}/platform/posix/clock_posix.c
    ${CMAKE_CURRENT_SOURCE_DIR}/platform/posix/transport/src/plaintext_posix.c
    ${CMAKE_CURRENT_SOURCE_DIR}/platform/posix/transport/src/sockets_posix.c
    ${CMAKE_CURRENT_SOURCE_DIR}/platform/posix/transport/src/openssl_posix.c
    ${CMAKE_CURRENT_SOURCE_DIR}/demos/http/common/src/http_demo_utils.c
    ${CMAKE_CURRENT_SOURCE_DIR}/demos/http/common/src/http_demo_url_utils.c
    ${CMAKE_CURRENT_SOURCE_DIR}/demos/http/${DEMO_NAME}/${DEMO_NAME}.c
)

# Add include directories for platform specific files
target_include_directories(${DEMO_NAME} PRIVATE
    ${CMAKE_CURRENT_SOURCE_DIR}/demos/http/${DEMO_NAME}
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
        core_http::core_http
        backoffalgorithm::backoffalgorithm
        pthread
        ssl
        crypto
)

# Add compile definitions
target_compile_definitions(${DEMO_NAME} PRIVATE
    SERVER_HOST="google.com"
    HTTPS_PORT=443
    ROOT_CA_CERT_PATH="/etc/ssl/certs/ca-certificates.crt"
)
