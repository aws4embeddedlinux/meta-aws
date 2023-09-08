#
# This image class creates an ami ec2 image, see /scripts/ec2-ami/README.md
#

inherit extrausers
# Hardening: Locking the root password. Creating the user without password for ssh key-based login only
EXTRA_USERS_PARAMS = "usermod -L root; useradd -p '*' user"

EXTRA_IMAGE_FEATURES:append = " ssh-server-openssh"

# Forcing removal of debug-tweakes as that leads to reversing some sshd_config hardening done in our bbappend when do_rootfs runs
EXTRA_IMAGE_FEATURES:remove = "debug-tweaks"

IMAGE_FSTYPES += " wic.vhd"
DISTRO_FEATURES:append = " systemd"
DISTRO_FEATURES:append = " virtualization"
DISTRO_FEATURES:append = " usrmerge"

DISTRO_FEATURES:remove = " sysvinit"
DISTRO_FEATURES_BACKFILL_CONSIDERED:append = " sysvinit"

INIT_MANAGER:forcevariable = "systemd"
POKY_INIT_MANAGER:forcevariable = "systemd"

VIRTUAL-RUNTIME_init_manager = "systemd"
VIRTUAL-RUNTIME_initscripts = "systemd-compat-units"
VIRTUAL-RUNTIME_login_manager = "shadow-base"
VIRTUAL-RUNTIME_dev_manager = "systemd"

IMAGE_INSTALL:append = " cloud-init"
