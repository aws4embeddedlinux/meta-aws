find_path(CORE_PKCS_INCLUDE_DIR core_pkcs11.h
          PATHS ${CMAKE_INSTALL_PREFIX}/include
          PATH_SUFFIXES core_pkcs)

find_library(CORE_PKCS_LIBRARY
             NAMES core_pkcs
             PATHS ${CMAKE_INSTALL_PREFIX}/lib)

include(FindPackageHandleStandardArgs)
find_package_handle_standard_args(core_pkcs DEFAULT_MSG
                                  CORE_PKCS_LIBRARY CORE_PKCS_INCLUDE_DIR)

mark_as_advanced(CORE_PKCS_INCLUDE_DIR CORE_PKCS_LIBRARY)

if(core_pkcs_FOUND AND NOT TARGET core_pkcs::core_pkcs)
  add_library(core_pkcs::core_pkcs UNKNOWN IMPORTED)
  set_target_properties(core_pkcs::core_pkcs PROPERTIES
    IMPORTED_LOCATION "${CORE_PKCS_LIBRARY}"
    INTERFACE_INCLUDE_DIRECTORIES "${CORE_PKCS_INCLUDE_DIR}")
endif()