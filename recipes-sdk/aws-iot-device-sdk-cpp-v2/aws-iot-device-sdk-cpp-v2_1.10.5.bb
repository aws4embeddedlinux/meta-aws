SUMMARY = "AWS IoT Device SDK for C++ v2"
DESCRIPTION = "The AWS IoT Device SDK for C++ v2 provides MQTT APIs for C++ applications"
HOMEPAGE = "https://github.com/aws/aws-iot-device-sdk-cpp-v2"
LICENSE = "Apache-2.0"
PROVIDES += "aws/aws-iot-device-sdk-cpp-v2"

inherit cmake

LIC_FILES_CHKSUM = "file://aws-iot-device-sdk-cpp-v2/LICENSE;md5=f91e61641e7a96835dea6926a65f4702"

BRANCH ?= "main"

SRC_URI = "git://github.com/awslabs/aws-c-common.git;protocol=https;branch=${BRANCH};destsuffix=${S}/aws-c-common;name=common \
           git://github.com/aws/aws-iot-device-sdk-cpp-v2.git;protocol=https;branch=${BRANCH};destsuffix=${S}/aws-iot-device-sdk-cpp-v2;name=sdk \
           file://001-move-c-iot-include.patch \
"
SRCREV_FORMAT = "sdk"
SRCREV_sdk = "4b16a0236be89d77375c43c9ffb8be5b929e6227"
SRCREV_common = "5464be487028fbdca71f80d0a4a83656a018c470"

S= "${WORKDIR}/git"

DEPENDS = "openssl aws-crt-cpp aws-c-iot"
RDEPENDS_${PN} = "aws-crt-cpp aws-c-iot"
CFLAGS_append = " -Wl,-Bsymbolic"

OECMAKE_BUILDPATH += "${WORKDIR}/build"
OECMAKE_SOURCEPATH += "${S}/aws-iot-device-sdk-cpp-v2"

EXTRA_OECMAKE += "-DCMAKE_MODULE_PATH=${S}/aws-c-common/cmake"
EXTRA_OECMAKE += "-DBUILD_DEPS=OFF"
EXTRA_OECMAKE += "-DBUILD_TESTING=OFF"
EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=ON"
EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=Release"
EXTRA_OECMAKE += "-DCMAKE_INSTALL_PREFIX=$D/usr"

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
FILES_SOLIBSDEV = ""

INSANE_SKIP_${PN} += "installed-vs-shipped"
BBCLASSEXTEND = "native nativesdk"
