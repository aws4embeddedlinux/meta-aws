SUMMARY = "Client library for using AWS IoT Jobs service."
DESCRIPTION = "Client library for using AWS IoT Jobs service on embedded devices."
HOMEPAGE = "https://github.com/aws/Jobs-for-AWS-IoT-embedded-sdk"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c8c19afab7f99fb196c9287cbd60a258"

DEPENDS = "corejson"

SRC_URI = "\
    gitsm://github.com/aws/Jobs-for-AWS-IoT-embedded-sdk.git;protocol=https;branch=main \
    file://CMakeLists.txt \
    file://Findjobs.cmake \
    file://001-fix-json-include.patch \
    file://run-ptest \
"

SRCREV = "d250e33446d39845470c8abd3212ac91d928a653"

inherit cmake ptest

EXTRA_OECMAKE:append = " \
    -DLIB_VERSION=${PV} \
    -DLIB_SOVERSION=${@d.getVar('PV').split('.')[0]} \
"

do_configure:prepend() {
    install ${UNPACKDIR}/CMakeLists.txt ${S}/
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

RDEPENDS:${PN} += "aws-iot-device-sdk-embedded-c-jobs-demo-mosquitto"
