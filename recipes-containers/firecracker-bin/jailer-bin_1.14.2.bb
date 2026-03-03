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

SRC_URI[x86_64.sha256sum] = "c9f112a983783f3cf50feea9e69b8ea9eb7475e52159a9585ca9555be630f5a3"
SRC_URI[aarch64.sha256sum] = "7418f619e7262b24431cf955d2346e5de4d950302ea1a0ff1af50a15fdea22f4"

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
