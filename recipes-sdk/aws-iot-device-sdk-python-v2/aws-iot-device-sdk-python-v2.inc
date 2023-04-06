SUMMARY = "AWS IoT Device SDK Python v2"
DESCRIPTION = "AWS IoT SDK based on the AWS Common Runtime"
HOMEPAGE = "https://github.com/aws/aws-iot-device-sdk-python-v2.git"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f91e61641e7a96835dea6926a65f4702"

BRANCH ?= "main"

SRC_URI = "git://github.com/aws/aws-iot-device-sdk-python-v2.git;branch=${BRANCH};name=aws-iot-device-sdk-python-v2"
SRCREV = "844bf38ebb13e316ea8100e364121e616c9e71df"

S = "${WORKDIR}/git"

RDEPENDS_${PN} += " \
    aws-crt-python \
"
