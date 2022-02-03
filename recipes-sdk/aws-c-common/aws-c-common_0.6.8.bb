# -*- mode: Conf; -*-
SUMMARY = "AWS C Common"
DESCRIPTION = "Core c99 package for AWS SDK for C. Includes cross-platform primitives, configuration, data structures, and error handling."

HOMEPAGE = "https://github.com/awslabs/aws-c-common"
LICENSE = "Apache-2.0"
PROVIDES = "aws/crt-c-common"

inherit cmake

LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

BRANCH ?= "main"
SRC_URI = "git://github.com/awslabs/aws-c-common.git;protocol=https;branch=${BRANCH}"
SRCREV = "2a28532d6f13435907ae200a5aea449c01e79149"

S = "${WORKDIR}/git"

CFLAGS:append = " -Wl,-Bsymbolic"
EXTRA_OECMAKE:append = " -DCMAKE_INSTALL_PREFIX=$D${prefix}"
EXTRA_OECMAKE += "-DBUILD_TESTING=OFF"
EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=ON"
EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=Release"
OECMAKE_BUILDPATH = "${WORKDIR}/build"
OECMAKE_SOURCEPATH = "${S}"

FILES:${PN}     = "${libdir}/lib${BPN}.so.1.0.0 \
                   ${libdir}/lib${BPN}.so.1"
FILES:${PN}-dev = "${includedir}/aws/common/* \
                   ${includedir}/aws/testing/* \
                   ${libdir}/cmake/* \
                   ${libdir}/aws-c-common/* \
                   ${libdir}/lib${BPN}.so"
FILES:${PN}-dbg = "${prefix}/src/debug/aws-c-common/* \
                   ${libdir}/.debug/lib${BPN}.so.1.0.0"

BBCLASSEXTEND = "native nativesdk"
