SUMMARY = "AWS IoT Device SDK for Embedded C - demo fleet_provisioning_keys_cert"
DESCRIPTION = "SDK for connecting to AWS IoT from a device using embedded C - demo fleet_provisioning_keys_cert build from discrete lib packages instead of self contained libs"
HOMEPAGE = "https://github.com/aws/aws-iot-device-sdk-embedded-C"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c8c19afab7f99fb196c9287cbd60a258 "

SRC_URI = "\
    gitsm://github.com/aws/aws-iot-device-sdk-embedded-C.git;protocol=https;branch=main \
    file://001-transport-interface.patch \
    file://002-config.patch \
    file://003-fix-includes.patch \
    file://004-convert-pem-to-der.patch \
    file://CMakeLists.txt_mbedtls \
    file://CMakeLists.txt \
    file://run-ptest \
    "

SRCREV = "da99638ec373c791a45557b0cd91fc20968d492d"

S = "${WORKDIR}/git"

DEPENDS = "\
    backoffalgorithm \
    coremqtt \
    corepkcs11 \
    fleet-provisioning-for-aws-iot-embedded-sdk \
    "

inherit cmake ptest

do_configure:prepend () {
    install ${WORKDIR}/CMakeLists.txt ${S}/
    install ${WORKDIR}/CMakeLists.txt_mbedtls ${S}/libraries/3rdparty/mbedtls/CMakeLists.txt
}

do_install () {
    install -d ${D}${bindir}
    install -m 0755 ${B}/fleet_provisioning_keys_cert ${D}${bindir}
}
