FILESEXTRAPATHS:prepend := "${THISDIR}:"
SRC_URI:append = " file://linux-kernel.cfg "
KERNEL_CONFIG_FRAGMENTS:remove = "${WORKDIR}/docker.cfg"
KERNEL_CONFIG_FRAGMENTS:append = "${WORKDIR}/linux-kernel.cfg "
