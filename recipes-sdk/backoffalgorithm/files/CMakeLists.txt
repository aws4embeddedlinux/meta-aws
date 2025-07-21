set( LIB_NAME "backoffalgorithm" )
cmake_minimum_required(VERSION 3.0)

# Include the file containing source definitions
include(${CMAKE_CURRENT_SOURCE_DIR}/backoffAlgorithmFilePaths.cmake)

# Option to build shared libraries
option(BUILD_SHARED_LIBS "Build shared libraries" ON)

add_library(${LIB_NAME}
    ${BACKOFF_ALGORITHM_SOURCES}
)

set_target_properties(${LIB_NAME} PROPERTIES
    VERSION ${LIB_VERSION}
    SOVERSION ${LIB_SOVERSION}
)

# Set include directories using the paths from backoffAlgorithmFilePaths.cmake
target_include_directories(${LIB_NAME}
    PUBLIC
        ${BACKOFF_ALGORITHM_INCLUDE_PUBLIC_DIRS}
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
