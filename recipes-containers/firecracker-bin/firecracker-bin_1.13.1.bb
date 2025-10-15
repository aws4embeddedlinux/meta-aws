SUMMARY = "firecracker - Binary Distribution"
DESCRIPTION = "Secure and fast microVMs for serverless computing."
HOMEPAGE = "https://firecracker-microvm.github.io/"
CVE_PRODUCT = "firecracker"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

ARCH_DIR:x86-64 = "x86_64"
ARCH_DIR:aarch64 = "aarch64"

COMPATIBLE_MACHINE = "null"
COMPATIBLE_MACHINE:aarch64 = "(.*)"
COMPATIBLE_MACHINE:x86-64 = "(.*)"

# nooelint: oelint.vars.specific
COMPATIBLE_HOST:arm = "null"

# nooelint: oelint.vars.srcurichecksum
SRC_URI = "https://github.com/firecracker-microvm/firecracker/releases/download/v${PV}/firecracker-v${PV}-${ARCH_DIR}.tgz;name=${ARCH_DIR}"

SRC_URI[x86_64.sha256sum] = "59450b9171ff2ebdf2f9a25be3a248a7ba79fb6371aec51a9d6d8eefca7b4faf"
SRC_URI[aarch64.sha256sum] = "3ae4006ccb33a068f41d93a9c488cad17356bb9c22418e3c7efc7ca2256a6e84"

UPGRADE_ARCHS = "x86_64 aarch64"
RECIPE_UPGRADE_EXTRA_TASKS = "update_other_arch_checksum"

UPSTREAM_CHECK_REGEX ?= "releases/tag/v?(?P<pver>\d+(\.\d+)+)"

UPSTREAM_CHECK_URI = "https://github.com/firecracker-microvm/firecracker/releases"

SRC_URI:append = " \
    file://run-ptest \
"

S = "${UNPACKDIR}/release-v${PV}-${ARCH_DIR}"

inherit bin_package ptest

# nooelint: oelint.vars.insaneskip
INSANE_SKIP:${PN} += "already-stripped"


FILES:${PN} += "\
    ${bindir}/firecracker \
"

do_install() {
    install -d ${D}${bindir}

    install -m 0755 ${S}/firecracker-v${PV}-${ARCH_DIR} ${D}${bindir}/firecracker
}

# https://bugzilla.yoctoproject.org/show_bug.cgi?id=15227
PACKAGE_DEPENDS:append:class-target = " virtual/cross-binutils"

python do_update_other_arch_checksum() {
    import urllib.request
    import hashlib
    import re
    
    pv = d.getVar('PV')
    recipe_file = d.getVar('FILE')
    upgrade_archs = d.getVar('UPGRADE_ARCHS').split()
    current_arch = d.getVar('ARCH_DIR')
    
    with open(recipe_file, 'r') as f:
        content = f.read()
    
    # Update checksums for other architectures (exclude current one)
    for arch in upgrade_archs:
        if arch != current_arch:
            url = f"https://github.com/firecracker-microvm/firecracker/releases/download/v{pv}/firecracker-v{pv}-{arch}.tgz"
            
            with urllib.request.urlopen(url) as response:
                data = response.read()
                sha256_hash = hashlib.sha256(data).hexdigest()
            
            # Add or update checksum line
            pattern = rf'^SRC_URI\[{arch}\.sha256sum\] = ".*"$'
            replacement = f'SRC_URI[{arch}.sha256sum] = "{sha256_hash}"'
            
            if re.search(pattern, content, re.MULTILINE):
                content = re.sub(pattern, replacement, content, flags=re.MULTILINE)
            else:
                # Add new line after existing checksums
                content = re.sub(
                    r'(SRC_URI\[\w+\.sha256sum\] = ".*")',
                    rf'\1\n{replacement}',
                    content,
                    count=1
                )
            
            bb.note(f"Updated {arch} SHA256: {sha256_hash}")
    
    with open(recipe_file, 'w') as f:
        f.write(content)
}
addtask do_update_other_arch_checksum
do_update_other_arch_checksum[network] = "1"
