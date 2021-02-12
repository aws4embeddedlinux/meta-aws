FILESEXTRAPATHS_prepend := "${THISDIR}:"
SRC_URI_append = " file://linux-kernel.cfg "
KERNEL_CONFIG_FRAGMENTS_append = "${WORKDIR}/linux-kernel.cfg "
