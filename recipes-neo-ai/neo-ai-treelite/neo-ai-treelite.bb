SUMMARY = "Treelite is a model compiler for efficient deployment of decision tree ensembles."
DESCRIPTION = "Neo-ai-treelite is a downstream branch of Treelite that includes vendor- and product-specific features on top of the upstream codebase."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM="file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
PACKAGES = "${PN}"

BRANCH = "master"

TL_URI       = "git://github.com/neo-ai/treelite.git"
TL_PATH      = "git/"
TL_SR        = "0972ce97687b4a1fb1262fad56232e7cc61116eb"
TL_DMLC_URI  = "git://github.com/dmlc/dmlc-core.git"
TL_DMLC_PATH = "git/dmlc-core/"
TL_DMLC_SR   = "4d49691f1a9d944c3b0aa5e63f1db3cad1f941f8"
TL_FMT_URI   = "git://github.com/fmtlib/fmt"
TL_FMT_BR    = "master"
TL_FMT_SR    = "135ab5cf71ed731fc9fa0653051e7d4884a3652f"
TL_FMT_PATH  = "git/3rdparty/fmt/"
TL_PB_URI    = "git://github.com/google/protobuf.git"
TL_PB_PATH   = "git/3rdparty/protobuf/"
TL_PB_SR     = "106ffc04be1abf3ff3399f54ccf149815b287dd9"

SRC_URI = "${TL_URI};protocol=https;branch=dev;rev=${TL_SR};destsuffix=${TL_PATH} \
           ${TL_DMLC_URI};protocol=https;rev=${TL_DMLC_SR};destsuffix=${TL_DMLC_PATH} \
           ${TL_FMT_URI};protocol=https;rev=${TL_FMT_SR};nobranch=1;destsuffix=${TL_FMT_PATH} \
           ${TL_PB_URI};protocol=https;rev=${TL_PB_SR};destsuffix=${TL_PB_PATH}"


S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE = ""

# Later, override installation to contain only required libraries?
#do_install_append() {
#    install -d ${D}${libdir}
#    install -m 0755 lib/libtreelite.so ${D}${libdir}
#    install -m 0755 runtime/native/lib/libtreelite_runtime_static.a ${D}${libdir}
#    install -m 0755 runtime/native/lib/libtreelite_runtime.so ${D}${libdir}
#}
