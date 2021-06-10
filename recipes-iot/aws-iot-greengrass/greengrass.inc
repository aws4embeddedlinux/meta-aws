SUMMARY = "AWS IoT Greengrass Core Recipe"
DESCRIPTION = "AWS IoT Greengrass seamlessly extends AWS to edge devices so \
               they can act locally on the data they generate, while \
               still using the cloud for management, analytics, and durable \
               storage. With AWS IoT Greengrass, connected devices can run \
               AWS Lambda functions, Docker containers, or both, \
               execute predictions based on machine learning models, keep \
               device data in sync, and communicate with other devices \
               securely – even when not connected to the Internet."

S = "${WORKDIR}/${BPN}"
FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

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
    install -d ${D}/etc/sysctl.d
    install -m 0400 ${WORKDIR}/greengrass.conf ${D}/etc/sysctl.d

    # Configure whether to use systemd or not
    sed -i -e "/useSystemd/{s,\[yes|no],${GG_USESYSTEMD},g}" ${D}/${BPN}/config/config.json
}

do_install_append_base-files() {
  cat >> $D${sysconfdir}/fstab <<-_EOF_
    # Greengrass: mount cgroups
    cgroup    /sys/fs/cgroup    cgroup    defaults    0  0
  _EOF_
}

do_install_append_resolvconf() {
  # Disable '/etc/resolv.conf' symlink
  if [ -f "$D${sysconfdir}/default/volatiles/00_core" ]; then
    sed -i -e '/resolv.conf/d' $D${sysconfdir}/default/volatiles/00_core
    cat >> $D${sysconfdir}/default/volatiles/00_core <<-_EOF_
      # Greengrass: create a real (no symlink) resolv.conf
      f root root 0644 /etc/resolv.conf none
    _EOF_
  fi
}

FILES_${PN} = "/${BPN} ${sysconfdir} ${systemd_unitdir} /etc/sysctl.d/greengrass.conf"

CONFFILES_${PN} += "/${BPN}/config/config.json"

INITSCRIPT_NAME = "greengrass"
INITSCRIPT_PARAMS = "defaults 80 20"

SYSTEMD_SERVICE_${PN} = "greengrass.service"

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "-r ggc_group"
USERADD_PARAM_${PN} = "-r -M -N -g ggc_group -s /bin/false ggc_user"

#
# Disable failing QA checks:
#
#   Binary was already stripped
#   No GNU_HASH in the elf binary
#
INSANE_SKIP_${PN} += "already-stripped ldflags file-rdeps"

RDEPENDS_${PN} += "ca-certificates python3-json python3-numbers sqlite3"
