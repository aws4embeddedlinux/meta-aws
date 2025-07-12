cmake_minimum_required(VERSION 3.0)
project(libjobs VERSION 1.0.0 LANGUAGES C)

# Find required packages
find_package(core_json REQUIRED)

# Include the file containing source definitions
include(${CMAKE_CURRENT_SOURCE_DIR}/jobsFilePaths.cmake)

# Option to build shared libraries
option(BUILD_SHARED_LIBS "Build shared libraries" ON)

# Create libjobs library
add_library(libjobs
    ${JOBS_SOURCES}
)

# Set library properties
set_target_properties(libjobs PROPERTIES
    VERSION ${PROJECT_VERSION}
    SOVERSION ${PROJECT_VERSION_MAJOR}
)

# Set include directories using the paths from jobsFilePaths.cmake
target_include_directories(libjobs
    PUBLIC
        ${JOBS_INCLUDE_PUBLIC_DIRS}
)

# Installation rules
include(GNUInstallDirs)

# Install the library and headers
install(TARGETS libjobs
    LIBRARY DESTINATION ${CMAKE_INSTALL_LIBDIR}
    PUBLIC_HEADER DESTINATION ${CMAKE_INSTALL_INCLUDEDIR}/libjobs
)

# Install header files
install(DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}/source/include/
    DESTINATION ${CMAKE_INSTALL_INCLUDEDIR}/libjobs
    FILES_MATCHING PATTERN "*.h"
)
