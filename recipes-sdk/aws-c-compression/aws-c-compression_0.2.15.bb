# -*- mode: Conf; -*-
SUMMARY = "AWS C Compression"
DESCRIPTION = "This is a cross-platform C99 implementation of compression algorithms such as gzip, and huffman encoding/decoding. Currently only huffman is implemented."

HOMEPAGE = "https://github.com/awslabs/aws-c-compression"
LICENSE = "Apache-2.0"
PROVIDES += "aws/crt-c-compression"

inherit cmake

LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

BRANCH ?= "main"

SRC_URI = "git://github.com/awslabs/aws-c-compression.git;protocol=https;branch=${BRANCH}"

SRCREV = "63e1ada3d1c1b2d337e9edc5ea977b1f17450ded"

S = "${WORKDIR}/git"

DEPENDS = "openssl s2n aws-c-common aws-c-cal aws-c-io"
RDEPENDS:${PN} = "s2n aws-c-common aws-c-cal aws-c-io"

AWS_C_INSTALL = "$D/usr"
CFLAGS:append = " -Wl,-Bsymbolic"
EXTRA_OECMAKE += "-DCMAKE_MODULE_PATH=${STAGING_LIBDIR}/cmake"
EXTRA_OECMAKE += "-DCMAKE_PREFIX_PATH=$D/usr"
EXTRA_OECMAKE += "-DCMAKE_INSTALL_PREFIX=$D/usr"
EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=ON"
OECMAKE_BUILDPATH += "${WORKDIR}/build"
OECMAKE_SOURCEPATH += "${S}"

FILES:${PN}     = "${libdir}/lib${PN}.so.1.0.0"
FILES:${PN}-dev = "${includedir}/aws/compression/* \
                   ${libdir}/aws-c-compression/* \
                   ${libdir}/lib${PN}.so"
FILES:${PN}-dbg = "/usr/src/debug/aws-c-compression/* \
                   ${libdir}/.debug/lib${PN}.so.1.0.0"

BBCLASSEXTEND = "native nativesdk"

