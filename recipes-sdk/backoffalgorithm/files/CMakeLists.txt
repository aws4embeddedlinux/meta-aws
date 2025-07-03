cmake_minimum_required(VERSION 3.0)
project(backoffalgorithm VERSION 1.0.0 LANGUAGES C)

# Include the file containing source definitions
include(${CMAKE_CURRENT_SOURCE_DIR}/backoffAlgorithmFilePaths.cmake)

# Option to build shared libraries
option(BUILD_SHARED_LIBS "Build shared libraries" ON)

# Create backoffalgorithm library
add_library(backoffalgorithm
    ${BACKOFF_ALGORITHM_SOURCES}
)

# Set library properties
set_target_properties(backoffalgorithm PROPERTIES
    VERSION ${PROJECT_VERSION}
    SOVERSION ${PROJECT_VERSION_MAJOR}
)

# Set include directories using the paths from backoffAlgorithmFilePaths.cmake
target_include_directories(backoffalgorithm
    PUBLIC
        ${BACKOFF_ALGORITHM_INCLUDE_PUBLIC_DIRS}
)

# Installation rules
include(GNUInstallDirs)

# Install the library and headers
install(TARGETS backoffalgorithm
    LIBRARY DESTINATION ${CMAKE_INSTALL_LIBDIR}
    PUBLIC_HEADER DESTINATION ${CMAKE_INSTALL_INCLUDEDIR}/backoffalgorithm
)

# Install header files
install(DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}/source/include/
    DESTINATION ${CMAKE_INSTALL_INCLUDEDIR}/backoffalgorithm
    FILES_MATCHING PATTERN "*.h"
)
