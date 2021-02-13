SUMMARY = "AWS IoT Device SDK Python v1"
DESCRIPTION = "AWS IoT SDK based on the AWS Common Runtime"
HOMEPAGE = "https://github.com/aws/aws-iot-device-sdk-python.git"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=9ac49901b833e769c7d6f21e8dbd7b30"

BRANCH ?= "master"

SRC_URI = "git://github.com/aws/aws-iot-device-sdk-python.git;branch=${BRANCH};name=aws-iot-device-sdk-python-v1"
SRCREV = "${AUTOREV}"

S = "${WORKDIR}/git"

