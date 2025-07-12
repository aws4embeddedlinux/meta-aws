cmake_minimum_required(VERSION 3.0)
project(core_pkcs VERSION 1.0.0 LANGUAGES C)

add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/source/dependency/3rdparty/mbedtls)

# Include the file containing source definitions
include(${CMAKE_CURRENT_SOURCE_DIR}/pkcsFilePaths.cmake)

# include(${CMAKE_CURRENT_SOURCE_DIR}/mbedtls.cmake)

# Option to build shared libraries
option(BUILD_SHARED_LIBS "Build shared libraries" ON)

# Create PKCS core library
add_library(core_pkcs
    ${PKCS_SOURCES}
    ${PKCS_SERIALIZER_SOURCES}
)

# Add define to use default config
target_compile_definitions(core_pkcs
    PUBLIC
        PKCS_DO_NOT_USE_CUSTOM_CONFIG
        MBEDTLS_CONFIG_FILE="${CMAKE_CURRENT_SOURCE_DIR}/source/dependency/3rdparty/mbedtls/include/mbedtls/mbedtls_config.h"
)

# Set library properties
set_target_properties(core_pkcs PROPERTIES
    VERSION ${PROJECT_VERSION}
    SOVERSION ${PROJECT_VERSION_MAJOR}
)

# Set include directories using the paths from pkcsFilePaths.cmake
target_include_directories(core_pkcs
    PUBLIC
        ${PKCS_INCLUDE_PUBLIC_DIRS}
        "${CMAKE_CURRENT_LIST_DIR}/source/test/include"
        "${CMAKE_CURRENT_SOURCE_DIR}/source/dependency/3rdparty/mbedtls/include"
)

# Installation rules
include(GNUInstallDirs)

# Install the library and headers
install(TARGETS core_pkcs
    LIBRARY DESTINATION ${CMAKE_INSTALL_LIBDIR}
    PUBLIC_HEADER DESTINATION ${CMAKE_INSTALL_INCLUDEDIR}/core_pkcs
)

# Install header files
install(DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}/source/include/
    DESTINATION ${CMAKE_INSTALL_INCLUDEDIR}/core_pkcs
    FILES_MATCHING PATTERN "*.h"
)
