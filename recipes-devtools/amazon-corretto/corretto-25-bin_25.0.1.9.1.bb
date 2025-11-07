SUMMARY = "Amazon Corretto 25"
HOMEPAGE = "https://github.com/corretto/corretto-25"

# nooelint: oelint.vars.srcurichecksum:SRC_URI[aarch64.md5sum]
SRC_URI:append:aarch64 = " https://corretto.aws/downloads/resources/${PV}/amazon-corretto-${PV}-linux-aarch64.tar.gz;name=aarch64"

# nooelint: oelint.vars.srcurichecksum:SRC_URI[x86-64.md5sum]
SRC_URI:append:x86-64 = " https://corretto.aws/downloads/resources/${PV}/amazon-corretto-${PV}-linux-x64.tar.gz;name=x86-64"

# you can find checksum here: https://github.com/corretto/corretto-25/releases since devtool upgrade can only do one arch atm.
SRC_URI[x86-64.sha256sum] = "8c1c0da1de121ce3570c5c84f92bf13cbc5a294a1fb0bb694dfa7e408d0af228"
SRC_URI[aarch64.sha256sum] = "a705c0613d3ede002ed1e172d30bf6041070f9eb091515f96aed7b9832c5fc54"

# also available in master (not kirkstone) in classes-recipe: github-releases
UPSTREAM_CHECK_REGEX ?= "releases/tag/v?(?P<pver>\d+(\.\d+)+)"

UPSTREAM_CHECK_URI = "https://github.com/corretto/corretto-25/tags"

ALTERNATIVE_PRIORITY = "90"
RPROVIDES:${PN} = "java jdk-25 java-25"

# nooelint: oelint.file.underscores
require corretto-bin-common.inc

# this is used by meta-aws-tests to find this recipe for ptests, so it should stay in this file instead of moving into corretto-bin-common
inherit ptest

UPGRADE_ARCHS = "x86-64 aarch64"
