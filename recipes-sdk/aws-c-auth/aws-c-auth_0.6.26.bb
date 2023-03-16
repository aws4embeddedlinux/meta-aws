SUMMARY = "AWS C Auth"
DESCRIPTION = "C99 library implementation of AWS client-side authentication: standard credentials providers and signing."

HOMEPAGE = "https://github.com/awslabs/aws-c-auth"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS += "\
    aws-c-cal \
    aws-c-common \
    aws-c-compression \
    aws-c-http \
    aws-c-io \
    aws-c-sdkutils \
    openssl \
    s2n \
    "

PROVIDES += "aws/crt-c-auth"

BRANCH ?= "main"
SRC_URI = "\
    git://github.com/awslabs/aws-c-auth.git;protocol=https;branch=${BRANCH} \
    file://run-ptest \
    "
SRCREV = "54f8d804120daab9e0d75a56a113a222b334d0f9"

S = "${WORKDIR}/git"

inherit cmake ptest pkgconfig

CFLAGS:append = " -Wl,-Bsymbolic"

EXTRA_OECMAKE += "\
    -DCMAKE_MODULE_PATH=${STAGING_LIBDIR}/cmake \
    -DCMAKE_PREFIX_PATH=$D/usr \
    -DCMAKE_INSTALL_PREFIX=$D/usr \
    -DBUILD_SHARED_LIBS=ON \
    -DCMAKE_BUILD_TYPE=Release \
"

PACKAGECONFIG ??= "\
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'with-tests', '', d)} \
    "

PACKAGECONFIG[with-tests] = "-DBUILD_TESTING=ON,-DBUILD_TESTING=OFF,"

FILES:${PN}-dev += "${libdir}/*/cmake"

RDEPENDS:${PN} += "\
    aws-c-cal \
    aws-c-common \
    aws-c-compression \
    aws-c-http \
    aws-c-io \
    aws-c-sdkutils \
    s2n \
    "

do_install_ptest () {
   install -d ${D}${PTEST_PATH}/tests
   cp -r ${B}/tests/* ${D}${PTEST_PATH}/tests/
   install -m 0755 ${B}/tests/aws-c-auth-tests ${D}${PTEST_PATH}/tests/
}

BBCLASSEXTEND = "native nativesdk"
