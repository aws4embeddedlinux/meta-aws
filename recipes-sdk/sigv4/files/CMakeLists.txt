set( LIB_NAME "sigv4" )

cmake_minimum_required(VERSION 3.0)
project(${LIB_NAME} VERSION 1.0.0 LANGUAGES C)

# Include the file containing source definitions
include(${CMAKE_CURRENT_SOURCE_DIR}/sigv4FilePaths.cmake)

# Option to build shared libraries
option(BUILD_SHARED_LIBS "Build shared libraries" ON)

# Create sigv4 library
add_library(${LIB_NAME}
    ${SIGV4_SOURCES}
)

## Set library properties
set_target_properties(${LIB_NAME} PROPERTIES
    VERSION ${LIB_VERSION}
    SOVERSION ${LIB_SOVERSION}
)

# Set include directories using the paths from sigv4FilePaths.cmake
target_include_directories(${LIB_NAME}
    PUBLIC
        ${SIGV4_INCLUDE_PUBLIC_DIRS}
)

# Installation rules
include(GNUInstallDirs)

# Install the library and headers
install(TARGETS ${LIB_NAME}
    LIBRARY DESTINATION ${CMAKE_INSTALL_LIBDIR}
    PUBLIC_HEADER DESTINATION ${CMAKE_INSTALL_INCLUDEDIR}/${LIB_NAME}
)

# Install header files
install(DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}/source/include/
    DESTINATION ${CMAKE_INSTALL_INCLUDEDIR}/${LIB_NAME}
    FILES_MATCHING PATTERN "*.h"
)
