SUMMARY = "jailer - Binary Distribution"
DESCRIPTION = "Process for starting Firecracker in production scenarios; applies a cgroup/namespace isolation barrier and then drops privileges."
HOMEPAGE = "https://firecracker-microvm.github.io/"
require firecracker-microvm-bin.inc

FILES:${PN} += "\
    ${bindir}/jailer \
"

RDEPENDS:${PN}-ptest += "\
    firecracker-bin \
"

do_install() {
    install -d ${D}${bindir}

    install -m 0755 ${S}/release-v${PV}-${TARGET_ARCH}/jailer-v${PV}-${TARGET_ARCH} ${D}${bindir}/jailer
}

