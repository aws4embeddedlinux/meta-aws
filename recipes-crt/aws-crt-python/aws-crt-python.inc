SUMMARY = "AWS CRT Python"
DESCRIPTION = "Python bindings for the AWS Common Runtime"
HOMEPAGE = "https://github.com/awslabs/aws-crt-python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

BRANCH ?= "master"

SRC_URI = "git://github.com/awslabs/aws-crt-python.git;branch=${BRANCH};name=aws-crt-python"
SRCREV = "2d19abb7fc360416202f9c590971c91c84dc2c72"
S = "${WORKDIR}/git"

do_configure_prepend() {
  cd ${S}
  git submodule update --init --recursive
}

DEPENDS += "cmake-native ${PYTHON_PN}-setuptools-scm-native"
RDEPENDS_${PN} = "python3"
CFLAGS_append = " -Wl,-Bsymbolic"