SUMMARY = "Amazon Corretto 17"
HOMEPAGE = "https://github.com/corretto/corretto-17"

# nooelint: oelint.vars.srcurichecksum:SRC_URI[aarch64.md5sum]
SRC_URI:append:aarch64 = " https://corretto.aws/downloads/resources/${PV}/amazon-corretto-${PV}-linux-aarch64.tar.gz;name=aarch64"

# nooelint: oelint.vars.srcurichecksum:SRC_URI[x86-64.md5sum]
SRC_URI:append:x86-64 = " https://corretto.aws/downloads/resources/${PV}/amazon-corretto-${PV}-linux-x64.tar.gz;name=x86-64"

# you can find checksum here: https://github.com/corretto/corretto-17/releases since devtool upgrade can only do one arch atm.
SRC_URI[x86-64.sha256sum] = "077a0bb5e754119408da694f8cd82579406f7b964bbc8b6141c5b2e42800db8f"
SRC_URI[aarch64.sha256sum] = "0d18a9cae8c8b195397cfbccdde2a16207ddd2c681ab6d2b5766fe251a8346d5"

# also available in master (not kirkstone) in classes-recipe: github-releases
UPSTREAM_CHECK_REGEX ?= "releases/tag/v?(?P<pver>\d+(\.\d+)+)"

UPSTREAM_CHECK_URI = "https://github.com/corretto/corretto-17/tags"

ALTERNATIVE_PRIORITY = "70"

# nooelint: oelint.file.underscores
require corretto-bin-common.inc

# this is used by meta-aws-tests to find this recipe for ptests, so it should stay in this file instead of moving into corretto-bin-common
inherit ptest
