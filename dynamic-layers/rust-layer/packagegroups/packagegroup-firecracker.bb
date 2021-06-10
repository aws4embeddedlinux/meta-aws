
DESCRIPTION = "Firecracker Package Groups"

inherit packagegroup

PACKAGES = "\
    packagegroup-firecracker \
    "

RDEPENDS_packagegroup-firecracker = "\
    firecracker \
    jailer \
    "
