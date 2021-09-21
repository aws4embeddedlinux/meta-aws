# -*- mode: Conf; -*-
SUMMARY = "AWS C Common"
DESCRIPTION = "Core c99 package for AWS SDK for C. Includes cross-platform primitives, configuration, data structures, and error handling."

HOMEPAGE = "https://github.com/awslabs/aws-c-common"
LICENSE = "Apache-2.0"
PROVIDES = "aws/crt-c-common"

inherit cmake

LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

BRANCH ?= "main"
TAG ?= "v${PV}"
SRC_URI = "git://github.com/awslabs/aws-c-common.git;branch=${BRANCH};tag=${TAG};"

S = "${WORKDIR}/git"

BUILD_SHARED_LIBS = "ON"

CFLAGS:append = " -Wl,-Bsymbolic"
EXTRA_OECMAKE:append = " -DCMAKE_INSTALL_PREFIX=$D/usr"
EXTRA_OECMAKE += "-DBUILD_TESTING=OFF"
EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=${BUILD_SHARED_LIBS}"
EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=Release"
OECMAKE_BUILDPATH = "${WORKDIR}/build"
OECMAKE_SOURCEPATH = "${S}"

do_install:append() {
	sed -i "s/BUILD_SHARED_LIBS/${BUILD_SHARED_LIBS}/" ${D}${libdir}/${BPN}/cmake/${BPN}-config.cmake
}

FILES:${PN}     = "${libdir}/lib${PN}.so.1.0.0 \
                   ${libdir}/lib${PN}.so.1"
FILES:${PN}-dev = "${includedir}/aws/common/* \
                   ${includedir}/aws/testing/* \
                   ${libdir}/cmake/* \
                   ${libdir}/aws-c-common/* \
                   ${libdir}/lib${PN}.so"
FILES:${PN}-dbg = "/usr/src/debug/aws-c-common/* \
                   ${libdir}/.debug/lib${PN}.so.1.0.0"

BBCLASSEXTEND = "native nativesdk"
