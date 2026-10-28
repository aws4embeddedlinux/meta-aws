SUMMARY = "Amazon Corretto 17"
HOMEPAGE = "https://github.com/corretto/corretto-17"

COMPATIBLE_MACHINE:armv7a = "null"
COMPATIBLE_MACHINE:armv7ve = "null"
COMPATIBLE_MACHINE:x86 = "null"
COMPATIBLE_MACHINE:x86-64 = "(.*)"
COMPATIBLE_MACHINE:aarch64 = "(.*)"
COMPATIBLE_MACHINE:riscv64 = "null"

# nooelint: oelint.vars.srcurichecksum:SRC_URI[aarch64.md5sum]
SRC_URI:append:aarch64 = " https://corretto.aws/downloads/resources/${PV}/amazon-corretto-${PV}-linux-aarch64.tar.gz;name=aarch64"

# nooelint: oelint.vars.srcurichecksum:SRC_URI[x86-64.md5sum]
SRC_URI:append:x86-64 = " https://corretto.aws/downloads/resources/${PV}/amazon-corretto-${PV}-linux-x64.tar.gz;name=x86-64"

# you can find checksum here: https://github.com/corretto/corretto-17/releases since devtool upgrade can only do one arch atm.
SRC_URI[x86-64.sha256sum] = "8f9a2ddb131ec5317419f2b0769240fe22ce27f8a0ae43f5346d53e1c8aec6a0"
SRC_URI[aarch64.sha256sum] = "33412bba53b912b21528150df7cf60c161d10fc3b7dae89cd5383d8f3c0362c0"

# also available in master (not kirkstone) in classes-recipe: github-releases
UPSTREAM_CHECK_REGEX ?= "releases/tag/v?(?P<pver>\d+(\.\d+)+)"

UPSTREAM_CHECK_URI = "https://github.com/corretto/corretto-17/tags"

ALTERNATIVE_PRIORITY = "70"
RPROVIDES:${PN} = "java jdk-17 java-17"
RCONFLICTS:${PN}:x86-64 = "corretto-8-bin corretto-11-bin corretto-21-bin corretto-25-bin"
RCONFLICTS:${PN}-ptest:x86-64 = "corretto-8-bin-ptest corretto-11-bin-ptest corretto-21-bin-ptest corretto-25-bin-ptest"

# nooelint: oelint.file.underscores
require corretto-bin-common.inc

# this is used by meta-aws-tests to find this recipe for ptests, so it should stay in this file instead of moving into corretto-bin-common
inherit ptest

UPGRADE_ARCHS = "x86-64 aarch64"
