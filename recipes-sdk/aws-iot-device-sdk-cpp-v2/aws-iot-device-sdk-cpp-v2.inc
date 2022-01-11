# -*- mode: Conf; -*-
SUMMARY = "AWS IoT Device SDK for C++ v2"
DESCRIPTION = "The AWS IoT Device SDK for C++ v2 provides MQTT APIs for C++ applications"
HOMEPAGE = "https://github.com/aws/aws-iot-device-sdk-cpp-v2"
LICENSE = "Apache-2.0"
PROVIDES += "aws/aws-iot-device-sdk-cpp-v2"

S= "${WORKDIR}/git"

do_configure:prepend() {
  cd ${S}
  git submodule update --init --recursive
}

DEPENDS = "openssl"
CFLAGS:append = " -Wl,-Bsymbolic"

EXTRA_OECMAKE += "-DBUILD_DEPS=ON"
EXTRA_OECMAKE += "-DBUILD_TESTING=OFF"
EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=ON"
EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=Release"
EXTRA_OECMAKE += "-DCMAKE_INSTALL_PREFIX=$D/usr"
OECMAKE_BUILDPATH += "${WORKDIR}/build"
OECMAKE_SOURCEPATH += "${S}"

FILES:${PN} += "${libdir}/*1.0.0"
FILES:${PN} += "${libdir}/libaws-crt-cpp.so"
FILES:${PN} += "${libdir}/libDiscovery-cpp.so"
FILES:${PN} += "${libdir}/libIotIdentity-cpp.so"
FILES:${PN} += "${libdir}/libIotJobs-cpp.so"
FILES:${PN} += "${libdir}/libIotShadow-cpp.so"
FILES:${PN} += "${libdir}/libs2n.so"

INSANE_SKIP:${PN} += "installed-vs-shipped"
FILES_SOLIBSDEV = ""
BBCLASSEXTEND = "native nativesdk"
