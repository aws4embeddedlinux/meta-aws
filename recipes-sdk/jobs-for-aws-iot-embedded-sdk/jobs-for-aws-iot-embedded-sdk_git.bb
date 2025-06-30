SUMMARY = "Client library for using AWS IoT Jobs service on embedded devices"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c8c19afab7f99fb196c9287cbd60a258"

SRC_URI = "\
	gitsm://github.com/aws/Jobs-for-AWS-IoT-embedded-sdk.git;protocol=https;branch=main \
    file://CMakeLists.txt \
    file://Findjobs.cmake \
    file://001-fix-json-include.patch \
	"

# Modify these as desired
PV = "1.0+git"
SRCREV = "0a89d30baa011d305029c5c06abe3658972336ef"

DEPENDS = "corejson"

inherit cmake

do_configure:prepend() {
    cp ${UNPACKDIR}/CMakeLists.txt ${S}/
}

do_install:append() {
    install -d ${D}${datadir}/cmake/Modules
    install -m 0644 ${UNPACKDIR}/Findjobs.cmake ${D}${datadir}/cmake/Modules/
}

FILES:${PN} += "${libdir}/libjobs.so.*"
FILES:${PN}-dev += "\
    ${libdir}/libjobs.so \
    ${includedir}/libjobs/* \
    ${datadir}/cmake/Modules/Findjobs.cmake \
"
