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
SRC_URI[aarch64.sha256sum] = "aab66a39cfcd5dbb01c29fb91da38d574822f2e099a43aee38c6d13005d82ff8"
SRC_URI[arm.sha256sum] = "1e7d41659441a3463f8d092ff936c1b29d2b9e63b84de0c916129318bda74fb3"
SRC_URI[x86-64.sha256sum] = "40baac37cb9a3953bba01c07f34782bf4d80a7529009641278e42ca674ff9ee8"
SRC_URI[x86.sha256sum] = "fd530713fbd635ebd3a01047afe9ec0fcd71e22d82a5959d35809a78db5aed6e"

COMPATIBLE_MACHINE:armv7a = "(.*)"
COMPATIBLE_MACHINE:armv7ve = "(.*)"
COMPATIBLE_MACHINE:x86 = "(.*)"

# also available in master (not kirkstone) in classes-recipe: github-releases
UPSTREAM_CHECK_REGEX ?= "releases/tag/v?(?P<pver>\d+(\.\d+)+)"

UPSTREAM_CHECK_URI = "https://github.com/corretto/corretto-11/tags"

# nooelint: oelint.file.underscores
require corretto-bin-common.inc

# nooelint: oelint.vars.insaneskip:INSANE_SKIP
INSANE_SKIP:${PN} += "ldflags"

RDEPENDS:${PN}-ptest:prepend = "\
    greengrass-bin \
    "