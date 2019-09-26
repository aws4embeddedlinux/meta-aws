SUMMARY = "TVM is a compiler stack for deep learning systems"
DESCRIPTION = "Neo-AI/TVM is a downstream branch of TVM that includes vendor- and product-specific features on top of the upstream codebase."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM="file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
PACKAGES = "${PN}"

BRANCH = "master"

TVM_URI         = "git://github.com/neo-ai/tvm.git"
TVM_PATH        = "git/"
TVM_SR          = "d1c7a3d2f6ab701ff7315f23c1d528185975bfb0"

TVM_DMLC_URI    = "git://github.com/dmlc/dmlc-core.git"
TVM_DMLC_PATH   = "git/3rdparty/dmlc-core/"
TVM_DMLC_SR     = "3943914eed66470bd010df581e29e4dca4f7df6f"

TVM_HIR_URI     = "git://github.com/dmlc/HalideIR"
TVM_HIR_PATH    = "git/3rdparty/HalideIR/"
TVM_HIR_SR      = "c4e5bc77bd7bca05e45664b35c6ce88246c43b1b"

TVM_DLPACK_URI  = "git://github.com/dmlc/dlpack.git"
TVM_DLPACK_PATH = "git/3rdparty/dlpack/"
TVM_DLPACK_SR   = "0acb731e0e43d15deee27b66f10e4c5b4e667913"

TVM_RANG_URI    = "git://github.com/agauniyal/rang.git"
TVM_RANG_PATH   = "git/3rdparty/rang/"
TVM_RANG_SR     = "cabe04d6d6b05356fa8f9741704924788f0dd762"


SRC_URI = "${TVM_URI};protocol=https;rev=${TVM_SR};nobranch=1;destsuffix=${TVM_PATH} \
           ${TVM_DMLC_URI};protocol=https;rev=${TVM_DMLC_SR};destsuffix=${TVM_DMLC_PATH} \
           ${TVM_HIR_URI};protocol=https;rev=${TVM_HIR_SR};destsuffix=${TVM_HIR_PATH} \
           ${TVM_DLPACK_URI};protocol=https;rev=${TVM_DLPACK_SR};destsuffix=${TVM_DLPACK_PATH} \
           ${TVM_RANG_URI};protocol=https;rev=${TVM_RANG_SR};nobranch=1;destsuffix=${TVM_RANG_PATH}"


S = "${WORKDIR}/git"

EXTRA_OECMAKE += "-C ${WORKDIR}/git/cmake/config.cmake ${WORKDIR}/git"

inherit cmake

