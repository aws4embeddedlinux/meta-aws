SUMMARY = "Open Deep Learning Compiler Stack"
HOMEPAGE = "https://tvm.ai/"
LICENSE = "Apache-2.0 & BSD-3-Clause"

LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e \
                    file://3rdparty/dmlc-core/LICENSE;md5=0ca7d6e8f4af26868cb42025ad83374b \
                    file://3rdparty/dlpack/LICENSE;md5=f62d4e85ba68a1574b74d97ab8dea9ab \
                    file://3rdparty/HalideIR/LICENSE;md5=9910386e68f0616e1ecf1037479fa97e \
"

PV = "0.5"

BRANCH ?= "v${PV}"

# Main TVM sources plus submodules.
SRC_URI = "git://github.com/dmlc/tvm;protocol=https;branch=${BRANCH};name=tvm \
           git://github.com/dmlc/dmlc-core;protocol=https;branch=master;destsuffix=${S}/3rdparty/dmlc-core;name=dmlc-core \
           git://github.com/dmlc/HalideIR;protocol=https;branch=master;destsuffix=${S}/3rdparty/HalideIR;name=halideir \
           git://github.com/dmlc/dlpack;protocol=https;branch=master;destsuffix=${S}/3rdparty/dlpack;name=dlpack \
           git://github.com/agauniyal/rang;protocol=https;branch=master;destsuffix=${S}/3rdparty/rang;name=rang \
           file://0001-CMakeLists-install-unit-tests.patch \
"

SRCREV_tvm = "f08015e7fde92c835907d4c9b7ad6d3f634e94a5"
SRCREV_dmlc-core = "d07fb7a443b5db8a89d65a15a024af6a425615a5"
SRCREV_halideir = "b257a9221ee1e5180d994b3488ddcc259b0ac157"
SRCREV_dlpack = "5c792cef3aee54ad8b7000111c9dc1797f327b59"
SRCREV_rang = "cabe04d6d6b05356fa8f9741704924788f0dd762"

S = "${WORKDIR}/git"

inherit setuptools3 cmake python3native

DEPENDS += "zlib llvm llvm-native gtest"

# Point to llvm-config
LLVM_RELEASE = "6.0"
EXTRA_OECMAKE += "-DUSE_LLVM=llvm-config${LLVM_RELEASE}"

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

# Versioned libs are not produced
FILES_SOLIBSDEV = ""

# Help llvm-native find target llvm-config and libs
export YOCTO_ALTERNATE_MULTILIB_NAME = "/${BASELIB}"
export YOCTO_ALTERNATE_EXE_PATH = "${STAGING_LIBDIR}/llvm${LLVM_RELEASE}/llvm-config"

BBCLASSEXTEND = "nativesdk"
