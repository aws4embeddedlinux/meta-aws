SUMMARY = "AWS IoT Device SDK for Embedded C - demo http-demo-s3-download"
DESCRIPTION = "SDK for connecting to AWS IoT from a device using embedded C - demo http-demo-s3-download build from discrete lib packages instead of self contained libs"
HOMEPAGE = "https://github.com/aws/aws-iot-device-sdk-embedded-C"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c8c19afab7f99fb196c9287cbd60a258"

DEPENDS = "\
    backoffalgorithm \
    corehttp \
    corejson \
    sigv4 \
    openssl \
    mbedtls \
"

SRC_URI = "\
    git://github.com/aws/aws-iot-device-sdk-embedded-C.git;protocol=https;branch=main \
    file://CMakeLists.txt \
    file://run-ptest \
    file://001-fix-includes.patch \
    "

SRCREV = "da99638ec373c791a45557b0cd91fc20968d492d"

RDEPENDS:${PN} += "\
    ca-certificates \
    openssl \
"

inherit cmake ptest

do_configure:prepend () {
    cp ${UNPACKDIR}/CMakeLists.txt ${S}/
}

do_install () {
    install -d ${D}${bindir}
    install -m 0755 ${B}/http_demo_s3_download ${D}${bindir}
}
