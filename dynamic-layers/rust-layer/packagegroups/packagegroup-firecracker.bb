
DESCRIPTION = "Firecracker Package Groups"

inherit packagegroup

PACKAGES = "\
    packagegroup-firecracker \
    "

RDEPENDS:packagegroup-firecracker = "\
    firecracker \
    jailer \
    "
