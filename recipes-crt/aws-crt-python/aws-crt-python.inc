SUMMARY = "AWS CRT Python"
DESCRIPTION = "Python bindings for the AWS Common Runtime"
HOMEPAGE = "https://github.com/awslabs/aws-crt-python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI[md5sum] = "a17cbf9401b8b95277c9d02d9b40fbb5"
SRC_URI[sha256sum] = "fa787d248505edae6640442134c032dfe22fb73df40b834341d66409f5275ea4"

PYPI_PACKAGE = "awscrt"

inherit pypi setuptools3

CFLAGS_append = " -Wl,-Bsymbolic"

DEPENDS += "cmake-native ${PYTHON_PN}-setuptools-scm-native"

RDEPENDS_${PN} += " \
    python3 \
"

# LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

# BRANCH ?= "master"

# SRC_URI = "git://github.com/awslabs/aws-crt-python.git;branch=${BRANCH};name=aws-crt-python"
# # SRCREV = "${AUTOREV}"
# SRCREV = "b41a9145e5be25c2e33491fb6055bb037dab51a1"

# S = "${WORKDIR}/git"

# do_configure_prepend() {
#   cd ${S}
#   git submodule update --init --recursive
# }
