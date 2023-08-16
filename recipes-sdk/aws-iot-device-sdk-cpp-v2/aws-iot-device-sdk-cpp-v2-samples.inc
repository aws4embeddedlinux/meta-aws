SUMMARY = "AWS IoT Device SDK for C++ v2 samples"
DESCRIPTION = "The AWS IoT Device SDK for C++ v2 provides MQTT APIs for C++ applications samples"
HOMEPAGE = "https://github.com/aws/aws-iot-device-sdk-cpp-v2"
LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://${WORKDIR}/git/documents/LICENSE;md5=f91e61641e7a96835dea6926a65f4702"

DEPENDS += "aws-iot-device-sdk-cpp-v2"

require aws-iot-device-sdk-cpp-v2-version.inc

inherit cmake

BRANCH ?= "main"

# we just want to upgrade the library, then the samples are as well updated
# set to match only git_invalid_tag_regex because UPSTREAM_VERSION_UNKNOWN seems to be broken for git
UPSTREAM_CHECK_GITTAGREGEX = "git_invalid_tag_regex"

CFLAGS:append = " -Wl,-Bsymbolic"

EXTRA_OECMAKE += "\
    -DCMAKE_MODULE_PATH=${STAGING_LIBDIR}/cmake \
    -DBUILD_DEPS=OFF \
    -DBUILD_TESTING=OFF \
    -DBUILD_SHARED_LIBS=ON \
    -DCMAKE_BUILD_TYPE=Release \
"

FILES:${PN}-dev += "${libdir}/*/cmake"

# Notify that libraries are not versioned
SOLIBS = "*.so"
FILES_SOLIBSDEV = ""

BBCLASSEXTEND = "native nativesdk"
