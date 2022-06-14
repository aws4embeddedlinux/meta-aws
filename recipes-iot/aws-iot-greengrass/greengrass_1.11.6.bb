require greengrass.inc

LICENSE = "MIT"
LIC_FILES_CHKSUM = " \
    file://ggc/packages/${PV}/THIRD-PARTY-LICENSES;md5=42f21cfb9a8020282301a68ded2463c6 \
"

SRC_URI_arm = " \
    https://d1onfpft10uf5o.cloudfront.net/greengrass-core/downloads/${PV}/greengrass-linux-armv7l-${PV}.tar.gz;name=arm \
    file://greengrass.service \
    file://greengrass-init \
    file://greengrass.conf \
"

SRC_URI_aarch64 = " \
    https://d1onfpft10uf5o.cloudfront.net/greengrass-core/downloads/${PV}/greengrass-linux-aarch64-${PV}.tar.gz;name=aarch64 \
    file://greengrass.service \
    file://greengrass-init \
    file://greengrass.conf \
"

SRC_URI_x86-64 = " \
    https://d1onfpft10uf5o.cloudfront.net/greengrass-core/downloads/${PV}/greengrass-linux-x86-64-${PV}.tar.gz;name=x86-64 \
    file://greengrass.service \
    file://greengrass-init \
    file://greengrass.conf \
"

SRC_URI[arm.sha256sum]     = "79425145dca285a5ce129b868e2680ada38e5b1aa2871519be14a75cff13d636"

SRC_URI[aarch64.sha256sum] = "92dc496efd787fd70701059271986f596086e6d569a539527b88e6d7d1452d0f"

SRC_URI[x86-64.sha256sum]  = "08449a148d311a1bf3f9680f3bb96471f8d11bc36b145cd9bf42e468e871e7a2"

# Release specific configuration

RDEPENDS_${PN} += "ca-certificates python3-json python3-numbers sqlite3"

do_install_append_x86-64() {
    # create symbolic link /lib64/ld-linux-x86-64.so.2 to enable loading the binary
    install -d ${D}/lib64
    cd ${D}/lib64
    ln -s ../lib/ld-linux-x86-64.so.2 ld-linux-x86-64.so.2
}

FILES_${PN} += " /lib64"
INSANE_SKIP_${PN} += " libdir"