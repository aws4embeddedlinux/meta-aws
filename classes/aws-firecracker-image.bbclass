#
# add firecracker distro features
#

DISTRO_FEATURES:append = " virtualization kvm"

IMAGE_INSTALL:append = " kernel-module-kvm kernel-module-kvm-intel kernel-module-kvm-amd "

IMAGE_INSTALL:append = " firecracker-demo"

IMAGE_ROOTFS_EXTRA_SPACE = "5242880"

QB_CPU:x86 = "-cpu Haswell-noTSX-IBRS,vmx=on"

QB_CPU_KVM:x86 = "-cpu Haswell-noTSX-IBRS,vmx=on"

QB_CPU:x86-64 = "-cpu Haswell-noTSX-IBRS,vmx=on"

QB_CPU_KVM:x86-64 = "-cpu Haswell-noTSX-IBRS,vmx=on"
