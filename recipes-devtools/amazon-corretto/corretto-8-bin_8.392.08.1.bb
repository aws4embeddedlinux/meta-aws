SUMMARY = "Amazon Corretto 8"
HOMEPAGE = "https://github.com/corretto/corretto-8"

# nooelint: oelint.vars.srcurichecksum:SRC_URI[aarch64.md5sum]
SRC_URI:aarch64 = "https://corretto.aws/downloads/resources/${PV}/amazon-corretto-${PV}-linux-aarch64.tar.gz;name=aarch64"

# nooelint: oelint.vars.srcurichecksum:SRC_URI[x86-64.md5sum]
SRC_URI:x86-64 = "https://corretto.aws/downloads/resources/${PV}/amazon-corretto-${PV}-linux-x64.tar.gz;name=x86-64"

# you can find checksum here: https://github.com/corretto/corretto-8/releases since devtool upgrade can only do one arch atm.
SRC_URI[x86-64.sha256sum] = "ffabf152c42d091ff16c920f0c4e267a4f13598bdbda2b32dd78a24054d97c0d"
SRC_URI[aarch64.sha256sum] = "25f2bf6103c97cc44371d888b649449178baeaa4033d1867a3d12bc328e96cd4"

UPSTREAM_CHECK_URI = "https://github.com/corretto/corretto-8/tags"

ALTERNATIVE_PRIORITY = "50"

ALTERNATIVE_NAMES = "\
    jar \
    jarsigner \
    java \
    javac \
    javadoc \
    javap \
    jcmd \
    jconsole \
    jdb \
    jdeps \
    jfr \
    jinfo \
    jmap \
    jps \
    jrunscript \
    jstack \
    jstat \
    jstatd \
    serialver \
"

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

# this is used by meta-aws-tests to find this recipe for ptests, so it should stay in this file instead of moving into corretto-bin-common
inherit ptest
