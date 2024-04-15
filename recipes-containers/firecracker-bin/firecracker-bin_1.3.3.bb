SUMMARY = "firecracker - Binary Distribution"
DESCRIPTION = "Secure and fast microVMs for serverless computing."
HOMEPAGE = "https://firecracker-microvm.github.io/"
CVE_PRODUCT = "firecracker"
LICENSE = "Apache-2.0"
# nooelint: oelint.var.licenseremotefile:License-File
LIC_FILES_CHKSUM = "file://${WORKDIR}/${PN}/release-v${PV}-${TARGET_ARCH}/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

ARCH_DIR:x86-64 = "x86_64"
ARCH_DIR:aarch64 = "aarch64"

COMPATIBLE_MACHINE = "null"
COMPATIBLE_MACHINE:aarch64 = "(.*)"
COMPATIBLE_MACHINE:x86-64 = "(.*)"

# nooelint: oelint.vars.srcurichecksum
SRC_URI = "https://github.com/firecracker-microvm/firecracker/releases/download/v${PV}/firecracker-v${PV}-${ARCH_DIR}.tgz;subdir=${BPN};name=${ARCH_DIR}"

SRC_URI[x86_64.sha256sum] = "f0095e359a31de37c8a1934162ec448dd7fad0dbefd68a536ad11ec382fe4fe0"
SRC_URI[aarch64.sha256sum] = "0dbb6577260b4c5d7e6526530e85c4d1d80b262d9a4f7c59fb37d6d60a1f2c54"

UPSTREAM_CHECK_REGEX ?= "releases/tag/v?(?P<pver>\d+(\.\d+)+)"

UPSTREAM_CHECK_URI = "https://github.com/firecracker-microvm/firecracker/releases"

SRC_URI:append = " \
    file://run-ptest \
"

S = "${WORKDIR}/${PN}"

inherit bin_package ptest

# nooelint: oelint.vars.insaneskip
INSANE_SKIP:${PN} += "already-stripped"


FILES:${PN} += "\
    ${bindir}/firecracker \
"

do_install() {
    install -d ${D}${bindir}

    install -m 0755 ${S}/release-v${PV}-${TARGET_ARCH}/firecracker-v${PV}-${TARGET_ARCH} ${D}${bindir}/firecracker
}

# https://bugzilla.yoctoproject.org/show_bug.cgi?id=15227
PACKAGE_DEPENDS:append:class-target = " virtual/${TARGET_PREFIX}binutils"