cmake_minimum_required(VERSION 3.10)
project(hello-world-sdk-lite)

# Set C standard
set(CMAKE_C_STANDARD 11)
set(CMAKE_C_STANDARD_REQUIRED ON)

# Find the AWS Greengrass SDK Lite
find_path(GGL_INCLUDE_DIR ggl/sdk.h)
find_library(GGL_LIBRARY NAMES libggl-sdk.a ggl-sdk PATHS /usr/lib)

if(NOT GGL_INCLUDE_DIR OR NOT GGL_LIBRARY)
    message(FATAL_ERROR "AWS Greengrass SDK Lite not found")
endif()

# Create the executable
add_executable(hello-world-sdk-lite main.c)

# Link with the Greengrass SDK Lite
target_include_directories(hello-world-sdk-lite PRIVATE ${GGL_INCLUDE_DIR})
target_link_libraries(hello-world-sdk-lite ${GGL_LIBRARY})

# Set compiler flags for embedded/optimized builds
target_compile_options(hello-world-sdk-lite PRIVATE
    -Wall
    -Wextra
    -Werror
    -O2
)

# Install the executable
install(TARGETS hello-world-sdk-lite
    RUNTIME DESTINATION bin
)
