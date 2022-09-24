# -*- mode: Conf; -*-
SUMMARY = "AWS C HTTP"
DESCRIPTION = "C99 implementation of the HTTP/1.1 and HTTP/2 specifications"

HOMEPAGE = "https://github.com/awslabs/aws-c-http"
LICENSE = "Apache-2.0"
PROVIDES += "aws/crt-c-http"

inherit cmake

LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

BRANCH ?= "main"

SRC_URI = "git://github.com/awslabs/aws-c-http.git;protocol=https;branch=${BRANCH}"

SRCREV = "db41119471600b4bfa568824d20d183afa9d0fa6"

S = "${WORKDIR}/git"

DEPENDS = "s2n aws-c-common aws-c-cal aws-c-io aws-c-compression aws-lc"
RDEPENDS:${PN} = "s2n aws-c-common aws-c-cal aws-c-io aws-c-compression aws-lc"

AWS_C_INSTALL = "$D/usr"
OECMAKE_SOURCEPATH = "${S}"
CFLAGS:append = " -Wl,-Bsymbolic"
EXTRA_OECMAKE += "-DBUILD_TEST_DEPS=OFF"
EXTRA_OECMAKE += "-DBUILD_TESTING=OFF"
EXTRA_OECMAKE += "-DCMAKE_MODULE_PATH=${STAGING_LIBDIR}/cmake"
EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=Release"
EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=ON"
OECMAKE_BUILDPATH += "${WORKDIR}/build"
OECMAKE_SOURCEPATH += "${S}"

FILES:${PN}     += "${libdir}/lib${PN}.so.1.0.0"
FILES:${PN}-dev += "${libdir}/include/* \
                   ${libdir}/aws/http/* \
                   ${libdir}/aws-c-http/* \
                   ${libdir}/lib${PN}.so"
FILES:${PN}-dbg += "${srcdir}/debug/aws-c-http/* \
                   ${libdir}/.debug/lib${PN}.so.1.0.0"

BBCLASSEXTEND = "native nativesdk"

