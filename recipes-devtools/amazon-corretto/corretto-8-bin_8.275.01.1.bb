SUMMARY     = "Amazon Corretto 8"
DESCRIPTION = ""
LICENSE = "GPL-2"

LIC_FILES_CHKSUM = "file://../${BASE}/LICENSE;md5=3e0b59f8fac05c3c03d4a26bbda13f8f"
SHR             = "amazon-corretto-${PV}"
BASE_aarch64    = "amazon-corretto-${PV}-linux-aarch64"
SRC_URI_aarch64 = "https://corretto.aws/downloads/resources/${PV}/amazon-corretto-${PV}-linux-aarch64.tar.gz;name=aarch64"

BASE_x86-64     = "amazon-corretto-${PV}-linux-x64"
SRC_URI_x86-64  = "https://corretto.aws/downloads/resources/${PV}/amazon-corretto-${PV}-linux-x64.tar.gz;name=x86-64"

SRC_URI[aarch64.md5sum]    = "3f1d7af28270165396046afe346a6397"
SRC_URI[aarch64.sha256sum] = "7b86c40410c75de44c311fe127bb1dd02c43040312d66b1363737ab3e7d77011"

SRC_URI[x86-64.md5sum]     = "6dc1ccea65e1112a3a8673e776c932d1"
SRC_URI[x86-64.sha256sum]  = "25415701c864ec301975097f207e909d065eb7c452a501c0e4b4487e77fbdc7a"

FILES = ""
FILES_${PN} = "/usr/lib/${SHR} /usr/bin"

do_package_qa[noexec] = "1"
EXCLUDE_FROM_SHLIBS = "1"

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

do_install_append_x86-64() {
    # create symbolic link /lib64/ld-linux-x86-64.so.2 to enable
    # loading the binary When maintainers build binaries on ubuntu,
    # this is the library they are linking to, and if we don't set it
    # up this way on the device, the program will not run.
    install -d ${D}/lib64
    cd ${D}/lib64
    ln -s ../lib/ld-linux-x86-64.so.2 ld-linux-x86-64.so.2
}

FILES_${PN} += " /lib64"
INSANE_SKIP_${PN} += " libdir"
