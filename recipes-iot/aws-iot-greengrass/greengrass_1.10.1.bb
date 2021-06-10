require greengrass.inc

LICENSE = "MIT"
LIC_FILES_CHKSUM = " \
    file://ggc/core/THIRD-PARTY-LICENSES;md5=1f0ad815f019455e3a0efe55e888a69a \
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

SRC_URI[arm.md5sum]        = "f9b1181efe9b0c65dd490b01e9193b61"
SRC_URI[arm.sha256sum]     = "9e77cc841558a15326b6145ddab2b145db8dc735a3710cb4dd0feb023d16ae2f"

SRC_URI[aarch64.md5sum]    = "4de5bd70bcc024634cd6cebe44d3e3f1"
SRC_URI[aarch64.sha256sum] = "b79449eadfdbe64d8c6700ac4fe8cd167495e53359c08eb2fc6071559da2cdd3"

SRC_URI[x86-64.md5sum]     = "e01f095bc1d43e62d1bfadb95f6a8d53"
SRC_URI[x86-64.sha256sum]  = "8fded584f9291510ee91fe98cfd8bc69e01d1b8e4147f24fa1366e0eb9172b28"

# Release specific configuration

RDEPENDS_${PN} += "ca-certificates python3-json python3-numbers sqlite3 docker python3-docker-compose openjdk-8"

do_install_append_x86-64() {
    # create symbolic link /lib64/ld-linux-x86-64.so.2 to enable loading the binary
    install -d ${D}/lib64
    cd ${D}/lib64
    ln -s ../lib/ld-linux-x86-64.so.2 ld-linux-x86-64.so.2
}

FILES_${PN} += " /lib64"
INSANE_SKIP_${PN} += " libdir"

