find_path(FLEETPROVISIONING_INCLUDE_PUBLIC_DIRS fleet_provisioning.h
          PATHS ${CMAKE_INSTALL_PREFIX}/include
          PATH_SUFFIXES fleetprovisioning)

find_library(FLEETPROVISIONING_LIBRARY
             NAMES fleetprovisioning
             PATHS ${CMAKE_INSTALL_PREFIX}/lib)

include(FindPackageHandleStandardArgs)
find_package_handle_standard_args(fleetprovisioning DEFAULT_MSG
                                  FLEETPROVISIONING_LIBRARY FLEETPROVISIONING_INCLUDE_PUBLIC_DIRS)

mark_as_advanced(FLEETPROVISIONING_INCLUDE_PUBLIC_DIRS FLEETPROVISIONING_LIBRARY)

if(fleetprovisioning_FOUND AND NOT TARGET fleetprovisioning::fleetprovisioning)
  add_library(fleetprovisioning::fleetprovisioning UNKNOWN IMPORTED)
  set_target_properties(fleetprovisioning::fleetprovisioning PROPERTIES
    IMPORTED_LOCATION "${FLEETPROVISIONING_LIBRARY}"
    INTERFACE_INCLUDE_DIRECTORIES "${FLEETPROVISIONING_INCLUDE_PUBLIC_DIRS}")
endif()
