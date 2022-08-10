# -*- mode: Conf; -*-
SUMMARY = "Open Deep Learning Compiler Stack"
HOMEPAGE = "https://tvm.ai/"
LICENSE = "Apache-2.0 & BSD-3-Clause"

LIC_FILES_CHKSUM = "file://LICENSE;md5=f30f7a1bcf7728eb568504d96cee7b09"

BRANCH = "v0.9.0"

SRC_URI = "gitsm://github.com/apache/tvm;protocol=https;branch=${BRANCH}"

SRCREV = "d361585965b95acfb35563b92a2011fe8059876c"

# exclude .dev releases
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+\.\d+.\d+)(?<!dev)$"

S = "${WORKDIR}/git"

inherit setuptools3_legacy cmake python3native

DEPENDS += "zlib llvm llvm-native googletest"

# Point to llvm-config
LLVM_RELEASE = "14.0.6"
EXTRA_OECMAKE += "-DUSE_LLVM=llvm-config${LLVM_RELEASE}"

# libbacktrace has cross build problems
EXTRA_OECMAKE += "-DUSE_LIBBACKTRACE=0"

# This is how we enable the unittests
export GTEST_LIB = "${STAGING_LIBDIR}"

do_configure:prepend() {
 ln -sfr ${STAGING_LIBDIR} ${STAGING_LIBDIR}/lib 
}

SETUPTOOLS_SETUP_PATH = "${S}/python"

do_install() {
    cmake_do_install

    # Install tvm library to a path where setup.py can find it
    # this should be done by setting TVM_LIBRARY_PATH, but this does not work!
    install -m 0644 ${B}/libtvm.so ${S}/../
    install -m 0644 ${B}/libtvm_runtime.so ${S}/../

    setuptools3_legacy_do_install
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

