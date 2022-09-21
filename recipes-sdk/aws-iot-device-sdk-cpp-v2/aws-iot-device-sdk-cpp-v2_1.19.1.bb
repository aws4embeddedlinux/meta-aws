# -*- mode: Conf; -*-
SUMMARY = "AWS IoT Device SDK for C++ v2"
DESCRIPTION = "The AWS IoT Device SDK for C++ v2 provides MQTT APIs for C++ applications"
HOMEPAGE = "https://github.com/aws/aws-iot-device-sdk-cpp-v2"
LICENSE = "Apache-2.0"
PROVIDES += "aws/aws-iot-device-sdk-cpp-v2"

inherit cmake

LIC_FILES_CHKSUM = "file://documents/LICENSE;md5=f91e61641e7a96835dea6926a65f4702"

BRANCH ?= "main"

SRC_URI = "git://github.com/aws/aws-iot-device-sdk-cpp-v2.git;protocol=https;branch=${BRANCH}"
SRCREV = "1b95d70b1d15913fde35ee4e44d18ac9bdda54e7"

UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>.*)"

S = "${WORKDIR}/git"

DEPENDS = "aws-c-iot"
RDEPENDS:${PN} = "aws-c-iot"
CFLAGS:append = " -Wl,-Bsymbolic"

EXTRA_OECMAKE += "-DCMAKE_MODULE_PATH=${STAGING_LIBDIR}/cmake"
EXTRA_OECMAKE += "-DBUILD_DEPS=OFF"
EXTRA_OECMAKE += "-DBUILD_TESTING=OFF"
EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=ON"
EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=Release"
EXTRA_OECMAKE += "-DCMAKE_INSTALL_PREFIX=$D/usr"

FILES:${PN}     = "${libdir}/*.so \
                   ${libdir}/lib${PN}.so.0unstable"
FILES:${PN}-dev = "${includedir}/aws/* \
                   ${libdir}/GreengrassIpc-cpp/* \
                   ${libdir}/IotDeviceDefender-cpp/* \
                   ${libdir}/IotDiscovery-cpp/* \
                   ${libdir}/IotShadow-cpp/* \
                   ${libdir}/Discovery-cpp/* \
                   ${libdir}/IotDeviceCommon-cpp/* \
                   ${libdir}/IotIdentity-cpp/* \
                   ${libdir}/IotSecureTunneling-cpp/* \
                   ${libdir}/EventstreamRpc-cpp/* \
                   ${libdir}/IotJobs-cpp/* \
                   ${libdir}/lib${PN}.so"
FILES:${PN}-dbg = "/usr/src/debug/aws-iot-device-sdk-cpp-v2/* \
                   ${libdir}/.debug/lib*"

# Notify that libraries are not versioned
FILES_SOLIBSDEV = ""

BBCLASSEXTEND = "native nativesdk"
