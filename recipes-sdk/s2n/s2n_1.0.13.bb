# -*- mode: Conf; -*-
SUMMARY = "s2n"
DESCRIPTION = "s2n is a C99 implementation of the TLS/SSL protocols that is designed to be simple, small, fast, and with security as a priority."
HOMEPAGE = "https://github.com/aws/s2n-tls"
LICENSE = "Apache-2.0"
PROVIDES += "aws/s2n"

inherit cmake

LIC_FILES_CHKSUM = "file://LICENSE;md5=26d85861cd0c0d05ab56ebff38882975"

BRANCH ?= "main"
SRC_URI = "git://github.com/aws/s2n-tls.git;protocol=https;branch=${BRANCH} \
           file://0002-cmakelists-remove-warn.patch \
"
SRCREV = "6de5de43f4def520c6e06d8a50681d01837cc640"

S= "${WORKDIR}/git"

DEPENDS = "openssl"
CFLAGS:append = " -Wl,-Bsymbolic"

EXTRA_OECMAKE += "-DBUILD_TESTING=OFF"
EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=Release"
EXTRA_OECMAKE += "-DCMAKE_INSTALL_PREFIX=$D${prefix}"
EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=ON"
# Fix "doesn't have GNU_HASH (didn't pass LDFLAGS?)" issue
TARGET_CC_ARCH += "${LDFLAGS}"

# Assume that warnings from upstream have already been evaluated
EXTRA_OECMAKE += "-DUNSAFE_TREAT_WARNINGS_AS_ERRORS=OFF"
OECMAKE_BUILDPATH += "${WORKDIR}/build"
OECMAKE_SOURCEPATH += "${S}"

FILES:${PN} += "${libdir}/libs2n.so"


FILES:${PN}     = "${libdir}/libs2n.so"
FILES:${PN}-dev = "${includedir}/s2n.h \
                   ${libdir}/s2n/*"
FILES:${PN}-dbg = "/usr/src/debug/s2n/* \
                   ${libdir}/.debug/libs2n.so"

# Notify that libraries are not versioned
FILES_SOLIBSDEV = ""

BBCLASSEXTEND = "native nativesdk"
