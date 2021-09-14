# -*- mode: Conf; -*-
SUMMARY = "AWS C HTTP"
DESCRIPTION = "C99 implementation of the HTTP/1.1 and HTTP/2 specifications"

HOMEPAGE = "https://github.com/awslabs/aws-c-http"
LICENSE = "Apache-2.0"
PROVIDES += "aws/crt-c-http"

inherit cmake

LIC_FILES_CHKSUM = "file://aws-c-http/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

BRANCH ?= "main"
TAG ?= "v${PV}"
TAG_COMMON ?= "v0.6.8"

SRC_URI = "git://github.com/awslabs/aws-c-common.git;branch=${BRANCH};tag=${TAG_COMMON};destsuffix=${S}/aws-c-common;name=common \
           git://github.com/awslabs/aws-c-http.git;branch=${BRANCH};tag=${TAG};destsuffix=${S}/aws-c-http;name=http \
"

S = "${WORKDIR}/git"

DEPENDS = "openssl s2n aws-c-common aws-c-cal aws-c-io aws-c-compression"
RDEPENDS:${PN} = "s2n aws-c-common aws-c-cal aws-c-io aws-c-compression"

AWS_C_INSTALL = "$D/usr"
OECMAKE_SOURCEPATH = "${S}/aws-c-http"
CFLAGS:append = " -Wl,-Bsymbolic"
EXTRA_OECMAKE += "-DBUILD_TEST_DEPS=OFF"
EXTRA_OECMAKE += "-DBUILD_TESTING=OFF"
EXTRA_OECMAKE += "-DCMAKE_MODULE_PATH=${S}/aws-c-common/cmake"
EXTRA_OECMAKE += "-DCMAKE_PREFIX_PATH=$D/usr"
EXTRA_OECMAKE += "-DCMAKE_INSTALL_PREFIX=$D/usr"
EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=Release"
EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=ON"
OECMAKE_BUILDPATH += "${WORKDIR}/build"
OECMAKE_SOURCEPATH += "${S}"

FILES:${PN}     = "${libdir}/lib${PN}.so.1.0.0"
FILES:${PN}-dev = "${includedir}/aws/http/* \
                   ${libdir}/aws-c-http/* \
                   ${libdir}/lib${PN}.so"
FILES:${PN}-dbg = "/usr/src/debug/aws-c-http/* \
                   ${libdir}/.debug/lib${PN}.so.1.0.0"

BBCLASSEXTEND = "native nativesdk"

