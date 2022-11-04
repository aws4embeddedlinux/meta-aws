# -*- mode: Conf; -*-
SUMMARY = "AWS Crt Cpp"
DESCRIPTION = "C++ wrapper around the aws-c-* libraries. Provides Cross-Platform Transport Protocols and SSL/TLS implementations for C++."

HOMEPAGE = "https://github.com/awslabs/aws-crt-cpp"
LICENSE = "Apache-2.0"
PROVIDES += "aws/crt-cpp"

inherit cmake

LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

BRANCH ?= "main"

SRC_URI = "git://github.com/awslabs/aws-crt-cpp.git;protocol=https;branch=${BRANCH}"

SRCREV = "ce61165fd57b1fd714dbb15499edef1fb8be940e"

S = "${WORKDIR}/git"

DEPENDS = "s2n aws-c-common aws-c-io aws-c-mqtt aws-c-auth aws-c-http aws-checksums aws-c-event-stream aws-c-s3"
RDEPENDS:${PN} = "s2n aws-c-common"

CFLAGS:append = " -Wl,-Bsymbolic"
EXTRA_OECMAKE += " \
    -DCMAKE_MODULE_PATH=${STAGING_LIBDIR}/cmake \
    -DCMAKE_PREFIX_PATH=$D/usr \
    -DCMAKE_INSTALL_PREFIX=$D/usr \
    -DCMAKE_BUILD_TYPE=Release \
    -DBUILD_DEPS=OFF \
    -DBUILD_SHARED_LIBS=ON \
"

FILES:${PN} += "${libdir}/libaws-crt-cpp.so"
FILES:${PN}-dev = "${includedir}/aws/crt/* \
                   ${libdir}/aws-crt-cpp/* \
                   ${includedir}/aws/iot/*"

# Notify that libraries are not versioned
FILES_SOLIBSDEV = ""

BBCLASSEXTEND = "native nativesdk"

