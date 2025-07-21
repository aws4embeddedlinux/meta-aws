find_path(SHADOW_INCLUDE_PUBLIC_DIRS shadow.h
          PATHS ${CMAKE_INSTALL_PREFIX}/include
          PATH_SUFFIXES shadow)

find_library(SHADOW_LIBRARY
             NAMES shadow
             PATHS ${CMAKE_INSTALL_PREFIX}/lib)

include(FindPackageHandleStandardArgs)
find_package_handle_standard_args(shadow DEFAULT_MSG
                                  SHADOW_LIBRARY SHADOW_INCLUDE_PUBLIC_DIRS)

mark_as_advanced(SHADOW_INCLUDE_PUBLIC_DIRS SHADOW_LIBRARY)

if(shadow_FOUND AND NOT TARGET shadow::shadow)
  add_library(shadow::shadow UNKNOWN IMPORTED)
  set_target_properties(shadow::shadow PROPERTIES
    IMPORTED_LOCATION "${SHADOW_LIBRARY}"
    INTERFACE_INCLUDE_DIRECTORIES "${SHADOW_INCLUDE_PUBLIC_DIRS}")
endif()
