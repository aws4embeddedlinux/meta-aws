SUMMARY = "AWS IoT Device SDK for C++ v2"
DESCRIPTION = "The AWS IoT Device SDK for C++ v2 provides MQTT APIs for C++ applications"
HOMEPAGE = "https://github.com/aws/aws-iot-device-sdk-cpp-v2"
LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://documents/LICENSE;md5=f91e61641e7a96835dea6926a65f4702"

DEPENDS += "aws-c-iot"

PROVIDES += "aws/aws-iot-device-sdk-cpp-v2"

require aws-iot-device-sdk-cpp-v2-version.inc

SRC_URI:append = " file://run-ptest"

S = "${WORKDIR}/git"

inherit cmake pkgconfig ptest

UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+\.\d+(\.\d+)*)"

CFLAGS:append = " -Wl,-Bsymbolic"

EXTRA_OECMAKE += "\
    -DCMAKE_MODULE_PATH=${STAGING_LIBDIR}/cmake \
    -DBUILD_DEPS=OFF \
    -DBUILD_TESTING=OFF \
    -DCMAKE_BUILD_TYPE=Release \
"

# Notify that libraries are not versioned
SOLIBS = "*.so"
FILES_SOLIBSDEV = ""

# enable PACKAGECONFIG = "static" to build static instead of shared libs
PACKAGECONFIG[static] = "-DBUILD_SHARED_LIBS=OFF,-DBUILD_SHARED_LIBS=ON"

PACKAGECONFIG ??= "\
   ${@bb.utils.contains('PTEST_ENABLED', '1', 'with-tests', '', d)} \
   "
PACKAGECONFIG[with-tests] = "-DBUILD_TESTING=ON,-DBUILD_TESTING=OFF,"

FILES:${PN}-dev += "${libdir}/*/cmake"

RDEPENDS:${PN}-ptest:prepend = "\
    aws-iot-device-sdk-cpp-v2-samples-mqtt5-pubsub \
    "

BBCLASSEXTEND = "native nativesdk"
