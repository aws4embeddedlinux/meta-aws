SUMMARY = "AWS IoT Device SDK v2 for Python"
DESCRIPTION = "AWS IoT devices can use the AWS IoT Device SDK for Python to communicate with AWS IoT and AWS IoT Greengrass core devices (using the Python programming language)."
HOMEPAGE = "https://github.com/aws/aws-iot-device-sdk-python-v2"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://documents/LICENSE;md5=f91e61641e7a96835dea6926a65f4702"

SRC_URI = "\
        git://github.com/aws/aws-iot-device-sdk-python-v2.git;protocol=https;branch=${BRANCH} \
        file://run-ptest\
        "
SRCREV = "1ef6533b08b239e9cdf30555077334604c1ac610"

S = "${WORKDIR}/git"

inherit setuptools3 ptest

BRANCH ?= "main"

RDEPENDS:${PN} += "aws-crt-python python3-json"

do_configure:prepend(){
        sed -i "s/__version__ = '1.0.0-dev'/__version__ = '${PV}'/" ${S}/awsiot/__init__.py
}

RDEPENDS:${PN}-ptest += "\
        ${PYTHON_PN} \
        bash \
        python3-boto3 \
"

do_install_ptest() {
        install -d ${D}${PTEST_PATH}/tests
        cp -rf ${S}/* ${D}${PTEST_PATH}/tests/
}
