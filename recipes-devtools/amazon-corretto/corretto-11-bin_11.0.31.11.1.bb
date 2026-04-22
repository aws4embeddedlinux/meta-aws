SUMMARY = "Amazon Corretto 11"
HOMEPAGE = "https://github.com/corretto/corretto-11"

COMPATIBLE_MACHINE:armv7a = "(.*)"
COMPATIBLE_MACHINE:armv7ve = "(.*)"
COMPATIBLE_MACHINE:x86 = "(.*)"
COMPATIBLE_MACHINE:x86-64 = "(.*)"
COMPATIBLE_MACHINE:aarch64 = "(.*)"
COMPATIBLE_MACHINE:riscv64 = "null"

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
SRC_URI[x86-64.sha256sum] = "70f6ff3668f27d1052f9e26c7a00d601774a556a49e6e9e7faa9d510ae1d0dbe"
SRC_URI[aarch64.sha256sum] = "8ee5fba821463363dc76a18049e338d12c74752430a743aa405af126a62218da"
SRC_URI[arm.sha256sum] = "2a2253ec40e3c275fa29687382f7782ff0a421114385ec34a2df2e32fd17d634"
SRC_URI[x86.sha256sum] = "7423a636ca1000688e874cdb961a507012f6eacb9cf42afc1273a7f279c61ada"

# also available in master (not kirkstone) in classes-recipe: github-releases
UPSTREAM_CHECK_REGEX ?= "releases/tag/v?(?P<pver>\d+(\.\d+)+)"

UPSTREAM_CHECK_URI = "https://github.com/corretto/corretto-11/tags"

ALTERNATIVE_PRIORITY = "60"
RPROVIDES:${PN} = "java jdk-11 java-11"
RCONFLICTS:${PN}:x86-64 = "corretto-8-bin corretto-17-bin corretto-21-bin corretto-25-bin"
RCONFLICTS:${PN}-ptest:x86-64 = "corretto-8-bin-ptest corretto-17-bin-ptest corretto-21-bin-ptest corretto-25-bin-ptest"
PROVIDES:class-native = "java-11-native jdk-11-native"

# nooelint: oelint.file.underscores
require corretto-bin-common.inc

# nooelint: oelint.vars.insaneskip:INSANE_SKIP
INSANE_SKIP:${PN} += "ldflags"

RDEPENDS:${PN}-ptest:prepend = "\
    greengrass \
    "

# this is used by meta-aws-tests to find this recipe for ptests, so it should stay in this file instead of moving into corretto-bin-common
inherit ptest

UPGRADE_ARCHS = "x86 x86-64 arm aarch64"

do_install:append:class-native() {
    install -d ${D}${libdir}/jvm

    ln -sf ../amazon-corretto-${PV} ${D}${libdir}/jvm/jdk-11-native
}

# Dodaj symlink do FILES
FILES:${PN}:append:class-native = " ${libdir}/jvm/jdk-11-native"
