# -*- mode: Conf; -*-
SUMMARY = "AWS C Cal"
DESCRIPTION = "AWS Crypto Abstraction Layer: Cross-Platform, C99 wrapper for cryptography primitives."

HOMEPAGE = "https://github.com/awslabs/aws-c-cal"
LICENSE = "Apache-2.0"
PROVIDES += "aws/crt-c-cal"

inherit cmake

LIC_FILES_CHKSUM = "file://aws-c-cal/LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

BRANCH ?= "main"

SRC_URI = "git://github.com/awslabs/aws-c-common.git;protocol=https;branch=${BRANCH};destsuffix=${S}/aws-c-common;name=common \
           git://github.com/awslabs/aws-c-cal.git;protocol=https;branch=${BRANCH};destsuffix=${S}/aws-c-cal;name=cal \
           file://01-aws-c-common-strict-flags-bypass.patch \
"

SRCREV_FORMAT = "cal"
SRCREV_cal = "61d66740b1469a0caef09932621e3e92ee3967e2"
SRCREV_common = "00c91eeb186970d50690ebbdceefdeae5c31fb4c"

S = "${WORKDIR}/git"

DEPENDS = "openssl s2n aws-c-common"
RDEPENDS:${PN} = "s2n aws-c-common"

CFLAGS:append = " -Wl,-Bsymbolic"
OECMAKE_SOURCEPATH = "${S}/aws-c-cal"
OECMAKE_BUILDPATH = "${WORKDIR}/build"
EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=ON"
EXTRA_OECMAKE += "-DCMAKE_MODULE_PATH=${S}/aws-c-common/cmake"
EXTRA_OECMAKE += "-DCMAKE_PREFIX_PATH=$D/usr"
EXTRA_OECMAKE += "-DCMAKE_INSTALL_PREFIX=$D/usr"
EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=Release"

INSANE_SKIP:${PN} += "installed-vs-shipped"
BBCLASSEXTEND = "native nativesdk"

