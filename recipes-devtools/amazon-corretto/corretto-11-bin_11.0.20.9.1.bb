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
SRC_URI[x86-64.sha256sum] = "b6150255d304eab8fdcc0422beab277e5395bc481b4f87f096da78a979e47d47"
SRC_URI[arm.sha256sum] = "99f0e94cf8d12cde57dceed308805646745bbc5190f49135a0dbf4defde0f869"
SRC_URI[aarch64.sha256sum] = "17c33bd5fb51fd8b4b5cdfce9d656f31698a6c6ccf018f4f2bf99d714948c736"
SRC_URI[x86.sha256sum] = "ed4f9ab8e1a4002d1748d755d34efb1569573ee17c498ad0dd43c2ded40f24cf"

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
