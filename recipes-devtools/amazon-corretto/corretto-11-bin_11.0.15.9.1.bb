SUMMARY     = "Amazon Corretto 11"
DESCRIPTION = ""
LICENSE = "GPL-2"

LIC_FILES_CHKSUM = "file://../${BASE}/LICENSE;md5=3e0b59f8fac05c3c03d4a26bbda13f8f"
SHR             = "amazon-corretto-${PV}"
BASE:aarch64    = "amazon-corretto-${PV}-linux-aarch64"
SRC_URI:aarch64 = "https://corretto.aws/downloads/resources/${PV}/amazon-corretto-${PV}-linux-aarch64.tar.gz;name=aarch64"

BASE:arm        = "amazon-corretto-${PV}-linux-armv7"
SRC_URI:arm     = "https://corretto.aws/downloads/resources/${PV}/amazon-corretto-${PV}-linux-armv7.tar.gz;name=arm"

BASE:x86-64     = "amazon-corretto-${PV}-linux-x64"
SRC_URI:x86-64  = "https://corretto.aws/downloads/resources/${PV}/amazon-corretto-${PV}-linux-x64.tar.gz;name=x86-64"

BASE:x86        = "amazon-corretto-${PV}-linux-x86"
SRC_URI:x86     = "https://corretto.aws/downloads/resources/${PV}/amazon-corretto-${PV}-linux-x86.tar.gz;name=x86"

SRC_URI[aarch64.sha256sum] = "d3968138f266661cdc9d0d8a6830fea1d0c697113d6496027b4eb1139bed5f68"

SRC_URI[arm.sha256sum]     = "0db58483018d7c1a0daa222c8e1c7d77ba0bf6949c28d91fd27c1cd8a6ab09f7"

SRC_URI[x86-64.sha256sum] = "1878cfcb1ed374d5c8d3e11f230ec702ad3a6779107b640ddb5a0cf9e313bb61"

SRC_URI[x86.sha256sum]     = "cfd956be63c33217093161022cb37176e48953e4e9f4d2b7a79de0277ac8c933"

FILES = ""
FILES:${PN} = "/usr/lib/${SHR} /usr/bin"

RDEPENDS:${PN} += " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'alsa', 'alsa-lib', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'libxrender libxext libxi libxtst libx11', '', d)} \
"

do_install() {
    install -d ${D}/usr/bin
    install -d ${D}/usr/lib/${SHR}
    cp -r ${WORKDIR}/${BASE}/* ${D}/usr/lib/${SHR}/
    cd ${D}/usr/bin
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
        rm ${D}/usr/lib/${SHR}/lib/libawt_xawt.so
        rm ${D}/usr/lib/${SHR}/lib/libjawt.so
        rm ${D}/usr/lib/${SHR}/lib/libsplashscreen.so
    fi
}

do_install:append() {
    if ${@bb.utils.contains('DISTRO_FEATURES','alsa','false','true',d)}; then
        rm ${D}/usr/lib/${SHR}/lib/libjsound.so
    fi
}

FILES:${PN} += " /lib64"


INSANE_SKIP:${PN} = "libdir"
