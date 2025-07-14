find_path(JOBS_INCLUDE_DIR_jobs.h
          PATHS ${CMAKE_INSTALL_PREFIX}/include
          PATH_SUFFIXES libjobs)

find_library(JOBS_LIBRARY
             NAMES libjobs
             PATHS ${CMAKE_INSTALL_PREFIX}/lib)

include(FindPackageHandleStandardArgs)
find_package_handle_standard_args(libjobs DEFAULT_MSG
                                  JOBS_LIBRARY JOBS_INCLUDE_DIR)

mark_as_advanced(JOBS_INCLUDE_DIR JOBS_LIBRARY)

if(jobs_FOUND AND NOT TARGET jobs::jobs)
  add_library(jobs::jobs UNKNOWN IMPORTED)
  set_target_properties(jobs::jobs PROPERTIES
    IMPORTED_LOCATION "${JOBS_LIBRARY}"
    INTERFACE_INCLUDE_DIRECTORIES "${JOBS_INCLUDE_DIR}")
endif()
