SUMMARY = "DLR is a compact, common runtime for deep learning models and decision tree models"
DESCRIPTION = "DLR is a compact, common runtime for deep learning models and decision tree models compiled by AWS SageMaker Neo, TVM, or Treelite. DLR uses the TVM runtime, Treelite runtime, NVIDIA TensorRT™, and can include other hardware-specific runtimes. DLR provides unified Python/C++ APIs for loading and running compiled models on various devices. DLR currently supports platforms from Intel, NVIDIA, and ARM, with support for Xilinx, Cadence, and Qualcomm coming soon."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM="file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

BRANCH = "master"

#
# DLR SOURCE CONFIGURATION
#

DLR_URI      = "git://github.com/neo-ai/neo-ai-dlr.git"
DLR_PATH     = "git"
DLR_BRANCH   = "master"
DLR_NOBRANCH = "nobranch=1"
DLR_SR       = "dd9c8e806065b2c0fca97209c8bfd1cce0749ea9"

TL_URI       = "git://github.com/neo-ai/treelite.git"
TL_PATH      = "git/3rdparty/treelite"
TL_BRANCH    = "dev"
TL_NOBRANCH  = "nobranch=1"
TL_SR        = "0972ce97687b4a1fb1262fad56232e7cc61116eb"

TVM_URI      = "git://github.com/neo-ai/tvm.git"
TVM_PATH     = "git/3rdparty/tvm"
TVM_BRANCH   = "stable"
TVM_NOBRANCH = "nobranch=1"
TVM_SR       = "44779571412930681eef9e8b9d32aa845b8cc5ad"

#
# TREELITE SUBMODULE SOURCE CONFIGURATION
#

TL_DMLC_URI      = "git://github.com/dmlc/dmlc-core.git"
TL_DMLC_PATH     = "git/3rdparty/treelite/dmlc-core/"
TL_DMLC_BRANCH   = ""
TL_DMLC_NOBRANCH = "nobranch=0"
TL_DMLC_SR       = "4d49691f1a9d944c3b0aa5e63f1db3cad1f941f8"

FMT_URI          = "git://github.com/fmtlib/fmt"
FMT_PATH         = "git/3rdparty/treelite/3rdparty/fmt/"
FMT_BRANCH       = "master"
FMT_NOBRANCH     = "nobranch=1"
FMT_SR           = "135ab5cf71ed731fc9fa0653051e7d4884a3652f"

PB_URI           = "git://github.com/google/protobuf.git"
PB_PATH          = "git/3rdparty/treelite/3rdparty/protobuf/"
PB_BRANCH        = ""
PB_NOBRANCH      = "nobranch=0"
PB_SR            = "106ffc04be1abf3ff3399f54ccf149815b287dd9"

#
# TVM SUBMODULE SOURCE CONFIGURATION
#

TVM_DMLC_URI      = "git://github.com/dmlc/dmlc-core.git"
TVM_DMLC_PATH     = "git/3rdparty/tvm/3rdparty/dmlc-core/"
TVM_DMLC_BRANCH   = ""
TVM_DMLC_NOBRANCH = "nobranch=1"
TVM_DMLC_SR       = "4d49691f1a9d944c3b0aa5e63f1db3cad1f941f8"

HIR_URI           = "git://github.com/dmlc/HalideIR"
HIR_PATH          = "git/3rdparty/tvm/3rdparty/HalideIR/"
HIR_BRANCH        = ""
HIR_NOBRANCH      = "nobranch=0"
HIR_SR            = "e4a4c02764d37c9c3db0d64c4996651a3ef9513c"

DLPACK_URI        = "git://github.com/dmlc/dlpack.git"
DLPACK_PATH       = "git/3rdparty/tvm/3rdparty/dlpack/"
DLPACK_BRANCH     = ""
DLPACK_NOBRANCH   = "nobranch=0"
DLPACK_SR         = "bee4d1dd8dc1ee4a1fd8fa6a96476c2f8b7492a3"

#
# This was configured for a more modern rev - not used in this
# configuration
#
RANG_URI          = "git://github.com/agauniyal/rang.git"
RANG_PATH         = "git/3rdparty/tvm/3rdparty/rang/"
RANG_BRANCH       = ""
RANG_NOBRANCH     = "nobranch=1"
RANG_SR           = "cabe04d6d6b05356fa8f9741704924788f0dd762"

#
# SOURCE CHECKOUT
#

SRC_URI = "${DLR_URI};protocol=https;rev=${DLR_SR};${DLR_NOBRANCH};destsuffix=${DLR_PATH} \
           ${TVM_URI};protocol=https;rev=${TVM_SR};${TVM_NOBRANCH};destsuffix=${TVM_PATH} \
           ${TL_URI};protocol=https;branch=${TL_BRANCH};rev=${TL_SR};${TL_NOBRANCH};destsuffix=${TL_PATH} \
           ${TVM_DMLC_URI};protocol=https;rev=${TVM_DMLC_SR};destsuffix=${TVM_DMLC_PATH} \
           ${HIR_URI};protocol=https;rev=${HIR_SR};destsuffix=${HIR_PATH} \
           ${DLPACK_URI};protocol=https;rev=${DLPACK_SR};destsuffix=${DLPACK_PATH} \
           ${TL_DMLC_URI};protocol=https;rev=${TL_DMLC_SR};destsuffix=${TL_DMLC_PATH} \
           ${FMT_URI};protocol=https;rev=${FMT_SR};${FMT_NOBRANCH};destsuffix=${FMT_PATH} \
           ${PB_URI};protocol=https;rev=${PB_SR};destsuffix=${PB_PATH}"


S = "${WORKDIR}/git"

EXTRA_OECMAKE += ""

FILES_${PN}     = "/usr/lib/libdlr.so"
FILES_${PN}-dev = "${includedir}"

DISTUTILS_BUILD_DIR = "${WORKDIR}/git/python"

inherit cmake setuptools3


do_install() {

    install -d ${D}${libdir}
    install -m 0755 ${WORKDIR}/git/lib/libdlr.so ${D}${libdir}

    # Manually install python modules since distutils3_do_install()
    # can't run in conjunction with installing other artifacts.

    install -d ${D}${libdir}/${PYTHON_PN}/site-packages/dlr
    install -m 0755 ${WORKDIR}/git/python/dlr/__init__.py ${D}${libdir}/${PYTHON_PN}/site-packages/dlr
    install -m 0755 ${WORKDIR}/git/python/dlr/api.py      ${D}${libdir}/${PYTHON_PN}/site-packages/dlr
    install -m 0755 ${WORKDIR}/git/python/dlr/libpath.py  ${D}${libdir}/${PYTHON_PN}/site-packages/dlr

    #install -d ${D}${libdir}
    #install -m 0644 ${S}/lib/* ${D}${libdir}

    #install -d ${D}${includedir}
    #install -m 0644 ${S}/include/* ${D}${includedir}

    #cd ${S}/python
    #distutils3_do_install
    #cd ${B}

    # setup.py install some libs under datadir, but we don't need them, so remove.
    #rm ${D}${datadir}/dlr/*.so

    # Now install python test scripts
    #install -d ${D}${datadir}/dlr/tests
    #install -m 0644 ${S}/tests/python/integration/*.py ${D}${datadir}/dlr/tests/

}

BBCLASSEXTEND = "native nativesdk"
