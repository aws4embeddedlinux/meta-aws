# -*- mode: Conf; -*-
SUMMARY = "AWS C Auth"
DESCRIPTION = "C99 library implementation of AWS client-side authentication: standard credentials providers and signing."

HOMEPAGE = "https://github.com/awslabs/aws-c-auth"
LICENSE = "Apache-2.0"
PROVIDES += "aws/crt-c-auth"

inherit cmake

LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

BRANCH ?= "main"
SRC_URI = "git://github.com/awslabs/aws-c-auth.git;protocol=https;branch=${BRANCH}"

SRCREV = "bad1066f0a93f3a7df86c94cc03076fa6b901bd2"
S= "${WORKDIR}/git"

DEPENDS = "openssl s2n aws-c-common aws-c-cal aws-c-io aws-c-compression aws-c-http aws-c-sdkutils"
RDEPENDS:${PN} = "s2n aws-c-common aws-c-cal aws-c-io aws-c-compression aws-c-http aws-c-sdkutils"

CFLAGS:append = " -Wl,-Bsymbolic"

EXTRA_OECMAKE += " \
    -DBUILD_TESTING=OFF \
    -DCMAKE_MODULE_PATH=${STAGING_LIBDIR}/cmake \
    -DCMAKE_PREFIX_PATH=$D/usr \
    -DCMAKE_INSTALL_PREFIX=$D/usr \
    -DBUILD_SHARED_LIBS=ON \
    -DCMAKE_BUILD_TYPE=Release \
"

FILES:${PN}-dev += "${libdir}/*/cmake"

BBCLASSEXTEND = "native nativesdk"
