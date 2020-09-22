SUMMARY = "AWS Device SDK for C++"
DESCRIPTION = "Next generation AWS IoT Client SDK for C++ using the AWS Common Runtime"
HOMEPAGE = "https://github.com/aws/aws-iot-device-sdk-cpp-v2"
LICENSE = "Apache-2.0"
PROVIDES += "aws/aws-iot-device-sdk-cpp-v2"

S= "${WORKDIR}/git"

do_configure_prepend() {
  cd ${S}
  git submodule update --init --recursive
}

do_install_append() {
    rm -rf ${D}${libdir}/cmake
    rm -rf ${D}${libdir}/aws-c-auth
    rm -rf ${D}${libdir}/aws-c-cal
    rm -rf ${D}${libdir}/aws-c-common
    rm -rf ${D}${libdir}/aws-c-compression
    rm -rf ${D}${libdir}/aws-c-http
    rm -rf ${D}${libdir}/aws-c-io
    rm -rf ${D}${libdir}/aws-c-mqtt
    rm -rf ${D}${libdir}/aws-crt-cpp
    rm -rf ${D}${libdir}/s2n
    rm -rf ${D}${libdir}/Discovery-cpp
    rm -rf ${D}${libdir}/IotIdentity-cpp
    rm -rf ${D}${libdir}/IotJobs-cpp
    rm -rf ${D}${libdir}/IotShadow-cpp

    install -d ${D}${libdir}
}

DEPENDS = "openssl"
CFLAGS_append = " -Wl,-Bsymbolic"

EXTRA_OECMAKE += "-DBUILD_DEPS=ON"
EXTRA_OECMAKE += "-DCMAKE_INSTALL_PREFIX=$B/git"
OECMAKE_BUILDPATH += "${WORKDIR}/build"
OECMAKE_SOURCEPATH += "${S}"

FILES_${PN} += "${libdir}/libaws-c-auth.so.1.0.0"
FILES_${PN} += "${libdir}/libaws-c-cal.so.1.0.0"
FILES_${PN} += "${libdir}/libaws-c-common.so.1.0.0"
FILES_${PN} += "${libdir}/libaws-c-compression.so.1.0.0"
FILES_${PN} += "${libdir}/libaws-c-http.so.1.0.0"
FILES_${PN} += "${libdir}/libaws-c-io.so.1.0.0"
FILES_${PN} += "${libdir}/libaws-c-mqtt.so.1.0.0"
FILES_${PN} += "${libdir}/libaws-crt-cpp.so"
FILES_${PN} += "${libdir}/libs2n.so"

PACKAGES = "${PN}"
INSANE_SKIP_${PN} += "installed-vs-shipped"

