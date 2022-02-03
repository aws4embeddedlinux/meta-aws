# -*- mode: Conf; -*-
SUMMARY = "AWS C IO"
DESCRIPTION = "aws-c-io is an event driven framework for implementing application protocols. It is built on top of cross-platform abstractions that allow you as a developer to think only about the state machine and API for your protocols."

HOMEPAGE = "https://github.com/awslabs/aws-c-io"
LICENSE = "Apache-2.0"
PROVIDES += "aws/crt-c-io"

inherit cmake

LIC_FILES_CHKSUM = "file://aws-c-io/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

BRANCH ?= "main"

SRC_URI = "git://github.com/awslabs/aws-c-common.git;protocol=https;branch=${BRANCH};destsuffix=${S}/aws-c-common;name=common \
           git://github.com/awslabs/aws-c-io.git;protocol=https;branch=${BRANCH};destsuffix=${S}/aws-c-io;name=io \
"

SRCREV_FORMAT="io"
SRCREV_io = "57b00febac48e78f8bf8cff4c82a249e6648842a"
SRCREV_common = "2a28532d6f13435907ae200a5aea449c01e79149"

S = "${WORKDIR}/git"

DEPENDS = "openssl s2n aws-c-common aws-c-cal"
RDEPENDS:${PN} = "s2n aws-c-common aws-c-cal"

AWS_C_INSTALL = "$D${prefix}"
OECMAKE_SOURCEPATH = "${S}/aws-c-io"
CFLAGS:append = " -Wl,-Bsymbolic"
EXTRA_OECMAKE += "-DCMAKE_MODULE_PATH=${S}/aws-c-common/cmake"
EXTRA_OECMAKE += "-DCMAKE_PREFIX_PATH=$D${prefix}"
EXTRA_OECMAKE += "-DCMAKE_INSTALL_PREFIX=$D${prefix}"
EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=ON"
OECMAKE_BUILDPATH += "${WORKDIR}/build"
OECMAKE_SOURCEPATH += "${S}"

FILES:${PN}     = " \
    ${libdir}/*.so.1.0.0 \
    ${libdir}/*.so \
"
FILES:${PN}-dev = "${includedir}/aws/io/* \
                   ${includedir}/aws/testing/* \
                   ${libdir}/aws-c-io/* \
                   ${libdir}/lib${PN}.so"
FILES:${PN}-dbg = "${prefix}/src/debug/aws-c-io/* \
                   ${libdir}/.debug/lib${PN}.so.1.0.0"

BBCLASSEXTEND = "native nativesdk"

