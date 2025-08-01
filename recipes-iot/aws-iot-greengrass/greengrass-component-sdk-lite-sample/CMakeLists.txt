cmake_minimum_required(VERSION 3.10)
project(hello-world-component)

# Set C standard
set(CMAKE_C_STANDARD 11)
set(CMAKE_C_STANDARD_REQUIRED ON)

# Find the AWS Greengrass SDK Lite
find_path(GGL_INCLUDE_DIR ggl/sdk.h)
find_library(GGL_LIBRARY ggl-sdk)

if(NOT GGL_INCLUDE_DIR OR NOT GGL_LIBRARY)
    message(FATAL_ERROR "AWS Greengrass SDK Lite not found")
endif()

# Create the executable
add_executable(hello-world-component main.c)

# Link with the Greengrass SDK Lite
target_include_directories(hello-world-component PRIVATE ${GGL_INCLUDE_DIR})
target_link_libraries(hello-world-component ${GGL_LIBRARY})

# Set compiler flags for embedded/optimized builds
target_compile_options(hello-world-component PRIVATE
    -Wall
    -Wextra
    -Werror
    -O2
)

# Install the executable
install(TARGETS hello-world-component
    RUNTIME DESTINATION bin
)
