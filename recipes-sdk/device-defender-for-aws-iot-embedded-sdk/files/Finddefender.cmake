find_path(DEFENDER_INCLUDE_PUBLIC_DIRS defender.h
          PATHS ${CMAKE_INSTALL_PREFIX}/include
          PATH_SUFFIXES defender)

find_library(DEFENDER_LIBRARY
             NAMES defender
             PATHS ${CMAKE_INSTALL_PREFIX}/lib)

include(FindPackageHandleStandardArgs)
find_package_handle_standard_args(defender DEFAULT_MSG
                                  DEFENDER_LIBRARY DEFENDER_INCLUDE_PUBLIC_DIRS)

mark_as_advanced(DEFENDER_INCLUDE_PUBLIC_DIRS DEFENDER_LIBRARY)

if(json_FOUND AND NOT TARGET defender::defender)
  add_library(defender::defender UNKNOWN IMPORTED)
  set_target_properties(defender::defender PROPERTIES
    IMPORTED_LOCATION "${DEFENDER_LIBRARY}"
    INTERFACE_INCLUDE_DIRECTORIES "${DEFENDER_INCLUDE_PUBLIC_DIRS}")
endif()
