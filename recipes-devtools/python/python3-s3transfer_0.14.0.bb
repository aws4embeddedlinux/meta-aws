SUMMARY = "python3 s3 transfer"
DESCRIPTION = "Amazon S3 Transfer Manager for Python"
HOMEPAGE = "https://github.com/boto/s3transfer"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3b83ef96387f14655fc854ddc3c6bd57"

FILESEXTRAPATHS:prepend := "${THISDIR}/../files:"

SRC_URI = "\
    git://github.com/boto/s3transfer.git;protocol=https;branch=master \
    file://run-ptest \
    file://python_dependency_test.py \
    "
SRCREV = "b4ee0a2cc675088f8419e4996a9c560e510afd37"

S = "${WORKDIR}/git"

inherit setuptools3 ptest

RDEPENDS:${PN} += "\
    python3-asyncio \
    python3-botocore \
    python3-multiprocessing \
    python3-urllib3 \
    "

RDEPENDS:${PN}-ptest += "\
        python3 \
        python3-pytest \
        python3-boto3 \
        "

do_install_ptest() {
        install -d ${D}${PTEST_PATH}/tests
        cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
        install -m 0755 ${WORKDIR}/python_dependency_test.py ${D}${PTEST_PATH}/
}
