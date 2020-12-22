SUMMARY = "AWS CRT Python"
DESCRIPTION = "Python bindings for the AWS Common Runtime"
HOMEPAGE = "https://github.com/awslabs/aws-crt-python"
LICENSE = "Apache-2.0"

S = "${WORKDIR}/git"

do_configure_prepend() {
  cd ${S}
  git submodule update --init --recursive
}

DEPENDS += "cmake-native ${PYTHON_PN}-setuptools-native"
RDEPENDS_${PN} = "python3"
CFLAGS_append = " -Wl,-Bsymbolic"
