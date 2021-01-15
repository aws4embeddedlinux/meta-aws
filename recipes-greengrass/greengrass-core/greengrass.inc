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

inherit update-rc.d systemd

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

    # Configure whether to use systemd or not
    sed -i -e "/useSystemd/{s,\[yes|no],${GG_USESYSTEMD},g}" ${D}/${BPN}/config/config.json
}

pkg_postinst_ontarget_${PN}() {
    # Enable protection for hardlinks and symlinks
    if ! grep -qs 'protected_.*links' $D${sysconfdir}/sysctl.conf | grep -v '^#'; then
    cat >> $D${sysconfdir}/sysctl.conf <<-_EOF_
# Greengrass: protect hardlinks/symlinks
fs.protected_hardlinks = 1
fs.protected_symlinks = 1
_EOF_
    fi

    # Customize '/etc/fstab'
    if [ -f "$D${sysconfdir}/fstab" ]; then
        # Disable TMPFS /var/volatile
        sed -i -e '\#^tmpfs[[:blank:]]\+/var/volatile#s,^,#,g' $D${sysconfdir}/fstab

		# Mount a cgroup hierarchy with all available subsystems
		if ! grep -qs '^cgroup' $D${sysconfdir}/fstab; then
			cat >> $D${sysconfdir}/fstab <<-_EOF_
				# Greengrass: mount cgroups
				cgroup    /sys/fs/cgroup    cgroup    defaults    0  0
			_EOF_
			mount -at cgroup 2>/dev/null
		fi
	fi

	# Disable '/etc/resolv.conf' symlink
	if [ -f "$D${sysconfdir}/default/volatiles/00_core" ]; then
		sed -i -e '/resolv.conf/d' $D${sysconfdir}/default/volatiles/00_core
		cat >> $D${sysconfdir}/default/volatiles/00_core <<-_EOF_
			# Greengrass: create a real (no symlink) resolv.conf
			f root root 0644 /etc/resolv.conf none
		_EOF_
	fi
}

FILES_${PN} = "/${BPN} ${sysconfdir} ${systemd_unitdir}"

CONFFILES_${PN} += "/${BPN}/config/config.json"

INITSCRIPT_NAME = "greengrass"
INITSCRIPT_PARAMS = "defaults 80 20"

SYSTEMD_SERVICE_${PN} = "greengrass.service"

inherit useradd
USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "--system ggc_group"
USERADD_PARAM_${PN}  = "--system --gid ggc_group --shell /bin/false ggc_user"

#
# Disable failing QA checks:
#
#   Binary was already stripped
#   No GNU_HASH in the elf binary
#
INSANE_SKIP_${PN} += "already-stripped ldflags file-rdeps"

