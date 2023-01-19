SUMMARY = "Amazon Corretto 8"
HOMEPAGE = "https://github.com/corretto/corretto-8"

# nooelint: oelint.vars.srcurichecksum:SRC_URI[aarch64.md5sum]
SRC_URI:aarch64 = "https://corretto.aws/downloads/resources/${PV}/amazon-corretto-${PV}-linux-aarch64.tar.gz;name=aarch64"

# nooelint: oelint.vars.srcurichecksum:SRC_URI[x86-64.md5sum]
SRC_URI:x86-64 = "https://corretto.aws/downloads/resources/${PV}/amazon-corretto-${PV}-linux-x64.tar.gz;name=x86-64"

# you can find checksum here: https://github.com/corretto/corretto-8/releases since devtool upgrade can only do one arch atm.
SRC_URI[x86-64.sha256sum] = "649dad358d51a631b68e7223433f5ad2360bcf40b0ba4cf021e4305b87d0f9ec"
SRC_URI[aarch64.sha256sum] = "82fd83ae2af7ad7777c5498742209dccc09aa4f0aec38690dc2592aa567fbeb2"

UPSTREAM_CHECK_URI = "https://github.com/corretto/corretto-8/tags"

# nooelint: oelint.file.underscores
require corretto-bin-common.inc

do_package_qa[noexec] = "1"
EXCLUDE_FROM_SHLIBS = "1"

# nooelint: oelint.vars.dependsordered
RDEPENDS:${PN} += "\
    cairo \
    gtk+ \
    libgl \
    pango \
"
