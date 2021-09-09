# -*- mode: Conf; -*-
SUMMARY = "AWS C Common"
DESCRIPTION = "Core c99 package for AWS SDK for C. Includes cross-platform primitives, configuration, data structures, and error handling."

HOMEPAGE = "https://github.com/awslabs/aws-c-common"
LICENSE = "Apache-2.0"
PROVIDES = "aws/crt-c-common"

inherit cmake

LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

BRANCH ?= "main"
TAG ?= "v${PV}"
SRC_URI = "git://github.com/awslabs/aws-c-common.git;branch=${BRANCH};tag=${TAG};"

S = "${WORKDIR}/git"

CFLAGS:append = " -Wl,-Bsymbolic"
EXTRA_OECMAKE:append = " -DCMAKE_INSTALL_PREFIX=$D/usr"
OECMAKE_BUILDPATH = "${WORKDIR}/build"
OECMAKE_SOURCEPATH = "${S}"

INSANE_SKIP:${PN} = "installed-vs-shipped"
BBCLASSEXTEND = "native nativesdk"
