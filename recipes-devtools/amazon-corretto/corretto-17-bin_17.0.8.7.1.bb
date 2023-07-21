SUMMARY = "Amazon Corretto 17"
HOMEPAGE = "https://github.com/corretto/corretto-17"

# nooelint: oelint.vars.srcurichecksum:SRC_URI[aarch64.md5sum]
SRC_URI:append:aarch64 = " https://corretto.aws/downloads/resources/${PV}/amazon-corretto-${PV}-linux-aarch64.tar.gz;name=aarch64"

# nooelint: oelint.vars.srcurichecksum:SRC_URI[x86-64.md5sum]
SRC_URI:append:x86-64 = " https://corretto.aws/downloads/resources/${PV}/amazon-corretto-${PV}-linux-x64.tar.gz;name=x86-64"

# you can find checksum here: https://github.com/corretto/corretto-17/releases since devtool upgrade can only do one arch atm.
SRC_URI[x86-64.sha256sum] = "6cc5e6ed4913fe51d3efd96684124522d0bfe75c263a31cf06340e0dfa81f4cb"

# also available in master (not kirkstone) in classes-recipe: github-releases
UPSTREAM_CHECK_REGEX ?= "releases/tag/v?(?P<pver>\d+(\.\d+)+)"

UPSTREAM_CHECK_URI = "https://github.com/corretto/corretto-17/tags"

# nooelint: oelint.file.underscores
require corretto-bin-common.inc
