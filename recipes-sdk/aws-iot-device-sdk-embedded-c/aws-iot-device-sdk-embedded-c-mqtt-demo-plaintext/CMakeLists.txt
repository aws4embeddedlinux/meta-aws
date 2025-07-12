cmake_minimum_required(VERSION 3.0)
project(mqtt_demo_plaintext C)

# Find required packages
find_package(core_mqtt REQUIRED)
find_package(backoffalgorithm REQUIRED)

include(${CMAKE_CURRENT_SOURCE_DIR}/demos/logging-stack/logging.cmake)

# Add source files
add_executable(mqtt_demo_plaintext
    ${CMAKE_CURRENT_SOURCE_DIR}/platform/posix/clock_posix.c
    ${CMAKE_CURRENT_SOURCE_DIR}/platform/posix/transport/src/plaintext_posix.c
    ${CMAKE_CURRENT_SOURCE_DIR}/platform/posix/transport/src/sockets_posix.c
    ${CMAKE_CURRENT_SOURCE_DIR}/demos/mqtt/mqtt_demo_plaintext/mqtt_demo_plaintext.c
)

# Add include directories for platform specific files
target_include_directories(mqtt_demo_plaintext PRIVATE
    ${CMAKE_CURRENT_SOURCE_DIR}
    ${CMAKE_CURRENT_SOURCE_DIR}/platform/posix/transport/include/
    ${CMAKE_CURRENT_SOURCE_DIR}/platform/include/
    PUBLIC
        ${CMAKE_CURRENT_LIST_DIR}
        ${LOGGING_INCLUDE_DIRS}
)


# Link libraries
target_link_libraries(mqtt_demo_plaintext
    PRIVATE
        core_mqtt::core_mqtt
        backoffalgorithm::backoffalgorithm
        pthread
)

# Add compile definitions
target_compile_definitions(mqtt_demo_plaintext PRIVATE
    BROKER_ENDPOINT="localhost"
    CLIENT_IDENTIFIER="mqtt_demo_plaintext"
)
