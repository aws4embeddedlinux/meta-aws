set( LIB_NAME "core_mqtt" )

cmake_minimum_required(VERSION 3.0)

# Include the file containing source definitions
include(${CMAKE_CURRENT_SOURCE_DIR}/mqttFilePaths.cmake)

option(BUILD_SHARED_LIBS "Build shared libraries" ON)

add_library(${LIB_NAME}
    ${MQTT_SOURCES}
    ${MQTT_SERIALIZER_SOURCES}
)

set_target_properties(${LIB_NAME} PROPERTIES
    VERSION ${LIB_VERSION}
    SOVERSION ${LIB_SOVERSION}
)

target_include_directories(${LIB_NAME}
    PUBLIC
        ${MQTT_INCLUDE_PUBLIC_DIRS}
)

include(GNUInstallDirs)

install(TARGETS ${LIB_NAME}
    LIBRARY DESTINATION ${CMAKE_INSTALL_LIBDIR}
    PUBLIC_HEADER DESTINATION ${CMAKE_INSTALL_INCLUDEDIR}/${LIB_NAME}
)

install(DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}/source/include/
    DESTINATION ${CMAKE_INSTALL_INCLUDEDIR}/${LIB_NAME}
    FILES_MATCHING PATTERN "*.h"
)
