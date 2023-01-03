SUMMARY = "AWS C HTTP"
DESCRIPTION = "C99 implementation of the HTTP/1.1 and HTTP/2 specifications"

HOMEPAGE = "https://github.com/awslabs/aws-c-http"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS += "\
    aws-c-cal \
    aws-c-common \
    aws-c-compression \
    aws-c-io \
    aws-lc \
    s2n \
    "

PROVIDES += "aws/crt-c-http"

BRANCH ?= "main"

SRC_URI = "git://github.com/awslabs/aws-c-http.git;protocol=https;branch=${BRANCH}"

SRCREV = "69b952ef08cc1d2766374cb7702f098c01e30250"

S = "${WORKDIR}/git"

inherit cmake

AWS_C_INSTALL = "$D/usr"
CFLAGS:append = " -Wl,-Bsymbolic"
EXTRA_OECMAKE += "\
    -DBUILD_TEST_DEPS=OFF \
    -DBUILD_TESTING=OFF \
    -DCMAKE_MODULE_PATH=${STAGING_LIBDIR}/cmake \
    -DCMAKE_BUILD_TYPE=Release \
    -DBUILD_SHARED_LIBS=ON \
"

FILES:${PN}-dev += "${libdir}/*/cmake"

BBCLASSEXTEND = "native nativesdk"

