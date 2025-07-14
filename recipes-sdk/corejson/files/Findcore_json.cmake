find_path(JSON_INCLUDE_PUBLIC_DIRS core_json.h
          PATHS ${CMAKE_INSTALL_PREFIX}/include
          PATH_SUFFIXES core_json)

find_library(JSON_LIBRARY
             NAMES core_json
             PATHS ${CMAKE_INSTALL_PREFIX}/lib)

include(FindPackageHandleStandardArgs)
find_package_handle_standard_args(core_json DEFAULT_MSG
                                  JSON_LIBRARY JSON_INCLUDE_PUBLIC_DIRS)

mark_as_advanced(JSON_INCLUDE_PUBLIC_DIRS JSON_LIBRARY)

if(json_FOUND AND NOT TARGET json::json)
  add_library(json::json UNKNOWN IMPORTED)
  set_target_properties(json::json PROPERTIES
    IMPORTED_LOCATION "${JSON_LIBRARY}"
    INTERFACE_INCLUDE_DIRECTORIES "${JSON_INCLUDE_PUBLIC_DIRS}")
endif()
