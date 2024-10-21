SUMMARY = "AWS CRT Python"
DESCRIPTION = "Python bindings for the AWS Common Runtime"
HOMEPAGE = "https://github.com/awslabs/aws-crt-python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS += "\
        ${PYTHON_PN}-setuptools-native \
        "

BRANCH ?= "main"
# nooelint: oelint.file.patchsignedoff
SRC_URI = "\
           gitsm://github.com/awslabs/aws-crt-python.git;protocol=https;branch=${BRANCH} \
           file://fix-shared-linking.patch \
           file://run-ptest \
           "
SRCREV = "5a67c680c948454c18218c54f0d949d4b92ebea1"
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>.*)"

S = "${WORKDIR}/git"

inherit setuptools3_legacy ptest

AWS_C_INSTALL = "${D}/usr/lib;${S}/source"

RDEPENDS:${PN} += "python3-asyncio"

CFLAGS:append = " -Wl,-Bsymbolic"

do_configure:prepend(){
        sed -i "s/__version__ = '1.0.0.dev0'/__version__ = '${PV}'/" ${S}/awscrt/__init__.py
}

RDEPENDS:${PN}-ptest += "\
        ${PYTHON_PN} \
        ${PYTHON_PN}-websockets \
        bash \
"

do_install_ptest() {
        cp -rf ${S}/test ${D}${PTEST_PATH}/
}

BBCLASSEXTEND = "native nativesdk"

# nooelint: oelint.vars.insaneskip:INSANE_SKIP
INSANE_SKIP:${PN}-dbg += "buildpaths"
