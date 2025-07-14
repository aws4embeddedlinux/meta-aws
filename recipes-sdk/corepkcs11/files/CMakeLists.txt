set( LIB_NAME core_pkcs )

cmake_minimum_required(VERSION 3.0)

# set(BUILD_SHARED_LIBS OFF)
add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/source/dependency/3rdparty/mbedtls)

# For your library (force shared only)
set(BUILD_SHARED_LIBS ON)

# Include the file containing source definitions
include(${CMAKE_CURRENT_SOURCE_DIR}/pkcsFilePaths.cmake)

# Create PKCS core library
add_library(${LIB_NAME}
    ${PKCS_SOURCES}
    ${PKCS_PAL_POSIX_SOURCES}
    ${CMAKE_CURRENT_SOURCE_DIR}/source/portable/mbedtls/core_pkcs11_mbedtls.c
    ${CMAKE_CURRENT_SOURCE_DIR}/source/dependency/3rdparty/mbedtls/library/threading.c
)

set_target_properties(${LIB_NAME} PROPERTIES
    VERSION ${LIB_VERSION}
    SOVERSION ${LIB_SOVERSION}
)

# Add define to use default config
target_compile_definitions(${LIB_NAME}
    PUBLIC
        MBEDTLS_CONFIG_FILE="${CMAKE_CURRENT_SOURCE_DIR}/source/dependency/3rdparty/mbedtls/include/mbedtls/mbedtls_config.h"
)

# Set include directories using the paths from pkcsFilePaths.cmake
target_include_directories(${LIB_NAME}
    PUBLIC
        ${PKCS_INCLUDE_PUBLIC_DIRS}
        ${PKCS_PAL_INCLUDE_PUBLIC_DIRS}
        "${CMAKE_CURRENT_LIST_DIR}/source/test/include"
        "${CMAKE_CURRENT_SOURCE_DIR}/source/dependency/3rdparty/mbedtls/include"
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

# Link against mbedtls lib
target_link_libraries(${LIB_NAME}
    PRIVATE
#        mbedcrypto
#        mbedx509
        mbedtls
)