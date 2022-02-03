# -*- mode: Conf; -*-
SUMMARY = "AWS C Compression"
DESCRIPTION = "This is a cross-platform C99 implementation of compression algorithms such as gzip, and huffman encoding/decoding. Currently only huffman is implemented."

HOMEPAGE = "https://github.com/awslabs/aws-c-compression"
LICENSE = "Apache-2.0"
PROVIDES += "aws/crt-c-compression"

inherit cmake

LIC_FILES_CHKSUM = "file://aws-c-compression/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

BRANCH ?= "main"

SRC_URI = "git://github.com/awslabs/aws-c-common.git;protocol=https;branch=${BRANCH};destsuffix=${S}/aws-c-common;name=common \
           git://github.com/awslabs/aws-c-compression.git;protocol=https;branch=${BRANCH};destsuffix=${S}/aws-c-compression;name=compression \
"
SRCREV_FORMAT = "compression"
SRCREV_compression = "5fab8bc5ab5321d86f6d153b06062419080820ec"
SRCREV_common = "2a28532d6f13435907ae200a5aea449c01e79149"

S = "${WORKDIR}/git"

DEPENDS = "openssl s2n aws-c-common aws-c-cal aws-c-io"
RDEPENDS:${PN} = "s2n aws-c-common aws-c-cal aws-c-io"

AWS_C_INSTALL = "$D${prefix}"
OECMAKE_SOURCEPATH = "${S}/aws-c-compression"
CFLAGS:append = " -Wl,-Bsymbolic"
EXTRA_OECMAKE += "-DCMAKE_MODULE_PATH=${S}/aws-c-common/cmake"
EXTRA_OECMAKE += "-DCMAKE_PREFIX_PATH=$D${prefix}"
EXTRA_OECMAKE += "-DCMAKE_INSTALL_PREFIX=$D${prefix}"
EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=ON"
OECMAKE_BUILDPATH += "${WORKDIR}/build"
OECMAKE_SOURCEPATH += "${S}"

FILES:${PN} = " \
    ${libdir}/*.so \
    ${libdir}/*.so.1.0.0 \
"
FILES:${PN}-dev = "${includedir}/aws/compression/* \
                   ${libdir}/aws-c-compression/* \
                   ${libdir}/lib${PN}.so"
FILES:${PN}-dbg = "${prefix}/src/debug/aws-c-compression/* \
                   ${libdir}/.debug/lib${PN}.so.1.0.0"

BBCLASSEXTEND = "native nativesdk"

