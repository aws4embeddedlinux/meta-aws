find_path(BACKOFFALGORITHM_INCLUDE_DIR backoff_algorithm.h
          PATHS ${CMAKE_INSTALL_PREFIX}/include
          PATH_SUFFIXES backoffalgorithm)

find_library(BACKOFFALGORITHM_LIBRARY
             NAMES backoffalgorithm
             PATHS ${CMAKE_INSTALL_PREFIX}/lib)

include(FindPackageHandleStandardArgs)
find_package_handle_standard_args(backoffalgorithm DEFAULT_MSG
                                  BACKOFFALGORITHM_LIBRARY BACKOFFALGORITHM_INCLUDE_DIR)

mark_as_advanced(BACKOFFALGORITHM_INCLUDE_DIR BACKOFFALGORITHM_LIBRARY)

if(backoffalgorithm_FOUND AND NOT TARGET backoffalgorithm::backoffalgorithm)
  add_library(backoffalgorithm::backoffalgorithm UNKNOWN IMPORTED)
  set_target_properties(backoffalgorithm::backoffalgorithm PROPERTIES
    IMPORTED_LOCATION "${BACKOFFALGORITHM_LIBRARY}"
    INTERFACE_INCLUDE_DIRECTORIES "${BACKOFFALGORITHM_INCLUDE_DIR}")
endif()