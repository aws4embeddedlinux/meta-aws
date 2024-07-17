SUMMARY = "Amazon Corretto 8"
HOMEPAGE = "https://github.com/corretto/corretto-8"

# nooelint: oelint.vars.srcurichecksum:SRC_URI[aarch64.md5sum]
SRC_URI:aarch64 = "https://corretto.aws/downloads/resources/${PV}/amazon-corretto-${PV}-linux-aarch64.tar.gz;name=aarch64"

# nooelint: oelint.vars.srcurichecksum:SRC_URI[x86-64.md5sum]
SRC_URI:x86-64 = "https://corretto.aws/downloads/resources/${PV}/amazon-corretto-${PV}-linux-x64.tar.gz;name=x86-64"

# you can find checksum here: https://github.com/corretto/corretto-8/releases since devtool upgrade can only do one arch atm.
SRC_URI[x86-64.sha256sum] = "065de7f9a700b1fb65990325f137a96ee49bed0b2be708463fe7695fcd36e0df"
SRC_URI[aarch64.sha256sum] = "f225874a5b90f004f88dec0b38bf7361189ee00ac336ab2aa6cea7ad508acc05"

UPSTREAM_CHECK_URI = "https://github.com/corretto/corretto-8/tags"

ALTERNATIVE_PRIORITY = "50"
RPROVIDES:${PN} = "java jdk-8 java-8"

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
