SUMMARY = "AWS IoT Device SDK v2 for Python"
DESCRIPTION = "AWS IoT devices can use the AWS IoT Device SDK for Python to communicate with AWS IoT and AWS IoT Greengrass core devices (using the Python programming language)."
HOMEPAGE = "https://github.com/aws/aws-iot-device-sdk-python-v2"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://documents/LICENSE;md5=f91e61641e7a96835dea6926a65f4702"

SRC_URI = "\
        git://github.com/aws/aws-iot-device-sdk-python-v2.git;protocol=https;branch=${BRANCH} \
        file://run-ptest\
        "
SRCREV = "b953d341d0d983073180b2b9ece0a9f7dc18c38b"

S = "${WORKDIR}/git"

inherit setuptools3 ptest

BRANCH ?= "main"

SRC_URI = "git://github.com/aws/aws-iot-device-sdk-python-v2.git;protocol=https;branch=${BRANCH} \
           file://run-ptest"
SRCREV = "5f1eb4483752d16db7bd3329f96dae82b34660a8"

S = "${WORKDIR}/git"

RDEPENDS:${PN} += "aws-crt-python python3-json"

RDEPENDS:${PN}-ptest += " \
        ${PYTHON_PN}-pytest \
        python3-boto3 \
"

do_install_ptest() {
        install -d ${D}${PTEST_PATH}/tests
        cp -rf ${S}/test/* ${D}${PTEST_PATH}/tests/
}
