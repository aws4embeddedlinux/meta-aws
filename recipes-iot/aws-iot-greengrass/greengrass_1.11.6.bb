SUMMARY = "AWS IoT Greengrass Core Recipe"
DESCRIPTION = "AWS IoT Greengrass seamlessly extends AWS to edge devices so \
               they can act locally on the data they generate, while \
               still using the cloud for management, analytics, and durable \
               storage. With AWS IoT Greengrass, connected devices can run \
               AWS Lambda functions, Docker containers, or both, \
               execute predictions based on machine learning models, keep \
               device data in sync, and communicate with other devices \
               securely – even when not connected to the Internet."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://ggc/packages/${PV}/THIRD-PARTY-LICENSES;md5=42f21cfb9a8020282301a68ded2463c6"

SRC_URI:arm = "\
    https://d1onfpft10uf5o.cloudfront.net/greengrass-core/downloads/${PV}/greengrass-linux-armv7l-${PV}.tar.gz;name=arm \
    file://greengrass.service \
    file://greengrass-init \
    file://greengrass.conf \
"

SRC_URI:aarch64 = "\
    https://d1onfpft10uf5o.cloudfront.net/greengrass-core/downloads/${PV}/greengrass-linux-aarch64-${PV}.tar.gz;name=aarch64 \
    file://greengrass.service \
    file://greengrass-init \
    file://greengrass.conf \
"

SRC_URI:x86-64 = "\
    https://d1onfpft10uf5o.cloudfront.net/greengrass-core/downloads/${PV}/greengrass-linux-x86-64-${PV}.tar.gz;name=x86-64 \
    file://greengrass.service \
    file://greengrass-init \
    file://greengrass.conf \
"

SRC_URI[arm.sha256sum] = "79425145dca285a5ce129b868e2680ada38e5b1aa2871519be14a75cff13d636"

SRC_URI[aarch64.sha256sum] = "92dc496efd787fd70701059271986f596086e6d569a539527b88e6d7d1452d0f"

SRC_URI[x86-64.sha256sum] = "08449a148d311a1bf3f9680f3bb96471f8d11bc36b145cd9bf42e468e871e7a2"

COMPATIBLE_MACHINE = "(^$)"
COMPATIBLE_MACHINE:armv7ve = "(.*)"
COMPATIBLE_MACHINE:armv7a = "(.*)"
COMPATIBLE_MACHINE:armv7l = "(.*)"
COMPATIBLE_MACHINE:aarch64 = "(.*)"
COMPATIBLE_MACHINE:x86-64 = "(.*)"

UPSTREAM_VERSION_UNKNOWN = "1"

S = "${WORKDIR}/${BPN}"

inherit update-rc.d useradd systemd

GG_USESYSTEMD = "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'yes', 'no', d)}"

# Disable tasks not needed for the binary package
do_configure[noexec] = "1"
do_compile[noexec]   = "1"

do_install() {
    install -d ${D}/${BPN}
    tar --no-same-owner --exclude='./patches' --exclude='./.pc' -cpf - -C ${S} . \
        | tar --no-same-owner -xpf - -C ${D}/${BPN}

    # Install wrapper bootscript to launch Greengrass core on boot
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/greengrass-init ${D}${sysconfdir}/greengrass
    sed -i -e "s,##GG_INSTALL_DIR##,/${BPN},g" ${D}${sysconfdir}/greengrass
    ln -sf ${sysconfdir}/greengrass ${D}${sysconfdir}/init.d/greengrass

    # Install systemd service
    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${WORKDIR}/greengrass.service ${D}${systemd_unitdir}/system/greengrass.service

    # Install sysctl file - #103
    install -d ${D}/${sysconfdir}/sysctl.d
    install -m 0400 ${WORKDIR}/greengrass.conf ${D}/${sysconfdir}/sysctl.d

    # Configure whether to use systemd or not
    sed -i -e "/useSystemd/{s,\[yes|no],${GG_USESYSTEMD},g}" ${D}/${BPN}/config/config.json
}

do_install:append:base-files() {
  cat >> $D${sysconfdir}/fstab <<-_EOF_
    # Greengrass: mount cgroups
    cgroup    /sys/fs/cgroup    cgroup    defaults    0  0
  _EOF_
}

do_install:append:resolvconf() {
  # Disable '/etc/resolv.conf' symlink
  if [ -f "$D${sysconfdir}/default/volatiles/00_core" ]; then
    sed -i -e '/resolv.conf/d' $D${sysconfdir}/default/volatiles/00_core
    cat >> $D${sysconfdir}/default/volatiles/00_core <<-_EOF_
      # Greengrass: create a real (no symlink) resolv.conf
      f root root 0644 /etc/resolv.conf none
    _EOF_
  fi
}

FILES:${PN} = "/${BPN} ${sysconfdir} ${systemd_unitdir} /etc/sysctl.d/greengrass.conf"

CONFFILES:${PN} += "/${BPN}/config/config.json"

INITSCRIPT_NAME = "greengrass"
INITSCRIPT_PARAMS = "defaults 80 20"

SYSTEMD_SERVICE:${PN} = "greengrass.service"

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "-r ggc_group"
USERADD_PARAM:${PN} = "-r -M -N -g ggc_group -s /bin/false ggc_user"

#
# Disable failing QA checks:
#
#   Binary was already stripped
#   No GNU_HASH in the elf binary
#
INSANE_SKIP:${PN} += "already-stripped ldflags file-rdeps libdir"

FILES:${PN} += "/lib64"

RDEPENDS:${PN} += "ca-certificates python3-json python3-numbers sqlite3"

do_install:append:x86-64() {
    # create symbolic link /lib64/ld-linux-x86-64.so.2 to enable loading the binary
    install -d ${D}/lib64
    cd ${D}/lib64
    ln -s ../lib/ld-linux-x86-64.so.2 ld-linux-x86-64.so.2
}
