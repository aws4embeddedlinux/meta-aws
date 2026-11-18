SUMMARY = "Amazon Corretto 21"
HOMEPAGE = "https://github.com/corretto/corretto-21"

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

# you can find checksum here: https://github.com/corretto/corretto-21/releases since devtool upgrade can only do one arch atm.
SRC_URI[x86-64.sha256sum] = "2aaa2a9a43143b20ff0b28c8d0da27407b97dc40b160d9a2f60a26965815e948"
SRC_URI[aarch64.sha256sum] = "55751c47c7e3be3b33af43ccdcf3a6f4814d308390b7400477594d421bd16a53"

# also available in master (not kirkstone) in classes-recipe: github-releases
UPSTREAM_CHECK_REGEX ?= "releases/tag/v?(?P<pver>\d+(\.\d+)+)"

UPSTREAM_CHECK_URI = "https://github.com/corretto/corretto-21/tags"

ALTERNATIVE_PRIORITY = "80"
RPROVIDES:${PN} = "java jdk-21 java-21"
RCONFLICTS:${PN}:x86-64 = "corretto-8-bin corretto-11-bin corretto-17-bin corretto-25-bin"
RCONFLICTS:${PN}-ptest:x86-64 = "corretto-8-bin-ptest corretto-11-bin-ptest corretto-17-bin-ptest corretto-25-bin-ptest"

# nooelint: oelint.file.underscores
require corretto-bin-common.inc

# this is used by meta-aws-tests to find this recipe for ptests, so it should stay in this file instead of moving into corretto-bin-common
inherit ptest

UPGRADE_ARCHS = "x86-64 aarch64"
