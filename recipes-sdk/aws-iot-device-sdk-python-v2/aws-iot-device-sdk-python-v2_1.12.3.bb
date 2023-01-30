SUMMARY = "AWS IoT Device SDK v2 for Python"
DESCRIPTION = "AWS IoT devices can use the AWS IoT Device SDK for Python to communicate with AWS IoT and AWS IoT Greengrass core devices (using the Python programming language)."
HOMEPAGE = "https://github.com/aws/aws-iot-device-sdk-python-v2"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://documents/LICENSE;md5=f91e61641e7a96835dea6926a65f4702"

SRC_URI = "\
        git://github.com/aws/aws-iot-device-sdk-python-v2.git;protocol=https;branch=${BRANCH} \
        file://run-ptest\
        "
SRCREV = "bd3177c2430710e0f53c6b55ead3f9e2df3057c6"

S = "${WORKDIR}/git"

inherit setuptools3 ptest

BRANCH ?= "main"

RDEPENDS:${PN} += "aws-crt-python python3-json"

RDEPENDS:${PN}-ptest += "\
        ${PYTHON_PN} \
        bash \
        python3-boto3 \
"

do_install_ptest() {
        install -d ${D}${PTEST_PATH}/tests
        cp -rf ${S}/* ${D}${PTEST_PATH}/tests/
}
