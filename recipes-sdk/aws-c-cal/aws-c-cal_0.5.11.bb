# -*- mode: Conf; -*-
SUMMARY = "AWS C Cal"
DESCRIPTION = "AWS Crypto Abstraction Layer: Cross-Platform, C99 wrapper for cryptography primitives."

HOMEPAGE = "https://github.com/awslabs/aws-c-cal"
LICENSE = "Apache-2.0"
PROVIDES += "aws/crt-c-cal"

inherit cmake

LIC_FILES_CHKSUM = "file://aws-c-cal/LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

BRANCH ?= "main"

SRC_URI = "git://github.com/awslabs/aws-c-common.git;protocol=https;branch=${BRANCH};destsuffix=${S}/aws-c-common;name=common \
           git://github.com/awslabs/aws-c-cal.git;protocol=https;branch=${BRANCH};destsuffix=${S}/aws-c-cal;name=cal \
"

SRCREV_FORMAT = "cal"
SRCREV_cal = "aa89aa4950074babe84762413f39bd364ecaf944"
SRCREV_common = "2a28532d6f13435907ae200a5aea449c01e79149"


S = "${WORKDIR}/git"

DEPENDS = "openssl s2n aws-c-common"
RDEPENDS:${PN} = "s2n aws-c-common"

CFLAGS:append = " -Wl,-Bsymbolic"
OECMAKE_SOURCEPATH = "${S}/aws-c-cal"
OECMAKE_BUILDPATH = "${WORKDIR}/build"
EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=ON"
EXTRA_OECMAKE += "-DCMAKE_MODULE_PATH=${S}/aws-c-common/cmake"
EXTRA_OECMAKE += "-DCMAKE_PREFIX_PATH=$D${prefix}"
EXTRA_OECMAKE += "-DCMAKE_INSTALL_PREFIX=$D${prefix}"
EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=Release"

FILES:${PN} = " \
    ${libdir}/*.so \
    ${libdir}/*.so.1.0.0 \
"
FILES:${PN}-dev = "\
    ${includedir}/aws/cal/* \
    ${libdir}/aws-c-cal/* \
    ${libdir}/*.so \
"

FILES:${PN}-dbg = " \
    ${prefix}/src/debug/aws-c-cal/* \
    ${libdir}/.debug/lib${PN}.so.1.0.0 \
"

BBCLASSEXTEND = "native nativesdk"

