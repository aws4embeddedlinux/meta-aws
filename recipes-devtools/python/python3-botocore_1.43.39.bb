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

<<<<<<<< HEAD:recipes-devtools/python/python3-botocore_1.42.92.bb
SRCREV = "20bd7a81afb0f4ff002496db547f04430f65519b"
========
SRCREV = "3f295440320638cc50db35e0f599e98555c80b7a"
>>>>>>>> 2bed01c7f (python3-botocore: upgrade 1.43.38 -> 1.43.39):recipes-devtools/python/python3-botocore_1.43.39.bb

inherit setuptools3 ptest

RDEPENDS:${PN} += "\
    python3-dateutil \
    python3-jmespath \
    python3-logging \
    python3-html \
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
        install -m 0755 ${UNPACKDIR}/python_dependency_test.py ${D}${PTEST_PATH}/
}
