# -*- mode: Conf; -*-

SUMMARY = "AWS IoT Device SDK Python v1"
DESCRIPTION = "AWS IoT SDK based on the AWS Common Runtime"
HOMEPAGE = "https://github.com/aws/aws-iot-device-sdk-python.git"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=9ac49901b833e769c7d6f21e8dbd7b30"

inherit setuptools3_legacy

BRANCH ?= "master"

SRC_URI = "git://github.com/aws/aws-iot-device-sdk-python.git;protocol=https;branch=${BRANCH} \
           file://0001-Replace-distutils-with-setuptools.patch"
           
SRCREV = "0ea1a2d013529839fc1e7448d19dadff25d581b4"

S = "${WORKDIR}/git"


