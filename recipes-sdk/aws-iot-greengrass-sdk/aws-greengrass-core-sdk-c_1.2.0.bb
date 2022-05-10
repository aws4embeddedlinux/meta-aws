# -*- mode: Conf; -*-
SUMMARY = "AWS Greengrass Core SDK C with Example"
DESCRIPTION = "A simple hello world application"
LICENSE = "Apache-2.0"

BRANCH = "master"
SRCREV = "a71613711438b48588e177f86ab322dd3992e780"
SRC_URI = "git://github.com/aws/aws-greengrass-core-sdk-c.git;protocol=https;branch=${BRANCH}"
LIC_FILES_CHKSUM = "file://LICENSE;md5=11f57cdb0512851468f5785d100afabf"

S = "${WORKDIR}/git"

inherit cmake

do_install:append() {
    # Remove unused files
    rm -rf ${D}${libdir}/cmake
    rm -rf ${D}${libdir}/.debug
    # install the library
    install -d ${D}${libdir}
}
FILES:${PN} += "${libdir}/libaws-greengrass-core-sdk-c.so"
FILES:${PN} += "${includedir}/greengrasssdk.h"

# BUG BUG this SDK needs to ship versioned libraries and then create
# symlinks to remove binary dependency
#INSANE_SKIP:${PN}-dev += "dev-elf"
# Notify that libraries are not versioned
FILES_SOLIBSDEV = ""

