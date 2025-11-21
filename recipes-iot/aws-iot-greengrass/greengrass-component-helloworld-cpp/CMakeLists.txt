cmake_minimum_required(VERSION 3.10)
project(hello-world-cpp CXX)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

find_path(GG_INCLUDE_DIR gg/sdk.h REQUIRED)
find_library(GG_SDK_LIB gg-sdk REQUIRED)
find_library(GG_SDK_CPP_LIB gg-sdk++ REQUIRED)

add_executable(hello-world-cpp main.cpp)
target_include_directories(hello-world-cpp PRIVATE ${GG_INCLUDE_DIR})
target_link_libraries(hello-world-cpp -Wl,--whole-archive ${GG_SDK_CPP_LIB} -Wl,--no-whole-archive ${GG_SDK_LIB} pthread)
