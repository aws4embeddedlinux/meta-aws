# -*- mode: Conf; -*-
SUMMARY = "AWS C S3"
DESCRIPTION = "C99 library implementation for communicating with the S3 service, designed for maximizing throughput on high bandwidth EC2 instances."

HOMEPAGE = "https://github.com/awslabs/aws-c-s3"
LICENSE = "Apache-2.0"
PROVIDES += "aws/crt-c-s3"

inherit cmake

LIC_FILES_CHKSUM = "file://aws-c-s3/LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

BRANCH ?= "main"

SRC_URI = "git://github.com/awslabs/aws-c-common.git;protocol=https;branch=${BRANCH};destsuffix=${S}/aws-c-common;name=common \
           git://github.com/awslabs/aws-c-s3.git;protocol=https;branch=${BRANCH};destsuffix=${S}/aws-c-s3;name=s3 \
"
SRCREV_FORMAT = "s3"
SRCREV_s3 = "dca8df1acb75d61db1b309f961ff20994d42c14f"
SRCREV_common = "2a28532d6f13435907ae200a5aea449c01e79149"

S= "${WORKDIR}/git"

DEPENDS = "openssl aws-c-auth aws-c-http"
RDEPENDS:${PN} = "aws-c-auth aws-c-http"

CFLAGS:append = " -Wl,-Bsymbolic"

OECMAKE_SOURCEPATH = "${S}/aws-c-s3"
EXTRA_OECMAKE += "-DBUILD_TESTING=OFF"
EXTRA_OECMAKE += "-DCMAKE_MODULE_PATH=${S}/aws-c-common/cmake"
EXTRA_OECMAKE += "-DCMAKE_PREFIX_PATH=$D${prefix}"
EXTRA_OECMAKE += "-DCMAKE_INSTALL_PREFIX=$D${prefix}"
EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=ON"
OECMAKE_BUILDPATH += "${WORKDIR}/build"
OECMAKE_SOURCEPATH += "${S}"


FILES:${PN} = " \
    ${libdir}/*.so.1.0.0 \
    ${libdir}/*.so.0unstable \
    ${libdir}/*.so \
"
FILES:${PN}-dev = "${includedir}/aws/s3/* \
                   ${libdir}/aws-c-s3/* \
                   ${libdir}/lib${PN}.so"
FILES:${PN}-dbg = "${prefix}/src/debug/aws-c-s3/* \
                   ${libdir}/.debug/lib${PN}.so.1.0.0"

BBCLASSEXTEND = "native nativesdk"
