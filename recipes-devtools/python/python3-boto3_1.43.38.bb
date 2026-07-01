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

<<<<<<<< HEAD:recipes-devtools/python/python3-boto3_1.43.19.bb
SRCREV = "3ae731cc04d72140312c6a76fd5f46abf68ad175"
========
SRCREV = "efcfb6506ce686aa8a09908d835ba2b9731209a5"
>>>>>>>> 6279e24d7 (python3-boto3: upgrade 1.43.33 -> 1.43.38):recipes-devtools/python/python3-boto3_1.43.38.bb

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
        ${PYTHON_PN}-setuptools \
"
do_install_ptest() {
        install -d ${D}${PTEST_PATH}/tests
        cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
        install -m 0755 ${UNPACKDIR}/python_dependency_test.py ${D}${PTEST_PATH}/
}
