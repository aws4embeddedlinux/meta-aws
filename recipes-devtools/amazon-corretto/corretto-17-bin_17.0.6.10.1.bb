SUMMARY = "Amazon Corretto 17"
DESCRIPTION = "Amazon Corretto is a no-cost, multiplatform, production-ready distribution of the Open Java Development Kit (OpenJDK)."
HOMEPAGE = "https://github.com/corretto/corretto-17"
LICENSE = "GPL-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=3e0b59f8fac05c3c03d4a26bbda13f8f"
SHR = "amazon-corretto-${PV}"
BASE:aarch64 = "amazon-corretto-${PV}-linux-aarch64"

# nooelint: oelint.vars.srcurichecksum:SRC_URI[aarch64.md5sum]
SRC_URI:append:aarch64 = " https://corretto.aws/downloads/resources/${PV}/amazon-corretto-${PV}-linux-aarch64.tar.gz;name=aarch64"

BASE:x86-64 = "amazon-corretto-${PV}-linux-x64"
# nooelint: oelint.vars.srcurichecksum:SRC_URI[aarch64.md5sum]
SRC_URI:append:x86-64 = " https://corretto.aws/downloads/resources/${PV}/amazon-corretto-${PV}-linux-x64.tar.gz;name=x86-64"

# you can find checksum here: https://github.com/corretto/corretto-17/releases since devtool upgrade can only do one arch atm.

SRC_URI[aarch64.sha256sum] = "8fc36009858cfb4dbd30ba4847c6fc4d53d4f843b08dea8189f38fbf8bf40ca8"

SRC_URI[x86-64.sha256sum] = "365bb4ae3f56bfb3c0df5f8f5b809ff0212366c46970c4b371acb80ecf4706cc"

COMPATIBLE_MACHINE = "(^$)"
COMPATIBLE_MACHINE:aarch64 = "(.*)"
COMPATIBLE_MACHINE:x86-64 = "(.*)"

SRC_URI:append = " file://run-ptest"

# also available in master (not kirkstone) in classes-recipe: github-releases
UPSTREAM_CHECK_REGEX ?= "releases/tag/v?(?P<pver>\d+(\.\d+)+)"

UPSTREAM_CHECK_URI = "https://github.com/corretto/corretto-17/tags"

S = "${WORKDIR}/${BASE}"

inherit ptest

FILES:${PN} += "\
    /usr/lib/${SHR} \
    /usr/bin \
    "

FILES:${PN} += "/lib64"

RDEPENDS:${PN} += "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'alsa', 'alsa-lib', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'libx11 libxext libxi libxrender libxtst ', '', d)} \
"

do_install() {
    install -d ${D}${bindir}
    install -d ${D}${libdir}/${SHR}
    cp -r ${WORKDIR}/${BASE}/* ${D}${libdir}/${SHR}/
    cd ${D}${bindir}
    ln -s ../lib/${SHR}/bin/jaotc
    ln -s ../lib/${SHR}/bin/jarsigner
    ln -s ../lib/${SHR}/bin/javac
    ln -s ../lib/${SHR}/bin/javap
    ln -s ../lib/${SHR}/bin/jconsole
    ln -s ../lib/${SHR}/bin/jdeprscan
    ln -s ../lib/${SHR}/bin/jfr
    ln -s ../lib/${SHR}/bin/jimage
    ln -s ../lib/${SHR}/bin/jjs
    ln -s ../lib/${SHR}/bin/jmap
    ln -s ../lib/${SHR}/bin/jps
    ln -s ../lib/${SHR}/bin/jshell
    ln -s ../lib/${SHR}/bin/jstat
    ln -s ../lib/${SHR}/bin/keytool
    ln -s ../lib/${SHR}/bin/rmic
    ln -s ../lib/${SHR}/bin/rmiregistry
    ln -s ../lib/${SHR}/bin/unpack200
    ln -s ../lib/${SHR}/bin/jar
    ln -s ../lib/${SHR}/bin/java
    ln -s ../lib/${SHR}/bin/javadoc
    ln -s ../lib/${SHR}/bin/jcmd
    ln -s ../lib/${SHR}/bin/jdb
    ln -s ../lib/${SHR}/bin/jdeps
    ln -s ../lib/${SHR}/bin/jhsdb
    ln -s ../lib/${SHR}/bin/jinfo
    ln -s ../lib/${SHR}/bin/jlink
    ln -s ../lib/${SHR}/bin/jmod
    ln -s ../lib/${SHR}/bin/jrunscript
    ln -s ../lib/${SHR}/bin/jstack
    ln -s ../lib/${SHR}/bin/jstatd
    ln -s ../lib/${SHR}/bin/pack200
    ln -s ../lib/${SHR}/bin/rmid
    ln -s ../lib/${SHR}/bin/serialver
}

do_install:append:x86-64() {
    # create symbolic link /lib64/ld-linux-x86-64.so.2 to enable
    # loading the binary When maintainers build binaries on ubuntu,
    # this is the library they are linking to, and if we don't set it
    # up this way on the device, the program will not run.
    install -d ${D}/lib64
    cd ${D}/lib64
    ln -s ../lib/ld-linux-x86-64.so.2 ld-linux-x86-64.so.2
}

do_install:append() {
    if ${@bb.utils.contains('DISTRO_FEATURES','x11','false','true',d)}; then
        rm ${D}${libdir}/${SHR}/lib/libawt_xawt.so
        rm ${D}${libdir}/${SHR}/lib/libjawt.so
        rm ${D}${libdir}/${SHR}/lib/libsplashscreen.so
    fi
    if ${@bb.utils.contains('DISTRO_FEATURES','alsa','false','true',d)}; then
        rm ${D}${libdir}/${SHR}/lib/libjsound.so
    fi
}

# nooelint: oelint.vars.insaneskip:INSANE_SKIP
INSANE_SKIP:${PN} += "libdir"
