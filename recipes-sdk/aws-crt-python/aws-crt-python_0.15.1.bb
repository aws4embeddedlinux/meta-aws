SUMMARY = "AWS CRT Python"
DESCRIPTION = "Python bindings for the AWS Common Runtime"
HOMEPAGE = "https://github.com/awslabs/aws-crt-python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

# note: this software build its depending libraries such as aws-lc in do_compile step, but finally links to the libs specified by DEPENDS!
# the 0002-disable-building-of-depending-libs.patch disable this behaviour, therefore it's not necessary to checkout the submodules (git:// instead of gitsm://)
BRANCH ?= "main"
SRC_URI = "git://github.com/awslabs/aws-crt-python.git;protocol=https;branch=${BRANCH} \
           file://0001-fix-library-suffix.patch \
           file://0002-disable-building-of-depending-libs.patch \
           "


SRCREV = "a989a07ff90676ef5ce4e912a0cf7e67167ae45a"
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>.*)"

S = "${WORKDIR}/git"

inherit setuptools3_legacy

AWS_C_INSTALL = "${D}/usr/lib;${S}/source"
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
        aws-lc \
        s2n \
        "

BRANCH ?= "main"
SRC_URI = "\
           git://github.com/awslabs/aws-crt-python.git;protocol=https;branch=${BRANCH} \
           file://0001-fix-library-suffix.patch \
           file://run-ptest \
           "
SRCREV = "c3d2b5eadc8860ba1171fc9de380a947d15f2d5c"
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
