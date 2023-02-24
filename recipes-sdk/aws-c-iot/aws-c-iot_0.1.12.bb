SUMMARY = "AWS C IoT"
DESCRIPTION = "C99 implementation of AWS IoT cloud services integration with devices"

HOMEPAGE = "https://github.com/awslabs/aws-c-iot"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2ee41112a44fe7014dce33e26468ba93"

DEPENDS += "\
    aws-c-auth \
    aws-c-common \
    aws-c-event-stream \
    aws-c-http \
    aws-c-io \
    aws-c-mqtt \
    aws-c-s3 \
    aws-checksums \
    aws-crt-cpp \
    aws-lc \
    s2n \
    "

PROVIDES += "aws/aws-c-iot"

BRANCH ?= "main"

SRC_URI = "\
    git://github.com/awslabs/aws-c-iot.git;protocol=https;branch=${BRANCH} \
    file://run-ptest \
    "

SRCREV = "09ded2b5e5bd34bbcf0fd71b5482381cf7f08627"

S = "${WORKDIR}/git"

inherit cmake ptest

FILES:${PN}-dev += "${libdir}/*/cmake"

RDEPENDS:${PN} = "\
    aws-c-common \
    aws-crt-cpp \
    "

CFLAGS:append = " -Wl,-Bsymbolic"
EXTRA_OECMAKE += "\
    -DCMAKE_MODULE_PATH=${STAGING_LIBDIR}/cmake \
    -DCMAKE_PREFIX_PATH=$D/usr \
    -DCMAKE_INSTALL_PREFIX=$D/usr \
    -DBUILD_SHARED_LIBS=ON \
"

do_install_ptest () {
   install -d ${D}${PTEST_PATH}/tests
   cp -r ${B}/tests/* ${D}${PTEST_PATH}/tests/
   install -m 0755 ${B}/tests/aws-c-iot-tests ${D}${PTEST_PATH}/tests/
}

BBCLASSEXTEND = "native nativesdk"

