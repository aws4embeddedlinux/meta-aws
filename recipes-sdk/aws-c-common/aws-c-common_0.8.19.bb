SUMMARY = "AWS C Common"
DESCRIPTION = "Core c99 package for AWS SDK for C. Includes cross-platform primitives, configuration, data structures, and error handling."

HOMEPAGE = "https://github.com/awslabs/aws-c-common"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

PROVIDES = "aws/crt-c-common"

BRANCH ?= "main"
SRC_URI = "\
    git://github.com/awslabs/aws-c-common.git;protocol=https;branch=${BRANCH} \
    file://run-ptest \
    file://ptest_result.py \
"
SRCREV = "fda2104107ec4c189e154a062b71fb9b3c296856"

S = "${WORKDIR}/git"

inherit cmake ptest

CFLAGS:append = " -Wl,-Bsymbolic"
OECMAKE_GENERATOR = "Unix Makefiles"
EXTRA_OECMAKE += "\
    ${@bb.utils.contains('PTEST_ENABLED', '1', '-DCMAKE_BUILD_TYPE=Debug -DALLOW_CROSS_COMPILED_TESTS=ON', '-DBUILD_TESTING=OFF -DCMAKE_BUILD_TYPE=Release', d)} \
    -DCMAKE_INSTALL_PREFIX=$D/usr \
    -DBUILD_SHARED_LIBS=ON \
"

FILES:${PN}-dev += "${libdir}/*/cmake"

RDEPENDS:${PN}-ptest += "cmake python3"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests

    cp -r ${B}/ ${D}${PTEST_PATH}/build
    cp -r ${S}/ ${D}${PTEST_PATH}/src

    find ${D}${PTEST_PATH}/build -name "CMakeFiles" | xargs rm -rf
    find ${D}${PTEST_PATH}/build -name "*.so*" | xargs rm -rf
    find ${D}${PTEST_PATH}/src -name ".git" | xargs rm -rf

    install -m 0755 ${WORKDIR}/ptest_result.py ${D}${PTEST_PATH}/
}

BBCLASSEXTEND = "native nativesdk"
