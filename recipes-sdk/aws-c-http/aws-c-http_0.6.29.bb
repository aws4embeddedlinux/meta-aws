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

SRCREV = "4e82c1e5022d3dd4d6eda4b8fa9cdba6e6def050"

S = "${WORKDIR}/git"

DEPENDS = "s2n aws-c-common aws-c-cal aws-c-io aws-c-compression aws-lc"
RDEPENDS:${PN} = "s2n aws-c-common aws-c-cal aws-c-io aws-c-compression aws-lc"

AWS_C_INSTALL = "$D/usr"
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

