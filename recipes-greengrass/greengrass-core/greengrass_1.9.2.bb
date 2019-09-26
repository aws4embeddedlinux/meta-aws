SUMMARY = "AWS IoT Greengrass Core Recipe"
DESCRIPTION = "Recipe created by bitbake-layers"
LICENSE = "MIT"

SRC_URI_arm = " \
    https://d1onfpft10uf5o.cloudfront.net/greengrass-core/downloads/${PV}/greengrass-linux-armv7l-${PV}.tar.gz;name=arm \
    file://greengrass.service \
    file://greengrass-init \
"

SRC_URI_aarch64 = " \
    https://d1onfpft10uf5o.cloudfront.net/greengrass-core/downloads/${PV}/greengrass-linux-aarch64-${PV}.tar.gz;name=aarch64 \
    file://greengrass.service \
    file://greengrass-init \
"

SRC_URI_x86-64 = " \
    https://d1onfpft10uf5o.cloudfront.net/greengrass-core/downloads/${PV}/greengrass-linux-x86-64-${PV}.tar.gz;name=x86-64 \
    file://greengrass.service \
    file://greengrass-init \
"

LIC_FILES_CHKSUM = " \
    file://ggc/core/THIRD-PARTY-LICENSES;md5=53b6a4caa097863bc3971d5e0ac6d1db \
"

SRC_URI[arm.md5sum]        = "63a1f6aae22260be19f34f278f7e7833"
SRC_URI[arm.sha256sum]     = "4bc0bc8a938cdb3d846df92e502155c6ec8cbaf1b63dfa9f3cc3a51372d95af5"

SRC_URI[aarch64.md5sum]    = "967cd25ac77e733b0a1b42d83dc96ed1"
SRC_URI[aarch64.sha256sum] = "4cbaf91e5354fe49ded160415394413f068426c2bba13089e6b8a28775990a9c"

SRC_URI[x86-64.md5sum]     = "a1f90898cc324b3c2a6a7cf50038d70a"
SRC_URI[x86-64.sha256sum]  = "ee55e370491905415cfab962f5429228c58aec4c7f481e66a14e952e1b62fe36"

S = "${WORKDIR}/${BPN}"

inherit update-rc.d useradd systemd

GG_USESYSTEMD = "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'yes', 'no', d)}"

# Check if we have all the env variables set
python __anonymous () {
	pkgname = d.getVar("PN")
#	if d.getVar("GREENGRASS_CERTS_DIR", False) == None:
#		bb.error("%s: GREENGRASS_CERTS_DIR is not set" % pkgname)

#	if d.getVar("GREENGRASS_CONFIG_DIR", False) == None:
#		bb.error("%s: GREENGRASS_CONFIG_DIR is not set" % pkgname)
}

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

    # Install the greengrass core specific certificates and config.json
#    install -m 0644 ${GREENGRASS_CERTS_DIR}/* ${D}/${BPN}/certs/
#    install -m 0644 ${GREENGRASS_CONFIG_DIR}/* ${D}/${BPN}/config/

    # Configure whether to use systemd or not
    sed -i -e "/useSystemd/{s,\[yes|no],${GG_USESYSTEMD},g}" ${D}/${BPN}/config/config.json
}

pkg_postinst_ontarget_${PN}() {
    # Enable protection for hardlinks and symlinks
    if ! grep -qs 'protected_.*links' $D${sysconfdir}/sysctl.conf; then
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

RDEPENDS_${PN} += "ca-certificates python-argparse python-json python-numbers sqlite3"

