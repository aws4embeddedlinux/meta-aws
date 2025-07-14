cmake_minimum_required(VERSION 3.0)
project(sigv4 VERSION 1.0.0 LANGUAGES C)


# Find required packages
find_package(mbedtls REQUIRED)

# Include the file containing source definitions
include(${CMAKE_CURRENT_SOURCE_DIR}/sigv4FilePaths.cmake)

# Option to build shared libraries
option(BUILD_SHARED_LIBS "Build shared libraries" ON)

# Create sigv4 library
add_library(sigv4
    ${SIGV4_SOURCES}
)

## Set library properties
set_target_properties(sigv4 PROPERTIES
    VERSION ${PROJECT_VERSION}
    SOVERSION ${PROJECT_VERSION_MAJOR}
)

# Set include directories using the paths from sigv4FilePaths.cmake
target_include_directories(sigv4
    PUBLIC
        ${SIGV4_INCLUDE_PUBLIC_DIRS}
)

# Installation rules
include(GNUInstallDirs)

# Install the library and headers
install(TARGETS sigv4
    LIBRARY DESTINATION ${CMAKE_INSTALL_LIBDIR}
    PUBLIC_HEADER DESTINATION ${CMAKE_INSTALL_INCLUDEDIR}/sigv4
)

# Install header files
install(DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}/source/include/
    DESTINATION ${CMAKE_INSTALL_INCLUDEDIR}/sigv4
    FILES_MATCHING PATTERN "*.h"
)
