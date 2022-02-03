# -*- mode: Conf; -*-
SUMMARY = "AWS C Auth"
DESCRIPTION = "C99 library implementation of AWS client-side authentication: standard credentials providers and signing."

HOMEPAGE = "https://github.com/awslabs/aws-c-auth"
LICENSE = "Apache-2.0"
PROVIDES += "aws/crt-c-auth"

inherit cmake

LIC_FILES_CHKSUM = "file://aws-c-auth/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

BRANCH ?= "main"
SRC_URI = "git://github.com/awslabs/aws-c-common.git;protocol=https;branch=${BRANCH};destsuffix=${S}/aws-c-common;name=common \
           git://github.com/awslabs/aws-c-auth.git;protocol=https;branch=${BRANCH};destsuffix=${S}/aws-c-auth;name=auth \
"

SRCREV_FORMAT="auth"
SRCREV_auth = "cc3d80c962787457258745fb8f5bb56bb2c4442d"
SRCREV_common = "2a28532d6f13435907ae200a5aea449c01e79149"

S= "${WORKDIR}/git"

DEPENDS = "openssl s2n aws-c-common aws-c-cal aws-c-io aws-c-compression aws-c-http"
RDEPENDS:${PN} = "s2n aws-c-common aws-c-cal aws-c-io aws-c-compression aws-c-http"

CFLAGS:append = " -Wl,-Bsymbolic"

OECMAKE_SOURCEPATH = "${S}/aws-c-auth"
EXTRA_OECMAKE += "-DBUILD_TESTING=OFF"
EXTRA_OECMAKE += "-DCMAKE_MODULE_PATH=${S}/aws-c-common/cmake"
EXTRA_OECMAKE += "-DCMAKE_PREFIX_PATH=$D${prefix}"
EXTRA_OECMAKE += "-DCMAKE_INSTALL_PREFIX=$D${prefix}"
EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=ON"
EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=Release"

OECMAKE_BUILDPATH += "${WORKDIR}/build"
OECMAKE_SOURCEPATH += "${S}"

FILES:${PN} = " \
    ${libdir}/*.so.1.0.0 \
    ${libdir}/*.so \
"
FILES:${PN}-dev = "${includedir}/aws/auth/* \
                   ${libdir}/aws-c-auth/* \
                   ${libdir}/lib${PN}.so"
FILES:${PN}-dbg = "${prefix}/src/debug/aws-c-auth/* \
                   ${libdir}/.debug/lib${PN}.so.1.0.0"

BBCLASSEXTEND = "native nativesdk"
