SUMMARY = "AWS IoT Device SDK for Embedded C - shadow-demo-main"
DESCRIPTION = "SDK for connecting to AWS IoT from a device using embedded C - demo shadow-demo-main build from discrete lib packages instead of self contained libs"
HOMEPAGE = "https://github.com/aws/aws-iot-device-sdk-embedded-C"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c8c19afab7f99fb196c9287cbd60a258"

DEPENDS = "\
    backoffalgorithm \
    coremqtt \
    corejson \
    device-shadow-for-aws-iot-embedded-sdk \
    openssl \
    "

SRC_URI = "\
    git://github.com/aws/aws-iot-device-sdk-embedded-C.git;protocol=https;branch=main \
    file://CMakeLists.txt \
    file://run-ptest \
    "

SRCREV = "da99638ec373c791a45557b0cd91fc20968d492d"

S = "${WORKDIR}/git"

RDEPENDS:${PN}-ptest += "\
    coreutils \
    mosquitto \
    "

inherit cmake ptest

do_configure:prepend () {
    install ${WORKDIR}/CMakeLists.txt ${S}/
}

do_install () {
    install -d ${D}${bindir}
    install -m 0755 ${B}/shadow_demo_main ${D}${bindir}
}
