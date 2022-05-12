# -*- mode: Conf; -*-
SUMMARY = "AWS IoT Device SDK v2 for Python"
DESCRIPTION = "AWS IoT devices can use the AWS IoT Device SDK for Python to communicate with AWS IoT and AWS IoT Greengrass core devices (using the Python programming language)."
HOMEPAGE = "https://github.com/aws/aws-iot-device-sdk-python-v2"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f91e61641e7a96835dea6926a65f4702"

inherit setuptools3_legacy

BRANCH ?= "main"

SRC_URI = "git://github.com/aws/aws-iot-device-sdk-python-v2.git;protocol=https;branch=${BRANCH}"
SRCREV = "53a75c3ffa008e093c0904e88b8e3cbfd4c90f00"

S = "${WORKDIR}/git"

RDEPENDS:${PN} += "python3-core aws-crt-python"
