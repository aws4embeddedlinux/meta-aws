DEPENDS += "patchelf-native"

do_install_append_qemux86-64() {
    patchelf --set-interpreter /lib/ld-linux-x86-64.so.2 ${D}/greengrass/ggc/core/bin/daemon
}

