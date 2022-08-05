DEPENDS += "dtc"

SRC_URI += "\
    file://0001-fdt-lib.patch \
    file://0002-kvm-bindings-use-local-version.patch \
    file://0003-Remove-abort-panic-from-the-workspace.patch \
"
UPSTREAM_VERSION_UNKNOWN = "1"