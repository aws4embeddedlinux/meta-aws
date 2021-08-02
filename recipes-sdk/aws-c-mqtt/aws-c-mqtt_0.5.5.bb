SUMMARY = "AWS C MQTT"
DESCRIPTION = "C99 implementation of the MQTT 3.1.1 specification."

HOMEPAGE = "https://github.com/awslabs/aws-c-mqtt"
LICENSE = "Apache-2.0"
PROVIDES += "aws/crt-c-mqtt"

inherit cmake

LIC_FILES_CHKSUM = "file://aws-c-mqtt/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

BRANCH ?= "main"

SRC_URI = "git://github.com/awslabs/aws-c-common.git;branch=${BRANCH};destsuffix=${S}/aws-c-common;name=common \
           git://github.com/awslabs/aws-c-mqtt.git;branch=${BRANCH};destsuffix=${S}/aws-c-mqtt;name=mqtt \
"

SRCREV_common = "00c91eeb186970d50690ebbdceefdeae5c31fb4c"
SRCREV_mqtt = "8984c51bc834598e3cb1e73ae5d7d614b445ce48"

S = "${WORKDIR}/git"

DEPENDS = "openssl s2n aws-c-common aws-c-cal aws-c-io aws-c-compression aws-c-http"
RDEPENDS_${PN} = "s2n aws-c-common aws-c-cal aws-c-io aws-c-compression aws-c-http"

OECMAKE_SOURCEPATH = "${S}/aws-c-mqtt"
CFLAGS_append = " -Wl,-Bsymbolic"
EXTRA_OECMAKE += "-DBUILD_TEST_DEPS=OFF"
EXTRA_OECMAKE += "-DBUILD_TESTING=OFF"
EXTRA_OECMAKE += "-DCMAKE_MODULE_PATH=${S}/aws-c-common/cmake"
EXTRA_OECMAKE += "-DCMAKE_PREFIX_PATH=$D/usr"
EXTRA_OECMAKE += "-DCMAKE_INSTALL_PREFIX=$D/usr"
OECMAKE_BUILDPATH += "${WORKDIR}/build"
OECMAKE_SOURCEPATH += "${S}"

INSANE_SKIP_${PN} += "installed-vs-shipped"
BBCLASSEXTEND = "native nativesdk"

