LICENSE = "Apache-2.0"
# nooelint: oelint.var.licenseremotefile:License-File
LIC_FILES_CHKSUM = "file://release-v${PV}-${TARGET_ARCH}/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

ARCH_DIR:x86-64 = "x86_64"
ARCH_DIR:aarch64 = "aarch64"

COMPATIBLE_MACHINE = "null"
COMPATIBLE_MACHINE:aarch64 = "(.*)"
COMPATIBLE_MACHINE:x86-64 = "(.*)"

# nooelint: oelint.vars.srcurichecksum
SRC_URI = "https://github.com/firecracker-microvm/firecracker/releases/download/v${PV}/firecracker-v${PV}-${ARCH_DIR}.tgz;subdir=${BPN};name=${ARCH_DIR}"

SRC_URI[x86_64.sha256sum] = "55f4e8dd3693aaa0584f6fc2656d05e6d197693e02c8170a0673f95ea73504b2"
SRC_URI[aarch64.sha256sum] = "3832016b8f365fc6cd851caf7662061a2ffa0596227f4d9c7fcb4059845c2369"

SRC_URI:append = " \
    file://run-ptest \
"

S = "${WORKDIR}/${PN}"

inherit bin_package ptest

# nooelint: oelint.vars.insaneskip
INSANE_SKIP:${PN} += "already-stripped"
