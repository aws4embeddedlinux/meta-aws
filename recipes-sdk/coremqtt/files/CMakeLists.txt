cmake_minimum_required(VERSION 3.0)
project(core_mqtt VERSION 1.0.0 LANGUAGES C)

# Include the file containing source definitions
include(${CMAKE_CURRENT_SOURCE_DIR}/mqttFilePaths.cmake)

# Option to build shared libraries
option(BUILD_SHARED_LIBS "Build shared libraries" ON)

add_library(core_mqtt
    ${MQTT_SOURCES}
    ${MQTT_SERIALIZER_SOURCES}
)

# Set library properties
set_target_properties(core_mqtt PROPERTIES
    VERSION ${PROJECT_VERSION}
    SOVERSION ${PROJECT_VERSION_MAJOR}
)

# Set include directories using the paths from mqttFilePaths.cmake
target_include_directories(core_mqtt
    PUBLIC
        ${MQTT_INCLUDE_PUBLIC_DIRS}
)

# Installation rules
include(GNUInstallDirs)

# Install the library and headers
install(TARGETS core_mqtt
    LIBRARY DESTINATION ${CMAKE_INSTALL_LIBDIR}
    PUBLIC_HEADER DESTINATION ${CMAKE_INSTALL_INCLUDEDIR}/core_mqtt
)

# Install header files
install(DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}/source/include/
    DESTINATION ${CMAKE_INSTALL_INCLUDEDIR}/core_mqtt
    FILES_MATCHING PATTERN "*.h"
)
