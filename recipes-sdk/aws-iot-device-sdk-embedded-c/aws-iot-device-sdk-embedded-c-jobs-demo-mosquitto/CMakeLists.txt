cmake_minimum_required(VERSION 3.0)
set( DEMO_NAME "jobs_demo_mosquitto" )

# Find required packages
find_package(backoffalgorithm REQUIRED)
find_package(jobs REQUIRED)
find_package(core_http REQUIRED)
find_package(core_json REQUIRED)
find_package(OpenSSL REQUIRED)

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
    ${CMAKE_CURRENT_SOURCE_DIR}/demos/jobs/${DEMO_NAME}/${DEMO_NAME}.c
)

# Add include directories for platform specific files
target_include_directories(${DEMO_NAME} PRIVATE
    ${CMAKE_CURRENT_SOURCE_DIR}/demos/jobs/${DEMO_NAME}
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
        pthread
        ssl
        crypto
        jobs::jobs
        mosquitto
)

# Add compile definitions
target_compile_definitions(${DEMO_NAME} PRIVATE
    AWS_IOT_ENDPOINT=""
    ROOT_CA_CERT_PATH="/etc/ssl/certs/ca-certificates.crt"
    CLIENT_PRIVATE_KEY_PATH=""

)

# Add define to use default config
target_compile_definitions(${DEMO_NAME}
    PUBLIC
        HTTP_DO_NOT_USE_CUSTOM_CONFIG
)