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

<<<<<<< HEAD:recipes-devtools/python/python3-botocore_1.43.55.bb
SRCREV = "b71d5637f58df4bc61953b80902b3c040c7af889"
S = "${WORKDIR}/git"
=======
SRCREV = "1ea0781bbf381e600a82c275219a7c88c1984b80"
>>>>>>> 75f5e055b (python3-botocore: upgrade 1.43.55 -> 1.43.57):recipes-devtools/python/python3-botocore_1.43.57.bb

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
        ${PYTHON_PN}-setuptools \
"
do_install_ptest() {
        install -d ${D}${PTEST_PATH}/tests
        cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
        install -m 0755 ${WORKDIR}/python_dependency_test.py ${D}${PTEST_PATH}/
}
