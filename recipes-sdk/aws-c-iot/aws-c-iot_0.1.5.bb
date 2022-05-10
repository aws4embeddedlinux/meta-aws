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

SRCREV = "9a5d6a85484d06024dc18c4778ba733266a634ee"

S = "${WORKDIR}/git"

DEPENDS = "aws-crt-cpp aws-c-common"
RDEPENDS:${PN} = "aws-crt-cpp aws-c-common"

CFLAGS:append = " -Wl,-Bsymbolic"
EXTRA_OECMAKE += "-DCMAKE_MODULE_PATH=${STAGING_LIBDIR}/cmake"
EXTRA_OECMAKE += "-DCMAKE_PREFIX_PATH=$D/usr"
EXTRA_OECMAKE += "-DCMAKE_INSTALL_PREFIX=$D/usr"
EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=ON"

FILES:${PN}     = "${libdir}/lib${PN}.so.1.0.0 \
                   ${libdir}/lib${PN}.so.0unstable"
FILES:${PN}-dev = "${includedir}/aws/iotdevice/* \
                   ${libdir}/aws-c-iot/* \
                   ${libdir}/lib${PN}.so"
FILES:${PN}-dbg = "/usr/src/debug/aws-c-iot/* \
                   ${libdir}/.debug/lib${PN}.so.1.0.0"

BBCLASSEXTEND = "native nativesdk"

