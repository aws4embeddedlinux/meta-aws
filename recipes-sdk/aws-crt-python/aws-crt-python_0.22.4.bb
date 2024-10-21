SUMMARY = "AWS CRT Python"
DESCRIPTION = "Python bindings for the AWS Common Runtime"
HOMEPAGE = "https://github.com/awslabs/aws-crt-python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS += "\
        ${PYTHON_PN}-setuptools-native \
        openssl \
        "

BRANCH ?= "main"
# nooelint: oelint.file.patchsignedoff
SRC_URI = "\
           gitsm://github.com/awslabs/aws-crt-python.git;protocol=https;branch=${BRANCH} \
           file://fix-shared-linking.patch \
           file://run-ptest \
           "
SRCREV = "c974233750aa17d066e303f02099e3eb130e6563"
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>.*)"

S = "${WORKDIR}/git"

inherit setuptools3 ptest

AWS_C_INSTALL = "${D}/usr/lib;${S}/source"

export AWS_CRT_BUILD_USE_SYSTEM_LIBCRYPTO="1"

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
