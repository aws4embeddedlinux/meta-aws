SUMMARY = "python3 botocore"
DESCRIPTION = "The low-level, core functionality of boto3 and the AWS CLI."
HOMEPAGE = "https://github.com/boto/botocore"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=2ee41112a44fe7014dce33e26468ba93"

SRC_URI = "\
    git://github.com/boto/botocore.git;protocol=https;branch=master \
    file://run-ptest \
    "

SRCREV = "58634211e18e6a5ca48ed8909b30d0e530723689"
S = "${WORKDIR}/git"

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
}
