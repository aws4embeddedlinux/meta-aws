# -*- mode: Conf; -*-
SUMMARY = "AWS CRT Python"
DESCRIPTION = "Python bindings for the AWS Common Runtime"
HOMEPAGE = "https://github.com/awslabs/aws-crt-python"
LICENSE = "Apache-2.0"

S = "${WORKDIR}/git"

do_configure:prepend() {
  cd ${S}
  git submodule update --init --recursive
}

DEPENDS += "cmake-native ${PYTHON_PN}-setuptools-native"
RDEPENDS:${PN} = "python3-core"
CFLAGS:append = " -Wl,-Bsymbolic"
