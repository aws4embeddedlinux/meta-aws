# -*- mode: Conf; -*-
SUMMARY = "s2n"
DESCRIPTION = "s2n is a C99 implementation of the TLS/SSL protocols that is designed to be simple, small, fast, and with security as a priority."
HOMEPAGE = "https://github.com/aws/s2n-tls"
LICENSE = "Apache-2.0"
PROVIDES += "aws/s2n"

inherit cmake

LIC_FILES_CHKSUM = "file://LICENSE;md5=26d85861cd0c0d05ab56ebff38882975"

BRANCH ?= "main"
SRC_URI = "git://github.com/aws/s2n-tls.git;protocol=https;branch=${BRANCH}"

SRCREV = "15d534e8a9ca1eda6bacee514e37d08b4f38a526"
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>.*)"

S= "${WORKDIR}/git"

DEPENDS = "openssl"
CFLAGS:append = " -Wl,-Bsymbolic"

EXTRA_OECMAKE += " \
    -DBUILD_TESTING=OFF \
    -DCMAKE_BUILD_TYPE=Release \
    -DCMAKE_INSTALL_PREFIX=$D/usr \
    -DBUILD_SHARED_LIBS=ON \
"
# Fix "doesn't have GNU_HASH (didn't pass LDFLAGS?)" issue
TARGET_CC_ARCH += "${LDFLAGS}"

# Assume that warnings from upstream have already been evaluated
EXTRA_OECMAKE += "-DUNSAFE_TREAT_WARNINGS_AS_ERRORS=OFF"

FILES:${PN}-dev += "${libdir}/*/cmake"

BBCLASSEXTEND = "native nativesdk"
