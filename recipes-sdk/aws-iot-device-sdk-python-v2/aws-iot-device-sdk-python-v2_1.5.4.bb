# -*- mode: Conf; -*-
SUMMARY = "AWS IoT Device SDK v2 for Python"
DESCRIPTION = "AWS IoT devices can use the AWS IoT Device SDK for Python to communicate with AWS IoT and AWS IoT Greengrass core devices (using the Python programming language)."
HOMEPAGE = "https://github.com/aws/aws-iot-device-sdk-python-v2"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f91e61641e7a96835dea6926a65f4702"


inherit setuptools3
BRANCH ?= "main"

SRC_URI = "git://github.com/aws/aws-iot-device-sdk-python-v2.git;protocol=https;branch=${BRANCH};name=aws-iot-device-sdk-python-v2"
SRCREV = "a7739a6f980292ae146e064b18f3f2723019261e"

S = "${WORKDIR}/git"

RDEPENDS:${PN} += "python3 aws-crt-python"
