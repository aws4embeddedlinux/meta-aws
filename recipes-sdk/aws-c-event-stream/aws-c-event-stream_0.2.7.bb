# -*- mode: Conf; -*-
SUMMARY = "AWS C Event Stream"
DESCRIPTION = "C99 implementation of the vnd.amazon.event-stream content-type."

HOMEPAGE = "https://github.com/awslabs/aws-c-event-stream"
LICENSE = "Apache-2.0"
PROVIDES += "aws/crt-c-event-stream"

inherit cmake

LIC_FILES_CHKSUM = "file://aws-c-event-stream/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

BRANCH ?= "main"
TAG ?= "v${PV}"
TAG_COMMON ?= "v0.6.8"

SRC_URI = "git://github.com/awslabs/aws-c-common.git;branch=${BRANCH};tag=${TAG_COMMON};destsuffix=${S}/aws-c-common;name=common \
           git://github.com/awslabs/aws-c-event-stream.git;branch=${BRANCH};tag=${TAG};destsuffix=${S}/aws-c-event-stream;name=es \
"

S = "${WORKDIR}/git"

DEPENDS = "openssl s2n aws-c-common aws-checksums aws-c-io"
RDEPENDS:${PN} = "s2n aws-c-common aws-checksums aws-c-io"

BUILD_SHARED_LIBS = "ON"

AWS_C_INSTALL = "$D/usr"
OECMAKE_SOURCEPATH = "${S}/aws-c-event-stream"
CFLAGS:append = " -Wl,-Bsymbolic"
EXTRA_OECMAKE += "-DBUILD_TEST_DEPS=OFF"
EXTRA_OECMAKE += "-DBUILD_TESTING=OFF"
EXTRA_OECMAKE += "-DCMAKE_MODULE_PATH=${S}/aws-c-common/cmake"
EXTRA_OECMAKE += "-DCMAKE_PREFIX_PATH=$D/usr"
EXTRA_OECMAKE += "-DCMAKE_INSTALL_PREFIX=$D/usr"
EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=${BUILD_SHARED_LIBS}"
OECMAKE_BUILDPATH += "${WORKDIR}/build"
OECMAKE_SOURCEPATH += "${S}"

do_install:append() {
	sed -i "s/BUILD_SHARED_LIBS/${BUILD_SHARED_LIBS}/" ${D}${libdir}/${BPN}/cmake/${BPN}-config.cmake
}

FILES:${PN}     = "${libdir}/lib${PN}.so.1.0.0"
FILES:${PN}-dev = "${includedir}/aws/event-stream/* \
                   ${libdir}/aws-c-event-stream/* \
                   ${libdir}/lib${PN}.so"
FILES:${PN}-dbg = "/usr/src/debug/aws-c-event-stream/* \
                   ${libdir}/.debug/lib${PN}.so.1.0.0"

BBCLASSEXTEND = "native nativesdk"
