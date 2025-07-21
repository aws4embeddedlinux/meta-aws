find_path(HTTP_INCLUDE_PUBLIC_DIRS core_http_client.h
          PATHS ${CMAKE_INSTALL_PREFIX}/include
          PATH_SUFFIXES core_http)

find_library(CORE_HTTP_LIBRARY
             NAMES core_http
             PATHS ${CMAKE_INSTALL_PREFIX}/lib)

include(FindPackageHandleStandardArgs)
find_package_handle_standard_args(core_http DEFAULT_MSG
                                  CORE_HTTP_LIBRARY HTTP_INCLUDE_PUBLIC_DIRS)

mark_as_advanced(HTTP_INCLUDE_PUBLIC_DIRS CORE_HTTP_LIBRARY)

if(core_http_FOUND AND NOT TARGET core_http::core_http)
  add_library(core_http::core_http UNKNOWN IMPORTED)
  set_target_properties(core_http::core_http PROPERTIES
    IMPORTED_LOCATION "${CORE_HTTP_LIBRARY}"
    INTERFACE_INCLUDE_DIRECTORIES "${HTTP_INCLUDE_PUBLIC_DIRS}")
endif()
