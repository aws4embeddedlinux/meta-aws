SUMMARY = "AWS C IO"
DESCRIPTION = "aws-c-io is an event driven framework for implementing application protocols. It is built on top of cross-platform abstractions that allow you as a developer to think only about the state machine and API for your protocols."

HOMEPAGE = "https://github.com/awslabs/aws-c-io"

CVE_PRODUCT = "amazon_web_services_aws-c-io"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS = "\
    aws-c-cal \
    aws-c-common \
    s2n \
    ${@bb.utils.contains('PACKAGECONFIG', 'static', 'aws-lc', 'openssl', d)} \
    "

PROVIDES += "aws/crt-c-io"

BRANCH ?= "main"
SRC_URI = "\
    git://github.com/awslabs/aws-c-io.git;protocol=https;branch=${BRANCH} \
    file://run-ptest \
    "
SRCREV = "c345d77274db83c0c2e30331814093e7c84c45e2"

S = "${WORKDIR}/git"

inherit cmake ptest pkgconfig

PACKAGECONFIG ??= "\
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'with-tests', '', d)} \
    "

# enable PACKAGECONFIG = "static" to build static instead of shared libs
PACKAGECONFIG[static] = "-DBUILD_SHARED_LIBS=OFF,-DBUILD_SHARED_LIBS=ON"

# CMAKE_CROSSCOMPILING=ON will disable building the tests
PACKAGECONFIG[with-tests] = "-DBUILD_TESTING=ON -DCMAKE_CROSSCOMPILING=OFF,-DBUILD_TESTING=OFF,"

FILES:${PN}-dev += "${libdir}/*/cmake"

AWS_C_INSTALL = "$D/usr"
CFLAGS:append = " -Wl,-Bsymbolic"
EXTRA_OECMAKE += "\
    -DCMAKE_MODULE_PATH=${STAGING_LIBDIR}/cmake \
    -DCMAKE_PREFIX_PATH=${STAGING_LIBDIR} \
"
do_install_ptest () {
   install -d ${D}${PTEST_PATH}/tests
   cp -r ${B}/tests/* ${D}${PTEST_PATH}/tests/
   install -m 0755 ${B}/tests/aws-c-io-tests ${D}${PTEST_PATH}/tests/
}

# nooelint: oelint.vars.insaneskip:INSANE_SKIP
INSANE_SKIP:${PN}-ptest += "buildpaths"

BBCLASSEXTEND = "native nativesdk"
