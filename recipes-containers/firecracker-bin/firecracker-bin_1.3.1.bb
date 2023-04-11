SUMMARY = "A microvm Virtual Machine Monitor."
HOMEPAGE = "https://firecracker-microvm.github.io/"
include firecracker-microvm-bin.inc

FILES:${PN} = "\
    ${bindir}/firecracker \
"

do_install() {
    install -d ${D}${bindir}

    install -m 0755 ${S}/release-v${PV}-${TARGET_ARCH}/firecracker-v${PV}-${TARGET_ARCH} ${D}${bindir}/firecracker
}

