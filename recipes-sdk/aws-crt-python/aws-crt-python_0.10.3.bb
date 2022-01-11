# -*- mode: Conf; -*-
SUMMARY = "AWS CRT Python"
DESCRIPTION = "Python bindings for the AWS Common Runtime"
HOMEPAGE = "https://github.com/awslabs/aws-crt-python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

BRANCH ?= "main"
SRC_URI = "git://github.com/awslabs/aws-crt-python.git;protocol=https;branch=${BRANCH}"
SRCREV = "d020399725065de2999c6743063d7fabf5e0dd9a"

S = "${WORKDIR}/git"

inherit setuptools3

AWS_C_INSTALL = "${D}/usr/lib;${S}/source"
DEPENDS += "cmake-native ${PYTHON_PN}-setuptools-native s2n aws-c-common aws-c-io aws-c-mqtt aws-c-auth aws-c-http aws-checksums aws-c-event-stream aws-c-s3"
RDEPENDS:${PN} = "python3 s2n aws-c-common aws-c-io aws-c-mqtt aws-c-auth aws-c-http aws-checksums aws-c-event-stream"
CFLAGS:append = " -Wl,-Bsymbolic"




