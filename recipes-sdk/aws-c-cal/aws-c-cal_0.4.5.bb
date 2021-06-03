# -*- mode: Conf; -*-
SUMMARY = "AWS C Cal"
DESCRIPTION = "AWS Crypto Abstraction Layer: Cross-Platform, C99 wrapper for cryptography primitives."

HOMEPAGE = "https://github.com/awslabs/aws-c-cal"
LICENSE = "Apache-2.0"
PROVIDES += "aws/crt-c-cal"

inherit cmake

LIC_FILES_CHKSUM = "file://aws-c-cal/LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

BRANCH ?= "main"

SRC_URI = "git://github.com/awslabs/aws-c-common.git;branch=${BRANCH};destsuffix=${S}/aws-c-common;tag=v0.4.67 \
           git://github.com/awslabs/aws-c-cal.git;branch=${BRANCH};destsuffix=${S}/aws-c-cal;tag=v0.4.5 \
           file://01-aws-c-common-strict-flags-bypass.patch \
"

S = "${WORKDIR}/git"

DEPENDS = "openssl s2n aws-c-common"
RDEPENDS_${PN} = "s2n aws-c-common"

CFLAGS_append = " -Wl,-Bsymbolic"
OECMAKE_SOURCEPATH = "${S}/aws-c-cal"
OECMAKE_BUILDPATH = "${WORKDIR}/build"
EXTRA_OECMAKE += "-DCMAKE_MODULE_PATH=${S}/aws-c-common/cmake"
EXTRA_OECMAKE += "-DCMAKE_PREFIX_PATH=$D/usr"
EXTRA_OECMAKE += "-DCMAKE_INSTALL_PREFIX=$D/usr"
EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=Release"

PACKAGES = "${PN}"
INSANE_SKIP_${PN} += "installed-vs-shipped"
BBCLASSEXTEND = "native nativesdk"

