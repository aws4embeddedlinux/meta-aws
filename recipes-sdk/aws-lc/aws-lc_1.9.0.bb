SUMMARY = "AWS libcrypto (AWS-LC)"
DESCRIPTION = "AWS-LC is a general-purpose cryptographic library maintained by the AWS Cryptography team for AWS and their customers. It іs based on code from the Google BoringSSL project and the OpenSSL project."

HOMEPAGE = "https://github.com/awslabs/aws-lc"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=006f3752e7de4899a9fc5a48db9d45a4"

PROVIDES += "aws/lc"

BRANCH ?= "main"

SRC_URI = "\
    git://github.com/awslabs/aws-lc.git;protocol=https;branch=${BRANCH} \
    file://run-ptest \
    "
SRCREV = "bf9cd886a792740df5b21eb196ecc10d7dda9b6e"

S = "${WORKDIR}/git"

inherit cmake ptest pkgconfig

PACKAGECONFIG ??= "\
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'with-tests', '', d)} \
    "

# CMAKE_CROSSCOMPILING=ON will disable building the tests
PACKAGECONFIG[with-tests] = "-DBUILD_TESTING=ON -DCMAKE_CROSSCOMPILING=OFF,-DBUILD_TESTING=OFF,"

# enable PACKAGECONFIG = "static" to build static instead of shared libs
PACKAGECONFIG[static] = "-DBUILD_SHARED_LIBS=OFF,-DBUILD_SHARED_LIBS=ON"

do_install_ptest () {
   install -d ${D}${PTEST_PATH}/tests
   cp -r ${B}/libboringssl_gtest.so ${D}${PTEST_PATH}/tests/
   install -m 0755 ${B}/ssl/ssl_test ${D}${PTEST_PATH}/tests/
}

EXTRA_OECMAKE += "\
    -DDISABLE_PERL=ON \
    -DDISABLE_GO=ON \
"

FILES:${PN} += "\
    ${libdir}/libcrypto.so \
    ${libdir}/libssl.so \
    ${libdir}/libdecrepit.so \
    "

FILES:${PN}-dev += "${libdir}/*/cmake"

# also test depend-on-us packages build
RDEPENDS:${PN}-ptest = "\
    aws-c-auth \
    aws-c-cal \
    aws-c-compression \
    aws-c-io \
    aws-c-s3 \
    aws-checksums \
    "

# Notify that libraries are not versioned
FILES_SOLIBSDEV = ""

BBCLASSEXTEND = "native nativesdk"
