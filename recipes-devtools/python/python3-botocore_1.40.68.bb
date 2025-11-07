SUMMARY = "python3 botocore"
DESCRIPTION = "The low-level, core functionality of boto3 and the AWS CLI."
HOMEPAGE = "https://github.com/boto/botocore"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=2ee41112a44fe7014dce33e26468ba93"

FILESEXTRAPATHS:prepend := "${THISDIR}/../files:"

SRC_URI = "\
    git://github.com/boto/botocore.git;protocol=https;branch=master \
    file://run-ptest \
    file://python_dependency_test.py \
    "

SRCREV = "c6c184d75856d173498476ad8f6872f8d0d732da"

inherit setuptools3 ptest

RDEPENDS:${PN} += "\
    python3-dateutil \
    python3-jmespath \
    python3-logging \
    "

RDEPENDS:${PN}-ptest += "\
        python3 \
        python3-pytest \
        python3-urllib3 \
"

do_install_ptest() {
        install -d ${D}${PTEST_PATH}/tests
        cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
        install -m 0755 ${UNPACKDIR}/python_dependency_test.py ${D}${PTEST_PATH}/
}
