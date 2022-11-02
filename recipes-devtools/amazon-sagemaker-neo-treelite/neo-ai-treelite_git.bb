SUMMARY = "Treelite is a model compiler for efficient deployment of decision tree ensembles."
DESCRIPTION = "Neo-ai-treelite is a downstream branch of Treelite that includes vendor- and product-specific features on top of the upstream codebase."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM="file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
PACKAGES = "${PN}"

SRC_URI = "gitsm://github.com/neo-ai/treelite.git;protocol=https;branch=master"

SRCREV = "938af7867641fb09a8c93aadb66587ad9cbed9c2"

UPSTREAM_VERSION_UNKNOWN = "1"
# set to match only git_invalid_tag_regex because UPSTREAM_VERSION_UNKNOWN seems to be broken for git
UPSTREAM_CHECK_GITTAGREGEX = "git_invalid_tag_regex"

inherit cmake

S = "${WORKDIR}/git"

PACKAGES = "${PN}-dev ${PN}-dbg ${PN}-staticdev ${PN}"

FILES:${PN}-dev = "${includedir} \
                   /usr/runtime/native/*"

FILES:${PN}-staticdev = "${libdir}/*.a"

FILES:${PN} = "${libdir}/*"
