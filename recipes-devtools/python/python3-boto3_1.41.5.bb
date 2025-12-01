SUMMARY = "Python Boto3"
DESCRIPTION = "Boto3 is the Amazon Web Services (AWS) Software Development Kit (SDK) for Python, which allows Python developers to write software that makes use of services like Amazon S3 and Amazon EC2."
HOMEPAGE = "https://github.com/boto/boto3"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2ee41112a44fe7014dce33e26468ba93"

FILESEXTRAPATHS:prepend := "${THISDIR}/../files:"

SRC_URI = "\
    git://github.com/boto/boto3.git;protocol=https;branch=master \
    file://run-ptest \
    file://python_dependency_test.py \
    "

SRCREV = "39215b64750416e032dbfe565cf055f5ef66a0fd"
S = "${UNPACKDIR}/git"

inherit setuptools3 ptest

# python3 needs to be included since there are core dependencies such
# as getpass.
RDEPENDS:${PN} += "\
    python3 \
    python3-botocore \
    python3-jmespath \
    python3-logging \
    python3-s3transfer \
    "

RDEPENDS:${PN}-ptest += "\
        ${PYTHON_PN}-pytest \
"
do_install_ptest() {
        install -d ${D}${PTEST_PATH}/tests
        cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
        install -m 0755 ${UNPACKDIR}/python_dependency_test.py ${D}${PTEST_PATH}/
}
