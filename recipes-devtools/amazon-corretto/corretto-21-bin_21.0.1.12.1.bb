SUMMARY = "Amazon Corretto 21"
HOMEPAGE = "https://github.com/corretto/corretto-21"

# nooelint: oelint.vars.srcurichecksum:SRC_URI[aarch64.md5sum]
SRC_URI:append:aarch64 = " https://corretto.aws/downloads/resources/${PV}/amazon-corretto-${PV}-linux-aarch64.tar.gz;name=aarch64"

# nooelint: oelint.vars.srcurichecksum:SRC_URI[x86-64.md5sum]
SRC_URI:append:x86-64 = " https://corretto.aws/downloads/resources/${PV}/amazon-corretto-${PV}-linux-x64.tar.gz;name=x86-64"

# you can find checksum here: https://github.com/corretto/corretto-17/releases since devtool upgrade can only do one arch atm.
SRC_URI[x86-64.sha256sum] = "3e718a86cfa6c1173c469f5e9d6b07fa37381a28ebb1f80593250cc380baf22f"
SRC_URI[aarch64.sha256sum] = "aa43295803595f78d73e9c7c02866301c9729377277144e2829f54a58e5f6d21"

# also available in master (not kirkstone) in classes-recipe: github-releases
UPSTREAM_CHECK_REGEX ?= "releases/tag/v?(?P<pver>\d+(\.\d+)+)"

UPSTREAM_CHECK_URI = "https://github.com/corretto/corretto-21/tags"

# nooelint: oelint.file.underscores
require corretto-bin-common.inc
