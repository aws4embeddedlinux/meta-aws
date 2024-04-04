SUMMARY = "AWS C Cal"
DESCRIPTION = "AWS Crypto Abstraction Layer: Cross-Platform, C99 wrapper for cryptography primitives."

HOMEPAGE = "https://github.com/awslabs/aws-c-cal"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

DEPENDS = "\
    aws-c-common \
    s2n \
    ${@bb.utils.contains('PACKAGECONFIG', 'static', 'aws-lc', 'openssl', d)} \
    "

PROVIDES += "aws/crt-c-cal"

BRANCH ?= "main"

SRC_URI = "\
    git://github.com/awslabs/aws-c-cal.git;protocol=https;branch=${BRANCH}; \
    file://run-ptest \
    "

SRCREV = "314fc555846ac7bf2cc68a117c99a6af26f7043e"

S = "${WORKDIR}/git"

inherit cmake ptest pkgconfig

PACKAGECONFIG ??= "\
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'with-tests', '', d)} \
    "

# CMAKE_CROSSCOMPILING=ON will disable building the tests
PACKAGECONFIG[with-tests] = "-DBUILD_TESTING=ON -DCMAKE_CROSSCOMPILING=OFF,-DBUILD_TESTING=OFF,"

# enable PACKAGECONFIG = "static" to build static instead of shared libs
PACKAGECONFIG[static] = "-DBUILD_SHARED_LIBS=OFF,-DBUILD_SHARED_LIBS=ON"

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
    -DCMAKE_MODULE_PATH=${STAGING_LIBDIR}/cmake \
    -DCMAKE_PREFIX_PATH=${STAGING_LIBDIR} \
    -DCMAKE_BUILD_TYPE=Release \
"

BBCLASSEXTEND = "native nativesdk"
