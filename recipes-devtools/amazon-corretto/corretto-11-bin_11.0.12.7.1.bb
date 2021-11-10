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

SRC_URI[aarch64.md5sum]    = "facff11c6ae8e5b9dc54cbbbddaf3dc0"
SRC_URI[aarch64.sha256sum] = "3de81a85e8ddbc03045e25b2025b00c8965d9c9be44bf0eefef10773dcc33e7f"

SRC_URI[arm.md5sum]        = "b7d9f46eabd604c0bf89227bc64644ed"
SRC_URI[arm.sha256sum]     = "63c05e86606d53df6a33472bb570fa9318de8125644dd928566d6205a1ecb024"

SRC_URI[x86-64.md5sum]     = "55e5ca4565737598ff24c6d927253275"
SRC_URI[x86-64.sha256sum]  = "9c451d1aec267e6be26a96b59e9f8ab8cf9253b9e0cd469fa2a62391c0dfe0c6"

SRC_URI[x86.md5sum]        = "cee889af2f2ba7b6a2277959ccd93c66"
SRC_URI[x86.sha256sum]     = "f2d428803d6881d8abcd706ea98897d08ef61970ec6dceecb19106d15116eb0a"

FILES = ""
FILES:${PN} = "/usr/lib/${SHR} /usr/bin"

RDEPENDS:${PN} += " \
    libx11 \
    libxrender \
    libxext \
    libxi \
    libxtst \
    alsa-lib \
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

FILES:${PN} += " /lib64"
INSANE_SKIP:${PN} += " libdir"
