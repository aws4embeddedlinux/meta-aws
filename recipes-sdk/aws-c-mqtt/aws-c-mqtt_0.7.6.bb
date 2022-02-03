# -*- mode: Conf; -*-
SUMMARY = "AWS C MQTT"
DESCRIPTION = "C99 implementation of the MQTT 3.1.1 specification."

HOMEPAGE = "https://github.com/awslabs/aws-c-mqtt"
LICENSE = "Apache-2.0"
PROVIDES += "aws/crt-c-mqtt"

inherit cmake

LIC_FILES_CHKSUM = "file://aws-c-mqtt/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

BRANCH ?= "main"

SRC_URI = "git://github.com/awslabs/aws-c-common.git;protocol=https;branch=${BRANCH};destsuffix=${S}/aws-c-common;name=common \
           git://github.com/awslabs/aws-c-mqtt.git;protocol=https;branch=${BRANCH};destsuffix=${S}/aws-c-mqtt;name=mqtt \
           file://fix-missing-macro.patch;patchdir=${S}/aws-c-mqtt/ \
"

SRCREV_FORMAT = "mqtt"
SRCREV_mqtt = "0a70bf814845e487b7e4862af7ad9e4a1199b5f4"
SRCREV_common = "2a28532d6f13435907ae200a5aea449c01e79149"

S = "${WORKDIR}/git"

DEPENDS = "openssl s2n aws-c-common aws-c-cal aws-c-io aws-c-compression aws-c-http"
RDEPENDS:${PN} = "s2n aws-c-common aws-c-cal aws-c-io aws-c-compression aws-c-http"

OECMAKE_SOURCEPATH = "${S}/aws-c-mqtt"
CFLAGS:append = " -Wl,-Bsymbolic"
EXTRA_OECMAKE += "-DBUILD_TEST_DEPS=OFF"
EXTRA_OECMAKE += "-DBUILD_TESTING=OFF"
EXTRA_OECMAKE += "-DCMAKE_MODULE_PATH=${S}/aws-c-common/cmake"
EXTRA_OECMAKE += "-DCMAKE_PREFIX_PATH=$D${prefix}"
EXTRA_OECMAKE += "-DCMAKE_INSTALL_PREFIX=$D${prefix}"
EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=ON"
OECMAKE_BUILDPATH += "${WORKDIR}/build"
OECMAKE_SOURCEPATH += "${S}"

FILES:${PN} = " \
    ${libdir}/*.so.1.0.0 \
    ${libdir}/*.so \
"
FILES:${PN}-dev = "${includedir}/aws/mqtt/* \
                   ${libdir}/aws-c-mqtt/* \
                   ${libdir}/lib${PN}.so"
FILES:${PN}-dbg = "${prefix}/src/debug/aws-c-mqtt/* \
                   ${libdir}/.debug/lib${PN}.so.1.0.0"

BBCLASSEXTEND = "native nativesdk"

