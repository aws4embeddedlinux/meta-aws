#
# This image class creates an ami ec2 image, see /scripts/ec2-ami/README.md
#

inherit extrausers
# Hardening: Locking the root password. Creating the user without password for ssh key-based login only
EXTRA_USERS_PARAMS = "usermod -L root; useradd -p '*' user"

EXTRA_IMAGE_FEATURES:append = " ssh-server-openssh"

# Remove the debug login tweaks; they reverse the sshd_config hardening our
# bbappend applies during do_rootfs.
EXTRA_IMAGE_FEATURES:remove = "allow-empty-password allow-root-login empty-root-password"

IMAGE_FSTYPES += " wic.vhd"
DISTRO_FEATURES:append = " systemd"
DISTRO_FEATURES:append = " virtualization"
DISTRO_FEATURES:append = " usrmerge"

# Force systemd as the init manager; INIT_MANAGER excludes sysvinit and sets
# the VIRTUAL-RUNTIME_* init defaults.
INIT_MANAGER:forcevariable = "systemd"
POKY_INIT_MANAGER:forcevariable = "systemd"

VIRTUAL-RUNTIME_init_manager = "systemd"
VIRTUAL-RUNTIME_login_manager = "shadow-base"
VIRTUAL-RUNTIME_dev_manager = "systemd"

IMAGE_INSTALL:append = " cloud-init"
