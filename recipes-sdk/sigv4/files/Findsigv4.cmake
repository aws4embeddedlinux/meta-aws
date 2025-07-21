find_path(SIGV4_INCLUDE_PUBLIC_DIRS sigv4.h
          PATHS ${CMAKE_INSTALL_PREFIX}/include
          PATH_SUFFIXES sigv4)

find_library(SIGV4_LIBRARY
             NAMES sigv4
             PATHS ${CMAKE_INSTALL_PREFIX}/lib)

include(FindPackageHandleStandardArgs)
find_package_handle_standard_args(sigv4 DEFAULT_MSG
                                  SIGV4_LIBRARY SIGV4_INCLUDE_PUBLIC_DIRS)

mark_as_advanced(SIGV4_INCLUDE_PUBLIC_DIRS SIGV4_LIBRARY)

if(sigv4_FOUND AND NOT TARGET sigv4::sigv4)
  add_library(sigv4::sigv4 UNKNOWN IMPORTED)
  set_target_properties(sigv4::sigv4 PROPERTIES
    IMPORTED_LOCATION "${SIGV4_LIBRARY}"
    INTERFACE_INCLUDE_DIRECTORIES "${SIGV4_INCLUDE_PUBLIC_DIRS}")
endif()
