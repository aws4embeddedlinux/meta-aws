# -*- mode: Conf; -*-
SUMMARY = "AWS C Event Stream"
DESCRIPTION = "C99 implementation of the vnd.amazon.event-stream content-type."

HOMEPAGE = "https://github.com/awslabs/aws-c-event-stream"
LICENSE = "Apache-2.0"
PROVIDES += "aws/crt-c-event-stream"

inherit cmake

LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

BRANCH ?= "main"

SRC_URI = "git://github.com/awslabs/aws-c-event-stream.git;protocol=https;branch=${BRANCH}"

SRCREV = "2f9b60c42f90840ec11822acda3d8cdfa97a773d"

S = "${WORKDIR}/git"

DEPENDS = "openssl s2n aws-c-common aws-checksums aws-c-io"
RDEPENDS:${PN} = "s2n aws-c-common aws-checksums aws-c-io"

AWS_C_INSTALL = "${D}/usr"
CFLAGS:append = " -Wl,-Bsymbolic"
EXTRA_OECMAKE += " \
    -DBUILD_TEST_DEPS=OFF \
    -DBUILD_TESTING=OFF \
    -DCMAKE_MODULE_PATH=${STAGING_LIBDIR}/cmake \
    -DCMAKE_PREFIX_PATH=$D/usr \
    -DCMAKE_INSTALL_PREFIX=$D/usr \
    -DBUILD_SHARED_LIBS=ON \
"

FILES:${PN}-dev += "${libdir}/*/cmake"

BBCLASSEXTEND = "native nativesdk"
