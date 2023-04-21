SUMMARY = "AWS C Cal"
DESCRIPTION = "AWS Crypto Abstraction Layer: Cross-Platform, C99 wrapper for cryptography primitives."

HOMEPAGE = "https://github.com/awslabs/aws-c-cal"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

DEPENDS = "\
    aws-c-common \
    openssl \
    s2n \
    "

PROVIDES += "aws/crt-c-cal"

BRANCH ?= "main"

SRC_URI = "\
    git://github.com/awslabs/aws-c-cal.git;protocol=https;branch=${BRANCH}; \
    file://run-ptest \
    "

SRCREV = "55e478b31cf50517cd359c1ef2e79ef6c2a1f9d0"

S = "${WORKDIR}/git"

inherit cmake ptest pkgconfig

PACKAGECONFIG ??= "\
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'with-tests', '', d)} \
    "

# CMAKE_CROSSCOMPILING=ON will disable building the tests
PACKAGECONFIG[with-tests] = "-DBUILD_TESTING=ON -DCMAKE_CROSSCOMPILING=OFF,-DBUILD_TESTING=OFF,"

FILES:${PN}-dev += "${libdir}/*/cmake"

RDEPENDS:${PN} = "\
    aws-c-common \
    s2n \
    "

do_install_ptest () {
   install -d ${D}${PTEST_PATH}/tests
   cp -r ${B}/tests/* ${D}${PTEST_PATH}/tests/
   install -m 0755 ${B}/tests/aws-c-cal-tests ${D}${PTEST_PATH}/tests/
}

CFLAGS:append = " -Wl,-Bsymbolic"

EXTRA_OECMAKE += "\
    -DBUILD_SHARED_LIBS=ON \
    -DCMAKE_MODULE_PATH=${STAGING_LIBDIR}/cmake \
    -DCMAKE_PREFIX_PATH=$D/usr \
    -DCMAKE_INSTALL_PREFIX=$D/usr \
    -DCMAKE_BUILD_TYPE=Release \
"

BBCLASSEXTEND = "native nativesdk"
