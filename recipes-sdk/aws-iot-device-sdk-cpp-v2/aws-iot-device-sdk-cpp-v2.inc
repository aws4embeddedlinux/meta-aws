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

do_install_append() {
#    rm -rf ${D}${libdir}/cmake
#    rm -rf ${D}${libdir}/aws-c-auth
#    rm -rf ${D}${libdir}/aws-c-cal
#    rm -rf ${D}${libdir}/aws-c-common
#    rm -rf ${D}${libdir}/aws-c-compression
#    rm -rf ${D}${libdir}/aws-c-http
#    rm -rf ${D}${libdir}/aws-c-io
#    rm -rf ${D}${libdir}/aws-c-mqtt
#    rm -rf ${D}${libdir}/aws-crt-cpp
#    rm -rf ${D}${libdir}/s2n
#    rm -rf ${D}${libdir}/Discovery-cpp
#    rm -rf ${D}${libdir}/IotIdentity-cpp
#    rm -rf ${D}${libdir}/IotJobs-cpp
#    rm -rf ${D}${libdir}/IotShadow-cpp
#    install -d ${D}${libdir}
}

DEPENDS = "openssl"
CFLAGS_append = " -Wl,-Bsymbolic"

EXTRA_OECMAKE += "-DBUILD_DEPS=ON"
EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=1"
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

#FILES_${PN}-dev += "${libdir}/*1.0.0"
#FILES_${PN}-dev += "${libdir}/libaws-crt-cpp.so"
#FILES_${PN}-dev += "${libdir}/libs2n.so"

PACKAGES = "${PN}"
INSANE_SKIP_${PN} += "installed-vs-shipped"
BBCLASSEXTEND = "native nativesdk"
