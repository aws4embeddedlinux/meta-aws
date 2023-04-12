SUMMARY = "firecracker - Binary Distribution"
DESCRIPTION = "Secure and fast microVMs for serverless computing."
HOMEPAGE = "https://firecracker-microvm.github.io/"
require firecracker-microvm-bin.inc

FILES:${PN} += "\
    ${bindir}/firecracker \
"

do_install() {
    install -d ${D}${bindir}

    install -m 0755 ${S}/release-v${PV}-${TARGET_ARCH}/firecracker-v${PV}-${TARGET_ARCH} ${D}${bindir}/firecracker
}
