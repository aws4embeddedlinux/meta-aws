SUMMARY = "AWS Greengrass Core SDK C with Example"
DESCRIPTION = "A simple hello world application"
LICENSE = "Apache-2.0"

BRANCH = "master"
SRCREV = "f269fc8d07f2922f49429e1d8bc097d65bb67188"
SRC_URI = "git://github.com/aws/aws-greengrass-core-sdk-c.git;protocol=https;branch=${BRANCH}"

S = "${WORKDIR}/git/"

inherit cmake

do_install_append() {
	# Remove unused files
    rm -rf ${D}${libdir}/cmake
    rm -rf ${D}${libdir}/.debug
    # install the library
    install -d ${D}${libdir}
}
FILES_${PN} += "${libdir}/libaws-greengrass-core-sdk-c.so"
FILES_${PN} += "${includedir}/greengrasssdk.h"

PACKAGES = "${PN}"

# just ignore produced debug files by cmake build system
INSANE_SKIP_${PN} += "installed-vs-shipped"
