SUMMARY = "Amazon Corretto 21"
HOMEPAGE = "https://github.com/corretto/corretto-21"

# nooelint: oelint.vars.srcurichecksum:SRC_URI[aarch64.md5sum]
SRC_URI:append:aarch64 = " https://corretto.aws/downloads/resources/${PV}/amazon-corretto-${PV}-linux-aarch64.tar.gz;name=aarch64"

# nooelint: oelint.vars.srcurichecksum:SRC_URI[x86-64.md5sum]
SRC_URI:append:x86-64 = " https://corretto.aws/downloads/resources/${PV}/amazon-corretto-${PV}-linux-x64.tar.gz;name=x86-64"

# you can find checksum here: https://github.com/corretto/corretto-21/releases since devtool upgrade can only do one arch atm.
SRC_URI[x86-64.sha256sum] = "86be550b203970414df9d181a72607731a1a592f7c5416f649bcce9a537e7572"
SRC_URI[aarch64.sha256sum] = "c301a227277bcdbebc334ff78d9112c8aac456d35ee448979e407b1f15e53d91"

# also available in master (not kirkstone) in classes-recipe: github-releases
UPSTREAM_CHECK_REGEX ?= "releases/tag/v?(?P<pver>\d+(\.\d+)+)"

UPSTREAM_CHECK_URI = "https://github.com/corretto/corretto-21/tags"

ALTERNATIVE_PRIORITY = "80"
RPROVIDES:${PN} = "java jdk-21 java-21"

# nooelint: oelint.file.underscores
require corretto-bin-common.inc

# this is used by meta-aws-tests to find this recipe for ptests, so it should stay in this file instead of moving into corretto-bin-common
inherit ptest
