cmake_minimum_required(VERSION 3.0)
set( DEMO_NAME "http_demo_s3_download" )

# Find required packages
find_package(core_http REQUIRED)
find_package(core_json REQUIRED)
find_package(sigv4 REQUIRED)
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
    ${CMAKE_CURRENT_SOURCE_DIR}/demos/http/common/src/http_demo_s3_utils.c
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
        core_json::core_json
        backoffalgorithm::backoffalgorithm
        sigv4::sigv4
        pthread
        ssl
        crypto
        mbedcrypto
)

# Add compile definitions
target_compile_definitions(${DEMO_NAME} PRIVATE
    HTTPS_PORT=443
    ROOT_CA_CERT_PATH="/etc/ssl/certs/ca-certificates.crt"
    ROOT_CA_CERT_PATH_S3=""
    CLIENT_CERT_PATH=""
    CLIENT_PRIVATE_KEY_PATH=""
    AWS_IOT_THING_NAME=""
    AWS_IOT_CREDENTIAL_PROVIDER_ENDPOINT=""
    AWS_IOT_CREDENTIAL_PROVIDER_ROLE=""
    AWS_S3_BUCKET_NAME=""
    AWS_S3_BUCKET_REGION=""
    AWS_S3_OBJECT_NAME=""
)
