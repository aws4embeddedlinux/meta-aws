# -*- mode: Conf; -*-
SUMMARY = "AWS Checksums"
DESCRIPTION = "Cross-Platform HW accelerated CRC32c and CRC32 with fallback to efficient SW implementations. C interface with language bindings for each of our SDKs"

HOMEPAGE = "https://github.com/awslabs/aws-checksums"
LICENSE = "Apache-2.0"
PROVIDES += "aws/checksums"

inherit cmake

LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

BRANCH ?= "main"

SRC_URI = "git://github.com/awslabs/aws-checksums.git;protocol=https;branch=${BRANCH}"

SRCREV = "48e7c0e01479232f225c8044d76c84e74192889d"

S = "${WORKDIR}/git"

DEPENDS = "openssl s2n aws-c-common"
RDEPENDS:${PN} = "s2n aws-c-common"

AWS_C_INSTALL = "$D/usr"
CFLAGS:append = " -Wl,-Bsymbolic"
EXTRA_OECMAKE += "-DBUILD_TEST_DEPS=OFF"
EXTRA_OECMAKE += "-DBUILD_TESTING=OFF"
EXTRA_OECMAKE += "-DCMAKE_MODULE_PATH=${STAGING_LIBDIR}/cmake"
EXTRA_OECMAKE += "-DCMAKE_PREFIX_PATH=$D/usr"
EXTRA_OECMAKE += "-DCMAKE_INSTALL_PREFIX=$D/usr"
EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=Release"
EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=ON"

FILES:${PN} = " \
    ${libdir}/*.so.1.0.0 \
    ${libdir}/*.so \
"
FILES:${PN}-dev = "${includedir}/aws/checksums/* \
                   ${libdir}/aws-checksums/* \
                   ${libdir}/lib${PN}.so"

BBCLASSEXTEND = "native nativesdk"

