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

SRCREV = "0c7b8b5f30b521584b8d432fd88e0bce9fe619c7"

S = "${WORKDIR}/git"

DEPENDS = "s2n aws-c-common aws-c-cal aws-c-io aws-c-compression aws-lc"
RDEPENDS:${PN} = "s2n aws-c-common aws-c-cal aws-c-io aws-c-compression aws-lc"

AWS_C_INSTALL = "$D/usr"
OECMAKE_SOURCEPATH = "${S}"
CFLAGS:append = " -Wl,-Bsymbolic"
EXTRA_OECMAKE += " \
    -DBUILD_TEST_DEPS=OFF \
    -DBUILD_TESTING=OFF \
    -DCMAKE_MODULE_PATH=${STAGING_LIBDIR}/cmake \
    -DCMAKE_BUILD_TYPE=Release \
    -DBUILD_SHARED_LIBS=ON \
"

FILES:${PN}-dev += "${libdir}/*/cmake"

BBCLASSEXTEND = "native nativesdk"

