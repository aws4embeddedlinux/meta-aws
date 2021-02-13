SUMMARY = "AWS IoT Device SDK for C++ v2"
DESCRIPTION = "The AWS IoT Device SDK for C++ v2 provides MQTT APIs for C++ applications"
HOMEPAGE = "https://github.com/aws/aws-iot-device-sdk-cpp-v2"
LICENSE = "Apache-2.0"
PROVIDES += "aws/aws-iot-device-sdk-cpp-v2"

inherit cmake

LIC_FILES_CHKSUM = "file://LICENSE;md5=f91e61641e7a96835dea6926a65f4702"

BRANCH ?= "main"

SRC_URI = "git://github.com/aws/aws-iot-device-sdk-cpp-v2.git;branch=${BRANCH};name=aws-iot-device-sdk-cpp-v2"
SRCREV = "4b16a0236be89d77375c43c9ffb8be5b929e6227"

S= "${WORKDIR}/git"

do_configure_prepend() {
  cd ${S}
  git submodule update --init --recursive
}

DEPENDS = "openssl"
CFLAGS_append = " -Wl,-Bsymbolic"

EXTRA_OECMAKE += "-DBUILD_DEPS=ON"
EXTRA_OECMAKE += "-DBUILD_TESTING=OFF"
EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=ON"
EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=Release"
EXTRA_OECMAKE += "-DCMAKE_INSTALL_PREFIX=$D/usr"
OECMAKE_BUILDPATH += "${WORKDIR}/build"
OECMAKE_SOURCEPATH += "${S}"

FILES_${PN} += "${libdir}/*1.0.0"
FILES_${PN} += "${libdir}/libaws-crt-cpp.so"
FILES_${PN} += "${libdir}/libDiscovery-cpp.so"
FILES_${PN} += "${libdir}/libIotIdentity-cpp.so"
FILES_${PN} += "${libdir}/libIotJobs-cpp.so"
FILES_${PN} += "${libdir}/libIotShadow-cpp.so"
FILES_${PN} += "${libdir}/libIotDeviceCommon-cpp.so"
FILES_${PN} += "${libdir}/libIotDeviceDefender-cpp.so"
FILES_${PN} += "${libdir}/libIotSecureTunneling-cpp.so"
FILES_${PN} += "${libdir}/libs2n.so"
FILES_${PN}-dev += "${includedir}/aws/iotidentity/IotIdentityClient.h"

PACKAGES = "${PN}"
INSANE_SKIP_${PN} += "installed-vs-shipped"
BBCLASSEXTEND = "native nativesdk"
