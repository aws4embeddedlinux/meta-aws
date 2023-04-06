SUMMARY = "AWS CRT Python"
DESCRIPTION = "Python bindings for the AWS Common Runtime"
HOMEPAGE = "https://github.com/awslabs/aws-crt-python"
LICENSE = "Apache-2.0"

S = "${WORKDIR}/git"

DEPENDS += "cmake-native ${PYTHON_PN}-setuptools-native s2n aws-c-common aws-c-io aws-c-mqtt aws-c-auth aws-c-http aws-checksums aws-c-event-stream"
RDEPENDS_${PN} = "python3 s2n aws-c-common aws-c-io aws-c-mqtt aws-c-auth aws-c-http aws-checksums aws-c-event-stream"
#CFLAGS_append = " -Wl,-Bsymbolic"

AWS_C_INSTALL = "${D}/usr"
DEP_INSTALL_PATH = "${D}/usr"

inherit setuptools3

LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

BRANCH ?= "main"

SRC_URI = "git://github.com/awslabs/aws-crt-python.git;branch=${BRANCH};name=aws-crt-python"
SRCREV = "2d19abb7fc360416202f9c590971c91c84dc2c72"
