# -*- mode: Conf; -*-
SUMMARY = "AWS IoT Device SDK for C++ v2"
DESCRIPTION = "The AWS IoT Device SDK for C++ v2 provides MQTT APIs for C++ applications"
HOMEPAGE = "https://github.com/aws/aws-iot-device-sdk-cpp-v2"
LICENSE = "Apache-2.0"
PROVIDES += "aws/aws-iot-device-sdk-cpp-v2"

inherit cmake

LIC_FILES_CHKSUM = "file://aws-iot-device-sdk-cpp-v2/LICENSE;md5=f91e61641e7a96835dea6926a65f4702"

BRANCH ?= "main"
TAG ?= "v${PV}"
TAG_COMMON ?= "v0.6.8"

SRC_URI = "git://github.com/awslabs/aws-c-common.git;branch=${BRANCH};destsuffix=${S}/aws-c-common;tag=${TAG_COMMON} \
           git://github.com/aws/aws-iot-device-sdk-cpp-v2.git;branch=${BRANCH};destsuffix=${S}/aws-iot-device-sdk-cpp-v2;tag=${TAG} \
"

S = "${WORKDIR}/git"

DEPENDS = "openssl aws-crt-cpp aws-c-iot aws-c-http"
RDEPENDS:${PN} = "aws-crt-cpp aws-c-iot"
CFLAGS:append = " -Wl,-Bsymbolic"
OECMAKE_BUILDPATH += "${WORKDIR}/build"
OECMAKE_SOURCEPATH += "${S}/aws-iot-device-sdk-cpp-v2"

EXTRA_OECMAKE += "-DCMAKE_MODULE_PATH=${S}/aws-c-common/cmake"
EXTRA_OECMAKE += "-DBUILD_DEPS=OFF"
EXTRA_OECMAKE += "-DBUILD_TESTING=OFF"
EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=ON"
EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=Release"
EXTRA_OECMAKE += "-DCMAKE_INSTALL_PREFIX=$D/usr"

FILES:${PN} += "${libdir}/*.so"
FILES:${PN}-dev += "${includedir}"

INSANE_SKIP:${PN} += "installed-vs-shipped"
INSANE_SKIP:${PN}-dev += "installed-vs-shipped"

# Notify that libraries are not versioned
FILES_SOLIBSDEV = ""

BBCLASSEXTEND = "native nativesdk"
