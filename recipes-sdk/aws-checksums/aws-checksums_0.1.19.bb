SUMMARY = "AWS Checksums"
DESCRIPTION = "Cross-Platform HW accelerated CRC32c and CRC32 with fallback to efficient SW implementations. C interface with language bindings for each of our SDKs"

HOMEPAGE = "https://github.com/awslabs/aws-checksums"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

DEPENDS += "\
    aws-c-common \
    s2n \
    "

PROVIDES += "aws/checksums"

BRANCH ?= "main"
SRC_URI = "\
    git://github.com/awslabs/aws-checksums.git;protocol=https;branch=${BRANCH} \
    file://run-ptest \
    "

SRCREV = "779b5f350eed7fb784bb3c6700b7532e3fdd84ff"

S = "${WORKDIR}/git"

inherit cmake ptest pkgconfig

PACKAGECONFIG ??= "\
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'with-tests', '', d)} \
    "

PACKAGECONFIG[with-tests] = "-DBUILD_TESTING=ON,-DBUILD_TESTING=OFF,"

# enable PACKAGECONFIG = "static" to build static instead of shared libs
PACKAGECONFIG[static] = "-DBUILD_SHARED_LIBS=OFF,-DBUILD_SHARED_LIBS=ON"

do_install_ptest () {
   install -d ${D}${PTEST_PATH}/tests
   cp -r ${B}/tests/* ${D}${PTEST_PATH}/tests/
   install -m 0755 ${B}/tests/aws-checksums-tests ${D}${PTEST_PATH}/tests/
}

# nooelint: oelint.vars.insaneskip:INSANE_SKIP
INSANE_SKIP:${PN}-ptest += "buildpaths"

AWS_C_INSTALL = "$D/usr"
CFLAGS:append = " -Wl,-Bsymbolic"
EXTRA_OECMAKE += "\
    -DCMAKE_MODULE_PATH=${STAGING_LIBDIR}/cmake \
    -DCMAKE_PREFIX_PATH=${STAGING_LIBDIR} \
    -DCMAKE_BUILD_TYPE=Release \
"

FILES:${PN}-dev += "${libdir}/*/cmake"

BBCLASSEXTEND = "native nativesdk"
