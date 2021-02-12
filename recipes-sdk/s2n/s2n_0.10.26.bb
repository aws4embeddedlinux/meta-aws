SUMMARY = "s2n"
DESCRIPTION = "s2n is a C99 implementation of the TLS/SSL protocols that is designed to be simple, small, fast, and with security as a priority."
HOMEPAGE = "https://github.com/awslabs/s2n"
LICENSE = "Apache-2.0"
PROVIDES += "s2n"

inherit cmake

LIC_FILES_CHKSUM = "file://LICENSE;md5=26d85861cd0c0d05ab56ebff38882975"

BRANCH ?= "main"

SRC_URI = "git://github.com/awslabs/s2n.git;branch=${BRANCH}"
SRCREV = "49c46200d6a6b5aee76c5f9adb86c329a737a6ca"

S= "${WORKDIR}/git"

#do_configure_prepend() {
#  cd ${S}
#  git submodule update --init --recursive
#}

DEPENDS = "openssl"
CFLAGS_append = " -Wl,-Bsymbolic"

#EXTRA_OECMAKE += "-DBUILD_DEPS=ON"
EXTRA_OECMAKE += "-DBUILD_TESTING=OFF"
EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=ON"
EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=Release"
EXTRA_OECMAKE += "-DCMAKE_INSTALL_PREFIX=$D/usr"
OECMAKE_BUILDPATH += "${WORKDIR}/build"
OECMAKE_SOURCEPATH += "${S}"

#FILES_${PN} += "${libdir}/*1.0.0"
#FILES_${PN} += "${libdir}/libaws-crt-cpp.so"
#FILES_${PN} += "${libdir}/libDiscovery-cpp.so"
#FILES_${PN} += "${libdir}/libIotIdentity-cpp.so"
#FILES_${PN} += "${libdir}/libIotJobs-cpp.so"
##FILES_${PN} += "${libdir}/libIotShadow-cpp.so"
#FILES_${PN} += "${libdir}/libIotDeviceCommon-cpp.so"
#FILES_${PN} += "${libdir}/libIotDeviceDefender-cpp.so"
#FILES_${PN} += "${libdir}/libIotSecureTunneling-cpp.so"
#FILES_${PN} += "${libdir}/libs2n.so"
#FILES_${PN}-dev += "${includedir}/aws/iotidentity/IotIdentityClient.h"

PACKAGES = "${PN}"
INSANE_SKIP_${PN} += "installed-vs-shipped"
BBCLASSEXTEND = "native nativesdk"
