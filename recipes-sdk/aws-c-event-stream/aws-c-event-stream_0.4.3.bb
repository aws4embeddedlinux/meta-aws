SUMMARY = "AWS C Event Stream"
DESCRIPTION = "C99 implementation of the vnd.amazon.event-stream content-type."

HOMEPAGE = "https://github.com/awslabs/aws-c-event-stream"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS += "\
    aws-c-common \
    aws-c-io \
    aws-checksums \
    s2n \
    ${@bb.utils.contains('PACKAGECONFIG', 'static', 'aws-lc', 'openssl', d)} \
    "

PROVIDES += "aws/crt-c-event-stream"

BRANCH ?= "main"
SRC_URI = "\
    git://github.com/awslabs/aws-c-event-stream.git;protocol=https;branch=${BRANCH} \
    file://run-ptest \
    "
SRCREV = "1b3825fc9cae2e9c7ed7479ee5d354d52ebdf7a0"

S = "${WORKDIR}/git"

inherit cmake ptest pkgconfig

do_install_ptest () {
   install -d ${D}${PTEST_PATH}/tests
   cp -r ${B}/tests/* ${D}${PTEST_PATH}/tests/
   install -m 0755 ${B}/tests/aws-c-event-stream-tests ${D}${PTEST_PATH}/tests/
}

AWS_C_INSTALL = "${D}/usr"
CFLAGS:append = " -Wl,-Bsymbolic"
EXTRA_OECMAKE += "\
    -DBUILD_TEST_DEPS=OFF \
    -DCMAKE_MODULE_PATH=${STAGING_LIBDIR}/cmake \
    -DCMAKE_PREFIX_PATH=${STAGING_LIBDIR} \
"

PACKAGECONFIG ??= "\
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'with-tests', '', d)} \
    "

# CMAKE_CROSSCOMPILING=ON will disable building the tests
PACKAGECONFIG[with-tests] = "-DBUILD_TESTING=ON,-DBUILD_TESTING=OFF,"

# enable PACKAGECONFIG = "static" to build static instead of shared libs
PACKAGECONFIG[static] = "-DBUILD_SHARED_LIBS=OFF,-DBUILD_SHARED_LIBS=ON"

FILES:${PN}-dev += "${libdir}/*/cmake"

# nooelint: oelint.vars.insaneskip:INSANE_SKIP
INSANE_SKIP:${PN}-ptest += "buildpaths"

BBCLASSEXTEND = "native nativesdk"
