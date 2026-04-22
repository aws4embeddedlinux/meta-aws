SUMMARY = "Amazon Corretto 8"
HOMEPAGE = "https://github.com/corretto/corretto-8"

COMPATIBLE_MACHINE:armv7a = "null"
COMPATIBLE_MACHINE:armv7ve = "null"
COMPATIBLE_MACHINE:x86 = "null"
COMPATIBLE_MACHINE:x86-64 = "(.*)"
COMPATIBLE_MACHINE:aarch64 = "(.*)"
COMPATIBLE_MACHINE:riscv64 = "null"

# nooelint: oelint.vars.srcurichecksum:SRC_URI[aarch64.md5sum]
SRC_URI:aarch64 = "https://corretto.aws/downloads/resources/${PV}/amazon-corretto-${PV}-linux-aarch64.tar.gz;name=aarch64"

# nooelint: oelint.vars.srcurichecksum:SRC_URI[x86-64.md5sum]
SRC_URI:x86-64 = "https://corretto.aws/downloads/resources/${PV}/amazon-corretto-${PV}-linux-x64.tar.gz;name=x86-64"

# you can find checksum here: https://github.com/corretto/corretto-8/releases since devtool upgrade can only do one arch atm.

SRC_URI[x86-64.sha256sum] = "91dfffc5015ed1f3ecd548ec48cf5471fad5a7a52fcc9a41f53c4a0e96b08aad"
SRC_URI[aarch64.sha256sum] = "fccd80cdb06d0257f2717158ae8db1b806a498473822bfd9d4b614d41e17e3e0"

UPSTREAM_CHECK_URI = "https://github.com/corretto/corretto-8/tags"

ALTERNATIVE_PRIORITY = "50"
RPROVIDES:${PN} = "java jdk-8 java-8"
RCONFLICTS:${PN}:x86-64 = "corretto-11-bin corretto-17-bin corretto-21-bin corretto-25-bin"
RCONFLICTS:${PN}-ptest:x86-64 = "corretto-11-bin-ptest corretto-17-bin-ptest corretto-21-bin-ptest corretto-25-bin-ptest"

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

# this is used by meta-aws-tests to find this recipe for ptests, so it should stay in this file instead of moving into corretto-bin-common
inherit ptest

UPGRADE_ARCHS = "x86-64 aarch64"
