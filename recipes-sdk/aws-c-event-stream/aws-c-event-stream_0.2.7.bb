# -*- mode: Conf; -*-
SUMMARY = "AWS C Event Stream"
DESCRIPTION = "C99 implementation of the vnd.amazon.event-stream content-type."

HOMEPAGE = "https://github.com/awslabs/aws-c-event-stream"
LICENSE = "Apache-2.0"
PROVIDES += "aws/crt-c-event-stream"

inherit cmake

LIC_FILES_CHKSUM = "file://aws-c-event-stream/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

BRANCH ?= "main"

SRC_URI = "git://github.com/awslabs/aws-c-common.git;protocol=https;branch=${BRANCH};destsuffix=${S}/aws-c-common;name=common \
           git://github.com/awslabs/aws-c-event-stream.git;protocol=https;branch=${BRANCH};destsuffix=${S}/aws-c-event-stream;name=es \
"

SRCREV_FORMAT = "es"
SRCREV_es = "e87537be561d753ec82e783bc0929b1979c585f8"
SRCREV_common = "2a28532d6f13435907ae200a5aea449c01e79149"

S = "${WORKDIR}/git"

DEPENDS = "openssl s2n aws-c-common aws-checksums aws-c-io"
RDEPENDS:${PN} = "s2n aws-c-common aws-checksums aws-c-io"

AWS_C_INSTALL = "$D${prefix}"
OECMAKE_SOURCEPATH = "${S}/aws-c-event-stream"
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
FILES:${PN}-dev = "${includedir}/aws/event-stream/* \
                   ${libdir}/aws-c-event-stream/* \
                   ${libdir}/lib${PN}.so"
FILES:${PN}-dbg = "${prefix}/src/debug/aws-c-event-stream/* \
                   ${libdir}/.debug/lib${PN}.so.1.0.0"

BBCLASSEXTEND = "native nativesdk"
