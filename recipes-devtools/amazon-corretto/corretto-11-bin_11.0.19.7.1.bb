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
SRC_URI[x86-64.sha256sum] = "d3b7de2a0916da0d3826d980e9718a64932a160c33e8dfa6dbff2a91fef56976"
SRC_URI[aarch64.sha256sum] = "623ab89cba349e44136cfaa9288596e58037cf5987ee80cb490d78db87ebac92"
SRC_URI[arm.sha256sum] = "3e459c9d9dee9bc5ca25558b3f1bbfa03d21c196815ff478fcb7056f80cffdc4"

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
