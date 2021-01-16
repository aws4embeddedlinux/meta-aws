inherit pypi setuptools3

SUMMARY = "SciPy: Scientific Library for Python"
HOMEPAGE = "https://www.scipy.org"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=8256119827cf2bbe63512d4868075867"

SRC_URI += " file://0001-Allow-passing-flags-via-FARCH-for-mach.patch"
SRC_URI[md5sum] = "ecf5c58e4df1d257abf1634d51cb9205"
SRC_URI[sha256sum] = "ddae76784574cc4c172f3d5edd7308be16078dd3b977e8746860c76c195fa707"

DEPENDS += "${PYTHON_PN}-numpy ${PYTHON_PN}-numpy-native ${PYTHON_PN}-pybind11-native lapack"
RDEPENDS_${PN} += "${PYTHON_PN}-numpy lapack"

CLEANBROKEN = "1"

export LAPACK = "${STAGING_LIBDIR}"
export BLAS = "${STAGING_LIBDIR}"

export F90 = "${TARGET_PREFIX}gfortran"
export FARCH = "${TUNE_CCARGS}"
# Numpy expects the LDSHARED env variable to point to a single
# executable, but OE sets it to include some flags as well. So we split
# the existing LDSHARED variable into the base executable and flags, and
# prepend the flags into LDFLAGS
LDFLAGS_prepend := "${@" ".join(d.getVar('LDSHARED', True).split()[1:])} "
export LDSHARED := "${@d.getVar('LDSHARED', True).split()[0]}"

# Tell Numpy to look in target sysroot site-packages directory for libraries
LDFLAGS_append = " -L${STAGING_LIBDIR}/${PYTHON_DIR}/site-packages/numpy/core/lib"

S = "${WORKDIR}/scipy-${PV}"