SUMMARY = "AWS C MQTT"
DESCRIPTION = "C99 implementation of the MQTT 3.1.1 specification."
HOMEPAGE = "https://github.com/awslabs/aws-c-mqtt"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS = "\
    aws-c-cal \
    aws-c-common \
    aws-c-compression \
    aws-c-http \
    aws-c-io \
    s2n \
    "

PROVIDES += "aws/crt-c-mqtt"

BRANCH ?= "main"

SRC_URI = "\
    git://github.com/awslabs/aws-c-mqtt.git;protocol=https;branch=${BRANCH}\
    file://run-ptest \
    "

SRCREV = "ed7bbd68c03d7022c915a2924740ab7992ad2311"

S = "${WORKDIR}/git"

inherit cmake ptest

CFLAGS:append = " -Wl,-Bsymbolic"
EXTRA_OECMAKE += "\
    -DCMAKE_MODULE_PATH=${STAGING_LIBDIR}/cmake \
    -DCMAKE_PREFIX_PATH=${STAGING_LIBDIR} \
"

PACKAGECONFIG ??= "\
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'with-tests', '', d)} \
    "

# enable PACKAGECONFIG = "static" to build static instead of shared libs
PACKAGECONFIG[static] = "-DBUILD_SHARED_LIBS=OFF,-DBUILD_SHARED_LIBS=ON,,"

# CMAKE_CROSSCOMPILING=ON will disable building additional tests
PACKAGECONFIG[with-tests] = "-DBUILD_TEST_DEPS=ON -DBUILD_TESTING=ON -DCMAKE_CROSSCOMPILING=OFF,-DBUILD_TEST_DEPS=OFF -DBUILD_TESTING=OFF,"

FILES:${PN}-dev += "${libdir}/*/cmake"

do_install_ptest () {
   install -d ${D}${PTEST_PATH}/tests
   cp -r ${B}/tests/* ${D}${PTEST_PATH}/tests/
   install -m 0755 ${B}/tests/aws-c-mqtt-tests ${D}${PTEST_PATH}/tests/
}

# nooelint: oelint.vars.insaneskip:INSANE_SKIP
INSANE_SKIP:${PN}-ptest += "buildpaths"

BBCLASSEXTEND = "native nativesdk"
