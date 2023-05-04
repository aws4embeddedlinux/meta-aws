FILESEXTRAPATHS:prepend:aws-ec2 := "${THISDIR}/files:"

SRC_URI:append:aws-ec2 = " file://cloud.cfg"

# nooelint: oelint.func.specific
do_install:append:aws-ec2 () {
    # Adding cloud-init configuration with reasonable settings.
    install -T -m 0644 ${WORKDIR}/cloud.cfg ${D}${sysconfdir}/cloud/cloud.cfg
}

SYSTEMD_PACKAGES = "${PN}-systemd"
SYSTEMD_SERVICE:${PN}-systemd = "cloud-init.service"
SYSTEMD_AUTO_ENABLE:${PN}-systemd = "enable"

inherit features_check
REQUIRED_DISTRO_FEATURES = "virtualization"

# nooelint: oelint.func.specific
RDEPENDS:${PN}:append:aws-ec2 = " ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'cloud-init-systemd', '', d)} rng-tools aws-cli e2fsprogs e2fsprogs-resize2fs e2fsprogs-tune2fs e2fsprogs-e2fsck e2fsprogs-mke2fs parted sudo sudo-sudo openssh-sftp-server iproute2"
