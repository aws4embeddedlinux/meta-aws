# -*- mode: Conf; -*-
SUMMARY = "Open Deep Learning Compiler Stack"
HOMEPAGE = "https://tvm.ai/"
LICENSE = "Apache-2.0 & BSD-3-Clause"

LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e \
"

RDEPENDS:${PN} = " python3-decorator \
"

PV = "0.5"

BRANCH = "main"

# Main TVM sources plus submodules.
SRC_URI = "gitsm://github.com/dmlc/tvm;protocol=https;branch=${BRANCH} \
           file://0001-CMakeLists-install-unit-tests.patch \
"

SRCREV = "76c239269935288e51fbce14f135d75ad9742b2a"

S = "${WORKDIR}/git"

DISTUTILS_SETUP_PATH = "${S}/python"

inherit distutils3 cmake python3native

DEPENDS += "zlib llvm-native googletest python3-setuptools-native"

# This is how we enable the unittests
export GTEST_LIB = "${STAGING_LIBDIR}"

do_install() {
    cmake_do_install

    cd ${S}/python
    TVM_LIBRARY_PATH=${D}${libdir} distutils3_do_install
    cd ${B}

    # setup.py install some libs under datadir, but we don't need them, so remove.
    rm ${D}${datadir}/tvm/*.so
}

PACKAGES =+ "${PN}-tests"
FILES:${PN}-tests = "${datadir}/tvm/cpptest"
RDEPENDS:${PN}-tests += "${PN} "

# Versioned libs are not produced
FILES_SOLIBSDEV = ""

# Help llvm-native find target llvm-config and libs
export YOCTO_ALTERNATE_MULTILIB_NAME = "/${BASELIB}"
export YOCTO_ALTERNATE_EXE_PATH = "${STAGING_LIBDIR}/llvm${LLVM_RELEASE}/llvm-config"

BBCLASSEXTEND = "nativesdk"
