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

SRCREV = "39bfa94a14b7126bf0c1330286ef8db452d87e66"

S = "${WORKDIR}/git"

DEPENDS = "openssl s2n aws-c-common aws-checksums aws-c-io"
RDEPENDS:${PN} = "s2n aws-c-common aws-checksums aws-c-io"

AWS_C_INSTALL = "${D}/usr"
CFLAGS:append = " -Wl,-Bsymbolic"
EXTRA_OECMAKE += "-DBUILD_TEST_DEPS=OFF"
EXTRA_OECMAKE += "-DBUILD_TESTING=OFF"
EXTRA_OECMAKE += "-DCMAKE_MODULE_PATH=${STAGING_LIBDIR}/cmake"
EXTRA_OECMAKE += "-DCMAKE_PREFIX_PATH=$D/usr"
EXTRA_OECMAKE += "-DCMAKE_INSTALL_PREFIX=$D/usr"
EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=ON"
OECMAKE_BUILDPATH += "${WORKDIR}/build"
OECMAKE_SOURCEPATH += "${S}"

FILES:${PN}     = "${libdir}/lib${PN}.so.1.0.0"
FILES:${PN}-dev = "${includedir}/aws/event-stream/* \
                   ${libdir}/aws-c-event-stream/* \
                   ${libdir}/lib${PN}.so"
FILES:${PN}-dbg = "/usr/src/debug/aws-c-event-stream/* \
                   ${libdir}/.debug/lib${PN}.so.1.0.0"

BBCLASSEXTEND = "native nativesdk"
