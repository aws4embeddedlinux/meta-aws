SUMMARY = "AWS C SDKUTILS"
DESCRIPTION = "No description or website provided. "

HOMEPAGE = "https://github.com/awslabs/aws-c-sdkutils"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

DEPENDS += "aws-c-common"

PROVIDES += "aws/c-sdkutils"

SRC_URI = "\
    git://github.com/awslabs/aws-c-sdkutils.git;protocol=https;branch=main \
    file://run-ptest \
    "
SRCREV = "812761fdbf791f77cb358212cefade9cc16974e7"

S = "${WORKDIR}/git"

inherit cmake ptest pkgconfig

PACKAGECONFIG ??= "\
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'with-tests', '', d)} \
    "

# enable PACKAGECONFIG = "static" to build static instead of shared libs
PACKAGECONFIG[static] = "-DBUILD_SHARED_LIBS=OFF,-DBUILD_SHARED_LIBS=ON,,"

# CMAKE_CROSSCOMPILING=ON will disable building the tests
PACKAGECONFIG[with-tests] = "-DBUILD_TESTING=ON -DCMAKE_CROSSCOMPILING=OFF,-DBUILD_TESTING=OFF,"

CFLAGS:append = " -Wl,-Bsymbolic"
EXTRA_OECMAKE += "\
    -DCMAKE_MODULE_PATH=${STAGING_LIBDIR}/aws-c-common/cmake \
    -DCMAKE_MODULE_PATH=${STAGING_LIBDIR}/cmake \
    -DCMAKE_PREFIX_PATH=$D/usr \
    -DCMAKE_INSTALL_PREFIX=$D/usr \
    -DCMAKE_BUILD_TYPE=Release \
"

do_install_ptest () {
   install -d ${D}${PTEST_PATH}/tests
   cp -r ${B}/tests/* ${D}${PTEST_PATH}/tests/
   install -m 0755 ${B}/tests/aws-c-sdkutils-tests ${D}${PTEST_PATH}/tests/
}

FILES:${PN}-dev += "${libdir}/*/cmake"
