# -*- mode: Conf; -*-
SUMMARY = "AWS C Common"
DESCRIPTION = "Core c99 package for AWS SDK for C. Includes cross-platform primitives, configuration, data structures, and error handling."

HOMEPAGE = "https://github.com/awslabs/aws-c-common"
LICENSE = "Apache-2.0"
PROVIDES = "aws/crt-c-common"

inherit cmake ptest

LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

BRANCH ?= "main"
SRC_URI = "\
    git://github.com/awslabs/aws-c-common.git;protocol=https;branch=${BRANCH} \
    file://run-ptest \
    file://ptest_result.py \
"
SRCREV = "d1c5491a0eecba270b3e7477591a729f1d1954af"

S = "${WORKDIR}/git"

CFLAGS:append = " -Wl,-Bsymbolic"
OECMAKE_GENERATOR = "Unix Makefiles"
EXTRA_OECMAKE:append = " -DCMAKE_INSTALL_PREFIX=$D/usr"
EXTRA_OECMAKE += "${@bb.utils.contains('PTEST_ENABLED', '1', '-DCMAKE_BUILD_TYPE=Debug -DALLOW_CROSS_COMPILED_TESTS=ON', '-DBUILD_TESTING=OFF -DCMAKE_BUILD_TYPE=Release', d)}"
EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=ON"
OECMAKE_BUILDPATH = "${WORKDIR}/build"
OECMAKE_SOURCEPATH = "${S}"


FILES:${PN}     = "${libdir}/lib${PN}.so.1.0.0 \
                   ${libdir}/lib${PN}.so.1"
FILES:${PN}-dev = "${includedir}/aws/common/* \
                   ${includedir}/aws/testing/* \
                   ${libdir}/cmake/* \
                   ${libdir}/aws-c-common/* \
                   ${libdir}/lib${PN}.so"
FILES:${PN}-dbg = "/usr/src/debug/aws-c-common/* \
                   ${libdir}/.debug/lib${PN}.so.1.0.0"

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
