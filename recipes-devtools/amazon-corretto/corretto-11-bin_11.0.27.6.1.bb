SUMMARY = "Amazon Corretto 11"
HOMEPAGE = "https://github.com/corretto/corretto-11"

# nooelint: oelint.vars.srcurichecksum:SRC_URI[aarch64.md5sum]
SRC_URI:aarch64 = "https://corretto.aws/downloads/resources/${PV}/amazon-corretto-${PV}-linux-aarch64.tar.gz;name=aarch64"

BASE:arm = "amazon-corretto-${PV}-linux-armv7"
# nooelint: oelint.vars.srcurichecksum:SRC_URI[arm.md5sum]
SRC_URI:arm = "https://corretto.aws/downloads/resources/${PV}/amazon-corretto-${PV}-linux-armv7.tar.gz;name=arm"

# nooelint: oelint.vars.srcurichecksum:SRC_URI[x86-64.md5sum]
SRC_URI:x86-64 = "https://corretto.aws/downloads/resources/${PV}/amazon-corretto-${PV}-linux-x64.tar.gz;name=x86-64"

BASE:x86 = "amazon-corretto-${PV}-linux-x86"
# nooelint: oelint.vars.srcurichecksum:SRC_URI[x86.md5sum]
SRC_URI:x86 = "https://corretto.aws/downloads/resources/${PV}/amazon-corretto-${PV}-linux-x86.tar.gz;name=x86"

# you can find checksum here: https://github.com/corretto/corretto-11/releases  since devtool upgrade can only do one arch atm.
SRC_URI[aarch64.sha256sum] = "679ab9f1f614d3ed000b61ccb5e0c06041c9ee29fb9c0ca1b598a9f23975cf85"
SRC_URI[arm.sha256sum] = "828b7bb111cf4be41ca47f3cb7cac13e5db7bb190f6bf19b4de697676963be98"
SRC_URI[x86-64.sha256sum] = "0b4fd441b90471384af288ea7e927897114871c668ad292f4e982e7cb9f0cbf7"
SRC_URI[x86.sha256sum] = "7b33d8d4455feea8284e142d82fa60a3fc65a6ed6160823810f70993f09a7f1f"

COMPATIBLE_MACHINE:armv7a = "(.*)"
COMPATIBLE_MACHINE:armv7ve = "(.*)"
COMPATIBLE_MACHINE:x86 = "(.*)"

# also available in master (not kirkstone) in classes-recipe: github-releases
UPSTREAM_CHECK_REGEX ?= "releases/tag/v?(?P<pver>\d+(\.\d+)+)"

UPSTREAM_CHECK_URI = "https://github.com/corretto/corretto-11/tags"

ALTERNATIVE_PRIORITY = "60"
RPROVIDES:${PN} = "java jdk-11 java-11"

# nooelint: oelint.file.underscores
require corretto-bin-common.inc

# nooelint: oelint.vars.insaneskip:INSANE_SKIP
INSANE_SKIP:${PN} += "ldflags"

RDEPENDS:${PN}-ptest:prepend = "\
    greengrass-bin \
    "

# this is used by meta-aws-tests to find this recipe for ptests, so it should stay in this file instead of moving into corretto-bin-common
inherit ptest
