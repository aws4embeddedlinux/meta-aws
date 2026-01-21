SUMMARY = "jailer - Binary Distribution"
DESCRIPTION = "Process for starting Firecracker in production scenarios; applies a cgroup/namespace isolation barrier and then drops privileges."
HOMEPAGE = "https://firecracker-microvm.github.io/"
CVE_PRODUCT = "jailer"
LICENSE = "Apache-2.0"
# nooelint: oelint.var.licenseremotefile:License-File
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

ARCH_DIR:x86-64 = "x86_64"
ARCH_DIR:aarch64 = "aarch64"

COMPATIBLE_MACHINE = "null"
COMPATIBLE_MACHINE:aarch64 = "(.*)"
COMPATIBLE_MACHINE:x86-64 = "(.*)"

# nooelint: oelint.vars.srcurichecksum
SRC_URI = "https://github.com/firecracker-microvm/firecracker/releases/download/v${PV}/firecracker-v${PV}-${ARCH_DIR}.tgz;name=${ARCH_DIR}"

SRC_URI[x86_64.sha256sum] = "ea66dc1fbdb2473bbb95a1e822ae7884cd575a891a8f801258723258d36b7c7c"
SRC_URI[aarch64.sha256sum] = "65a39256b9dd741e20c3a3fe5055cb38e5159049b5ede2015604951521000a04"

UPSTREAM_CHECK_REGEX ?= "releases/tag/v?(?P<pver>\d+(\.\d+)+)"

UPSTREAM_CHECK_URI = "https://github.com/firecracker-microvm/firecracker/releases"

SRC_URI:append = " \
    file://run-ptest \
"

S = "${WORKDIR}/release-v${PV}-${TARGET_ARCH}"

inherit bin_package ptest

# nooelint: oelint.vars.insaneskip
INSANE_SKIP:${PN} += "already-stripped"

FILES:${PN} += "\
    ${bindir}/jailer \
"

RDEPENDS:${PN}-ptest += "\
    firecracker-bin \
"

do_install() {
    install -d ${D}${bindir}

    install -m 0755 ${S}/jailer-v${PV}-${TARGET_ARCH} ${D}${bindir}/jailer
}
