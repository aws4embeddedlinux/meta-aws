SUMMARY = "Amazon Kinesis Video Streams C Producer"
DESCRIPTION = "Amazon Kinesis Video Streams Producer SDK for C/C++ makes it easy to build an on-device application that securely connects to a video stream, and reliably publishes video and other media data to Kinesis Video Streams. It takes care of all the underlying tasks required to package the frames and fragments generated by the device's media pipeline. The SDK also handles stream creation, token rotation for secure and uninterrupted streaming, processing acknowledgements returned by Kinesis Video Streams, and other tasks."
HOMEPAGE = "https://github.com/awslabs/amazon-kinesis-video-streams-producer-c"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

DEPENDS += "\
    amazon-kvs-producer-pic \
    curl \
    libwebsockets \
    mbedtls \
    openssl \
    "

PROVIDES += "aws/amazon-kvs-producer-sdk-c"

BRANCH ?= "master"
SRC_URI = "\
    git://github.com/awslabs/amazon-kinesis-video-streams-producer-c.git;protocol=https;branch=${BRANCH}\
    "

# this recipe should be released only together with amazon-kvs-producer-sdk-pic and amazon-kvs-producer-sdk-cpp
UPSTREAM_VERSION_UNKNOWN = "1"
# set to match only git_invalid_tag_regex because UPSTREAM_VERSION_UNKNOWN seems to be broken for git
UPSTREAM_CHECK_GITTAGREGEX = "git_invalid_tag_regex"
SRCREV = "3e519b7670e39031375d227f983ad2cde888078e"

inherit cmake pkgconfig

# ptest are disabled, cause running tests require a certificate

PACKAGECONFIG ??= "\
     ${@bb.utils.contains('PTEST_ENABLED', '1', 'with-tests', '', d)} \
     "

# enable PACKAGECONFIG = "static" to build static instead of shared libs
PACKAGECONFIG[static] = "-DBUILD_SHARED_LIBS=OFF,-DBUILD_SHARED_LIBS=ON -DBUILD_STATIC=OFF,,"

PACKAGECONFIG[with-tests] = "-DBUILD_TEST=ON,-DBUILD_TEST=OFF,gtest"

FILES:${PN} += "\
    ${libdir}/pkgconfig/*.pc \
    "

FILES:${PN}-dev += "\
    ${includedir}/com/amazonaws/kinesis/video/* \
    ${libdir}/libkvsCommonLws.so \
    ${libdir}/libkvsCommonCurl.so \
    ${libdir}/libcproducer.so \
    "

RDEPENDS:${PN} = ""
CFLAGS:append = " -Wl,-Bsymbolic"

EXTRA_OECMAKE += "\
    -DBUILD_DEPENDENCIES=OFF \
    -DCODE_COVERAGE=OFF \
    -DBUILD_COMMON_LWS=ON \
    -DCOMPILER_WARNINGS=OFF \
    -DADDRESS_SANITIZER=OFF \
    -DMEMORY_SANITIZER=OFF \
    -DTHREAD_SANITIZER=OFF \
    -DUNDEFINED_BEHAVIOR_SANITIZER=OFF \
    -DDEBUG_HEAP=OFF \
    -DALIGNED_MEMORY_MODEL=OFF \
    -DCMAKE_BUILD_TYPE=Release \
"

# Notify that libraries are not versioned
FILES_SOLIBSDEV = ""

do_install_ptest () {
   install -d ${D}${PTEST_PATH}/tests
   cp -r ${B}/tst/* ${D}${PTEST_PATH}/tests/
}

BBCLASSEXTEND = "native nativesdk"
