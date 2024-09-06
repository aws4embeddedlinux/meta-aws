SUMMARY = "Python Boto3"
DESCRIPTION = "Boto3 is the Amazon Web Services (AWS) Software Development Kit (SDK) for Python, which allows Python developers to write software that makes use of services like Amazon S3 and Amazon EC2."
HOMEPAGE = "https://github.com/boto/boto3"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2ee41112a44fe7014dce33e26468ba93"

SRC_URI = "\
    git://github.com/boto/boto3.git;protocol=https;branch=master \
    file://run-ptest"

SRCREV = "18a61dc6b57e74c002348867899acabc5fd0c364"
S = "${WORKDIR}/git"

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
}
