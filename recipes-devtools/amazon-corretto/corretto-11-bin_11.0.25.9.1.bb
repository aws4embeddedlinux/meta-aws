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

SRC_URI[aarch64.sha256sum] = "130b51021d30e13f7b15c0b1febb6c6dfa8bcd52f9212528a342f1bc4432f9f6"
SRC_URI[arm.sha256sum] = "be187726104d66392f76f9f4abe3f4190b15048aedb1566c4701873c939b80be"
SRC_URI[x86-64.sha256sum] = "68e75fa1048b9e2aec0c19f8defe399868f11862028a93b9afba0dbb462dfc02"
SRC_URI[x86.sha256sum] = "9977fab8fe12777be06787d64e8abb0e50d62f408d9821f632824810322d6ea3"

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
