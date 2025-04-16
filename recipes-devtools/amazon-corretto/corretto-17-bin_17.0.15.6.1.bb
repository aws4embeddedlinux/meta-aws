SUMMARY = "Amazon Corretto 17"
HOMEPAGE = "https://github.com/corretto/corretto-17"

# nooelint: oelint.vars.srcurichecksum:SRC_URI[aarch64.md5sum]
SRC_URI:append:aarch64 = " https://corretto.aws/downloads/resources/${PV}/amazon-corretto-${PV}-linux-aarch64.tar.gz;name=aarch64"

# nooelint: oelint.vars.srcurichecksum:SRC_URI[x86-64.md5sum]
SRC_URI:append:x86-64 = " https://corretto.aws/downloads/resources/${PV}/amazon-corretto-${PV}-linux-x64.tar.gz;name=x86-64"

# you can find checksum here: https://github.com/corretto/corretto-17/releases since devtool upgrade can only do one arch atm.
SRC_URI[aarch64.sha256sum] = "dec11e9862ef04f84949408044703ffd8c03e83850b3b058d56750ea14c45b69"
SRC_URI[x86-64.sha256sum] = "7860c1d3ba126439d463fe272b54673a9384ee0b6e1cbdbc8ba2734935042cca"

# also available in master (not kirkstone) in classes-recipe: github-releases
UPSTREAM_CHECK_REGEX ?= "releases/tag/v?(?P<pver>\d+(\.\d+)+)"

UPSTREAM_CHECK_URI = "https://github.com/corretto/corretto-17/tags"

ALTERNATIVE_PRIORITY = "70"
RPROVIDES:${PN} = "java jdk-17 java-17"

# nooelint: oelint.file.underscores
require corretto-bin-common.inc

# this is used by meta-aws-tests to find this recipe for ptests, so it should stay in this file instead of moving into corretto-bin-common
inherit ptest
