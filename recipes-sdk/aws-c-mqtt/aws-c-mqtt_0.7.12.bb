# -*- mode: Conf; -*-
SUMMARY = "AWS C MQTT"
DESCRIPTION = "C99 implementation of the MQTT 3.1.1 specification."

HOMEPAGE = "https://github.com/awslabs/aws-c-mqtt"
LICENSE = "Apache-2.0"
PROVIDES += "aws/crt-c-mqtt"

inherit cmake

LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

BRANCH ?= "main"

SRC_URI = "git://github.com/awslabs/aws-c-mqtt.git;protocol=https;branch=${BRANCH}"

SRCREV = "cea176e7f3ec32d1465788e95ae9c652014aa135"

S = "${WORKDIR}/git"

DEPENDS = "s2n aws-c-common aws-c-cal aws-c-io aws-c-compression aws-c-http"
RDEPENDS:${PN} = "s2n aws-c-common aws-c-cal aws-c-io aws-c-compression aws-c-http"

CFLAGS:append = " -Wl,-Bsymbolic"
EXTRA_OECMAKE += "-DBUILD_TEST_DEPS=OFF"
EXTRA_OECMAKE += "-DBUILD_TESTING=OFF"
EXTRA_OECMAKE += "-DCMAKE_MODULE_PATH=${STAGING_LIBDIR}/cmake"
EXTRA_OECMAKE += "-DCMAKE_PREFIX_PATH=$D/usr"
EXTRA_OECMAKE += "-DCMAKE_INSTALL_PREFIX=$D/usr"
EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=ON"

FILES:${PN}-dev += "${libdir}/*/cmake"

BBCLASSEXTEND = "native nativesdk"

