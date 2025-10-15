SUMMARY = "firecracker - Binary Distribution"
DESCRIPTION = "Secure and fast microVMs for serverless computing."
HOMEPAGE = "https://firecracker-microvm.github.io/"
CVE_PRODUCT = "firecracker"
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

SRC_URI[x86_64.sha256sum] = "59450b9171ff2ebdf2f9a25be3a248a7ba79fb6371aec51a9d6d8eefca7b4faf"
SRC_URI[aarch64.sha256sum] = "3ae4006ccb33a068f41d93a9c488cad17356bb9c22418e3c7efc7ca2256a6e84"

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
    ${bindir}/firecracker \
"

do_install() {
    install -d ${D}${bindir}

    install -m 0755 ${S}/firecracker-v${PV}-${TARGET_ARCH} ${D}${bindir}/firecracker
}
