SUMMARY = "Amazon Corretto 21"
HOMEPAGE = "https://github.com/corretto/corretto-21"

# nooelint: oelint.vars.srcurichecksum:SRC_URI[aarch64.md5sum]
SRC_URI:append:aarch64 = " https://corretto.aws/downloads/resources/${PV}/amazon-corretto-${PV}-linux-aarch64.tar.gz;name=aarch64"

# nooelint: oelint.vars.srcurichecksum:SRC_URI[x86-64.md5sum]
SRC_URI:append:x86-64 = " https://corretto.aws/downloads/resources/${PV}/amazon-corretto-${PV}-linux-x64.tar.gz;name=x86-64"

# you can find checksum here: https://github.com/corretto/corretto-21/releases since devtool upgrade can only do one arch atm.
SRC_URI[x86-64.sha256sum] = "9e421102813f97d67f26ddbbafe4d05e7e9e3bdb07ad0fc97c685470f0b993fd"
SRC_URI[aarch64.sha256sum] = "99281175896bce0168657c329f7bee5d9236c3cc2fac72d7008429f94612de5f"

# also available in master (not kirkstone) in classes-recipe: github-releases
UPSTREAM_CHECK_REGEX ?= "releases/tag/v?(?P<pver>\d+(\.\d+)+)"

UPSTREAM_CHECK_URI = "https://github.com/corretto/corretto-21/tags"

ALTERNATIVE_PRIORITY = "80"
RPROVIDES:${PN} = "java jdk-21 java-21"

# nooelint: oelint.file.underscores
require corretto-bin-common.inc

# this is used by meta-aws-tests to find this recipe for ptests, so it should stay in this file instead of moving into corretto-bin-common
inherit ptest
