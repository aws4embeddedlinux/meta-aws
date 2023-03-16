SUMMARY = "AWS CRT Python"
DESCRIPTION = "Python bindings for the AWS Common Runtime"
HOMEPAGE = "https://github.com/awslabs/aws-crt-python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS += "\
        ${PYTHON_PN}-setuptools-native \
        aws-c-auth \
        aws-c-cal \
        aws-c-common \
        aws-c-compression \
        aws-c-event-stream \
        aws-c-http \
        aws-c-io \
        aws-c-mqtt \
        aws-c-s3 \
        aws-c-sdkutils \
        aws-checksums \
        s2n \
        "

BRANCH ?= "main"
SRC_URI = "\
           git://github.com/awslabs/aws-crt-python.git;protocol=https;branch=${BRANCH} \
           file://0001-fix-library-suffix.patch \
           file://run-ptest \
           "
SRCREV = "87840f2451493ce497aa857814b4727340809092"
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>.*)"

S = "${WORKDIR}/git"

inherit setuptools3_legacy ptest

AWS_C_INSTALL = "${D}/usr/lib;${S}/source"

RDEPENDS:${PN} += "python3-asyncio"

CFLAGS:append = " -Wl,-Bsymbolic"

RDEPENDS:${PN}-ptest += "\
        ${PYTHON_PN} \
        ${PYTHON_PN}-websockets \
        bash \
"

do_install_ptest() {
        install -d ${D}${PTEST_PATH}/tests
        cp -rf ${S}/* ${D}${PTEST_PATH}/tests/
}
