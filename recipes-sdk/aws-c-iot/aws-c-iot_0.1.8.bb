# -*- mode: Conf; -*-
SUMMARY = "AWS C IoT"
DESCRIPTION = "C99 implementation of AWS IoT cloud services integration with devices"

HOMEPAGE = "https://github.com/awslabs/aws-c-iot"
LICENSE = "Apache-2.0"
PROVIDES += "aws/aws-c-iot"

inherit cmake

LIC_FILES_CHKSUM = "file://LICENSE;md5=2ee41112a44fe7014dce33e26468ba93"

BRANCH ?= "main"

SRC_URI = "git://github.com/awslabs/aws-c-iot.git;protocol=https;branch=${BRANCH}"

SRCREV = "a61ed88a14a2a8ebef1a6ea50bcc79e900729954"

S = "${WORKDIR}/git"

DEPENDS = "aws-crt-cpp aws-c-common s2n aws-c-common aws-c-io aws-c-mqtt aws-c-auth aws-c-http aws-checksums aws-c-event-stream aws-c-s3"
RDEPENDS:${PN} = "aws-crt-cpp aws-c-common"

CFLAGS:append = " -Wl,-Bsymbolic"
EXTRA_OECMAKE += "-DCMAKE_MODULE_PATH=${STAGING_LIBDIR}/cmake"
EXTRA_OECMAKE += "-DCMAKE_PREFIX_PATH=$D/usr"
EXTRA_OECMAKE += "-DCMAKE_INSTALL_PREFIX=$D/usr"
EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=ON"

FILES:${PN}-dev += "${libdir}/*/cmake"

BBCLASSEXTEND = "native nativesdk"

