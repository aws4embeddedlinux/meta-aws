SUMMARY = "AWS IoT Device SDK for C++ v2"
DESCRIPTION = "The AWS IoT Device SDK for C++ v2 provides MQTT APIs for C++ applications"
HOMEPAGE = "https://github.com/aws/aws-iot-device-sdk-cpp-v2"
LICENSE = "Apache-2.0"
PROVIDES += "aws/aws-iot-device-sdk-cpp-v2"

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
FILES_${PN} += "${libdir}/libs2n.so"
FILES_SOLIBSDEV = ""

INSANE_SKIP_${PN} += "installed-vs-shipped"
BBCLASSEXTEND = "native nativesdk"
