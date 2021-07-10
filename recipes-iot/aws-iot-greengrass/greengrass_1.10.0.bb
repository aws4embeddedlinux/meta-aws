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

SRC_URI[arm.md5sum]        = "e54bb57929bc278ea89737c4abcd89e8"
SRC_URI[arm.sha256sum]     = "91f3d92dca977ea504921c7dbae96a926adce441c8f9ec1896e4c8cf085d6d2e"

SRC_URI[aarch64.md5sum]    = "1bdde4df4c461cd5502f7adbb79b2903"
SRC_URI[aarch64.sha256sum] = "912ecbe10398382894045f9b9dafd16eac7fabce0fc04fc9ee83c8ec8f67ca5a"

SRC_URI[x86-64.md5sum]     = "cd363d38e22a1918ca0bc6ea8d07a931"
SRC_URI[x86-64.sha256sum]  = "589d91ab2a358d028cd0c458efdcc1a80d19a1fb8d41c358f928d6a98c333f42"

# Release specific configuration

DEPENDS += "patchelf-native"
RDEPENDS_${PN} += "ca-certificates python3-json python3-numbers sqlite3 docker python3-docker-compose openjdk-8"

do_install_append_x86-64() {
    patchelf --set-interpreter /lib/ld-linux-x86-64.so.2 ${D}/greengrass/ggc/core/bin/daemon
}
