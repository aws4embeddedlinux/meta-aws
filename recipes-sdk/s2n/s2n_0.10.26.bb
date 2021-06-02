# -*- mode: Conf; -*-
SUMMARY = "s2n"
DESCRIPTION = "s2n is a C99 implementation of the TLS/SSL protocols that is designed to be simple, small, fast, and with security as a priority."
HOMEPAGE = "https://github.com/awslabs/s2n"
LICENSE = "Apache-2.0"
PROVIDES += "aws/s2n"

inherit cmake

LIC_FILES_CHKSUM = "file://LICENSE;md5=26d85861cd0c0d05ab56ebff38882975"

BRANCH ?= "main"

SRC_URI = "git://github.com/awslabs/s2n.git;branch=${BRANCH};tag=v0.10.26 \
           file://0001-cmakelists-remove-warn.patch \
"

S= "${WORKDIR}/git"

DEPENDS = "openssl"
CFLAGS_append = " -Wl,-Bsymbolic"

EXTRA_OECMAKE += "-DBUILD_TESTING=OFF"
EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=Release"
EXTRA_OECMAKE += "-DCMAKE_INSTALL_PREFIX=$D/usr"
OECMAKE_BUILDPATH += "${WORKDIR}/build"
OECMAKE_SOURCEPATH += "${S}"

PACKAGES = "${PN}"
INSANE_SKIP_${PN} += "installed-vs-shipped"
BBCLASSEXTEND = "native nativesdk"
