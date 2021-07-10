require greengrass.inc

LICENSE = "MIT"
LIC_FILES_CHKSUM = " \
    file://ggc/core/THIRD-PARTY-LICENSES;md5=d3fb176f85edb203d99ed157c1301989 \
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

SRC_URI[arm.md5sum]        = "c5f2981d724e200c0d68ee41e6f6b47c"
SRC_URI[arm.sha256sum]     = "af6ac0b277193a17d59b010071e153aa3d9aca1136062dd044caab3a9b663b13"

SRC_URI[aarch64.md5sum]    = "98cf7b575c2f60cc31d0bb83cabecbfd"
SRC_URI[aarch64.sha256sum] = "20bf2f5bf0bd92db5eb944551057bfcaf4f05309bff2e834286d69e774655e19"

SRC_URI[x86-64.md5sum]     = "03eb60225cd5fb2c725560d8e81fd99e"
SRC_URI[x86-64.sha256sum]  = "28f9ec7c64e937d309bd5b70903020e9ed590626de08a8151b7b9bd4a618727e"

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
