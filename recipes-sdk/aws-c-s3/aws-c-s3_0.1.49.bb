# -*- mode: Conf; -*-
SUMMARY = "AWS C S3"
DESCRIPTION = "C99 library implementation for communicating with the S3 service, designed for maximizing throughput on high bandwidth EC2 instances."

HOMEPAGE = "https://github.com/awslabs/aws-c-s3"
LICENSE = "Apache-2.0"
PROVIDES += "aws/crt-c-s3"

inherit cmake

LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

BRANCH ?= "main"

SRC_URI = "git://github.com/awslabs/aws-c-s3.git;protocol=https;branch=${BRANCH}"

SRCREV = "d70205ee28bc0bcfde69bed5370525bffc032f34"

S= "${WORKDIR}/git"

DEPENDS = "openssl aws-c-auth aws-c-http aws-checksums"
RDEPENDS:${PN} = "aws-c-auth aws-c-http aws-checksums"

CFLAGS:append = " -Wl,-Bsymbolic"

EXTRA_OECMAKE += "-DBUILD_TESTING=OFF"
EXTRA_OECMAKE += "-DCMAKE_MODULE_PATH=${STAGING_LIBDIR}/cmake"
EXTRA_OECMAKE += "-DCMAKE_PREFIX_PATH=$D/usr"
EXTRA_OECMAKE += "-DCMAKE_INSTALL_PREFIX=$D/usr"
EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=ON"

FILES:${PN}     = "${libdir}/lib${PN}.so.1.0.0 \
                   ${libdir}/lib${PN}.so.0unstable"
FILES:${PN}-dev = "${includedir}/aws/s3/* \
                   ${libdir}/aws-c-s3/* \
                   ${libdir}/lib${PN}.so"
FILES:${PN}-dbg = "/usr/src/debug/aws-c-s3/* \
                   ${libdir}/.debug/lib${PN}.so.1.0.0"

BBCLASSEXTEND = "native nativesdk"
