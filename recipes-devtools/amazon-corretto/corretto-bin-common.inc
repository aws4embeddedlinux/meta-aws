DESCRIPTION = "Amazon Corretto is a no-cost, multiplatform, production-ready distribution of the Open Java Development Kit (OpenJDK)."
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3e0b59f8fac05c3c03d4a26bbda13f8f"
CVE_PRODUCT = "corretto"

SHR = "amazon-corretto-${PV}"

BASE:aarch64 = "amazon-corretto-${PV}-linux-aarch64"
BASE:x86-64 = "amazon-corretto-${PV}-linux-x64"

COMPATIBLE_MACHINE = "(^$)"
COMPATIBLE_MACHINE:aarch64 = "(.*)"
COMPATIBLE_MACHINE:x86-64 = "(.*)"

# also available in master (not kirkstone) in classes-recipe: github-releases
UPSTREAM_CHECK_REGEX ?= "releases/tag/v?(?P<pver>\d+(\.\d+)+)"

SRC_URI:append = " file://run-ptest"

S = "${WORKDIR}/${BASE}"

FILES:${PN} += "\
    /usr/lib/${SHR} \
    /usr/bin \
    "

do_install() {
    install -d ${D}${bindir}
    install -d ${D}${libdir}/${SHR}
    cp -r ${WORKDIR}/${BASE}/* ${D}${libdir}/${SHR}/
    cd ${D}${bindir}

    for alt_name in ${ALTERNATIVE_NAMES}; do
        ln -s ../lib/${SHR}/bin/${alt_name}
    done
}

do_install:append:x86-64() {
    # create symbolic link /lib64/ld-linux-x86-64.so.2 to enable
    # loading the binary When maintainers build binaries on ubuntu,
    # this is the library they are linking to, and if we don't set it
    # up this way on the device, the program will not run.
    install -d ${D}/lib64
    cd ${D}/lib64
    ln -s ../lib/ld-linux-x86-64.so.2 ld-linux-x86-64.so.2
}

FILES:${PN} += "/lib64"

# nooelint: oelint.vars.insaneskip:INSANE_SKIP
INSANE_SKIP:${PN} += "libdir"

RDEPENDS:${PN} += "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'alsa', 'alsa-lib', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'libx11 libxext libxi libxrender libxtst ', '', d)} \
    zlib \
"

do_install:append() {
    if ${@bb.utils.contains('DISTRO_FEATURES','x11','false','true',d)}; then
        rm -f ${D}${libdir}/${SHR}/lib/libawt_xawt.so
        rm -f ${D}${libdir}/${SHR}/lib/libjawt.so
        rm -f ${D}${libdir}/${SHR}/lib/libsplashscreen.so
    fi
    if ${@bb.utils.contains('DISTRO_FEATURES','alsa','false','true',d)}; then
        rm -f ${D}${libdir}/${SHR}/lib/libjsound.so
    fi
}

inherit update-alternatives

ALTERNATIVE_NAMES ?= "\
    jar \
    jarsigner \
    java \
    javac \
    javadoc \
    javap \
    jcmd \
    jconsole \
    jdb \
    jdeprscan \
    jdeps \
    jfr \
    jhsdb \
    jimage \
    jinfo \
    jlink \
    jmap \
    jmod \
    jps \
    jrunscript \
    jshell \
    jstack \
    jstat \
    jstatd \
    serialver \
"

python do_package:prepend () {
    prio = d.getVar('ALTERNATIVE_PRIORITY')
    alt_names = d.getVar('ALTERNATIVE_NAMES')

    for alt_name in alt_names.split():
        d.appendVar("ALTERNATIVE_PRIORITY_VARDEPS", ' ' + alt_name + ':' + prio)
}


ALTERNATIVE:${PN} = "${ALTERNATIVE_NAMES}"

RECIPE_UPGRADE_EXTRA_TASKS = "update_other_arch_checksum"

python do_update_other_arch_checksum() {
    import urllib.request
    import hashlib
    import re

    pv = d.getVar('PV')
    recipe_file = d.getVar('FILE')
    upgrade_archs = d.getVar('UPGRADE_ARCHS').split()
    
    # Map TARGET_ARCH to UPGRADE_ARCHS format
    target_arch = d.getVar('TARGET_ARCH')
    arch_to_upgrade = {"x86_64": "x86-64", "aarch64": "aarch64", "arm": "arm", "i686": "x86", "i586": "x86"}
    current_arch = arch_to_upgrade.get(target_arch)
    
    bb.note(f"TARGET_ARCH={target_arch}, current_arch={current_arch}, upgrade_archs={upgrade_archs}")

    with open(recipe_file, 'r') as f:
        content = f.read()

    # Update checksums for other architectures (exclude current one)
    for arch in upgrade_archs:
        if arch != current_arch:
            bb.note(f"Processing arch: {arch}")
            arch_map = {
                "aarch64": "aarch64",
                "x86-64": "x64",
                "arm": "armv7",
                "x86": "x86"
            }

            if arch not in arch_map:
                bb.fatal(f"Unsupported architecture: {arch}")

            url = f"https://corretto.aws/downloads/resources/{pv}/amazon-corretto-{pv}-linux-{arch_map[arch]}.tar.gz"

            with urllib.request.urlopen(url) as response:
                data = response.read()
                sha256_hash = hashlib.sha256(data).hexdigest()

            # Add or update checksum line
            pattern = rf'^SRC_URI\[{re.escape(arch)}\.sha256sum\] = ".*"$'
            replacement = f'SRC_URI[{arch}.sha256sum] = "{sha256_hash}"'

            if re.search(pattern, content, re.MULTILINE):
                content = re.sub(pattern, replacement, content, flags=re.MULTILINE)
            else:
                # Add new line after existing checksums
                content = re.sub(
                    r'(SRC_URI\[[^\]]+\.sha256sum\] = ".*")',
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

# nooelint: oelint.vars.insaneskip
INSANE_SKIP:${PN} += "already-stripped"
