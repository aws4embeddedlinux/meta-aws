SUMMARY = "Amazon Kinesis Video Streams C Producer"
DESCRIPTION = "Amazon Kinesis Video Streams Producer SDK for C/C++ makes it easy to build an on-device application that securely connects to a video stream, and reliably publishes video and other media data to Kinesis Video Streams. It takes care of all the underlying tasks required to package the frames and fragments generated by the device's media pipeline. The SDK also handles stream creation, token rotation for secure and uninterrupted streaming, processing acknowledgements returned by Kinesis Video Streams, and other tasks."
HOMEPAGE = "https://github.com/awslabs/amazon-kinesis-video-streams-producer-c"
LICENSE = "Apache-2.0"
PROVIDES += "aws/amazon-kvs-producer-sdk-c"

inherit cmake
inherit pkgconfig

LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

BRANCH ?= "master"
SRC_URI = "git://github.com/awslabs/amazon-kinesis-video-streams-producer-c.git;protocol=https;branch=${BRANCH}"

SRCREV = "80c74ac9200b58427a8fcb7782a03b1774020983"
S = "${WORKDIR}/git"

DEPENDS = "openssl curl gtest amazon-kvs-producer-pic mbedtls libwebsockets"
RDEPENDS:${PN} = ""
CFLAGS:append = " -Wl,-Bsymbolic"
OECMAKE_BUILDPATH += "${WORKDIR}/build"
OECMAKE_SOURCEPATH += "${S}"

EXTRA_OECMAKE += " \
    -DBUILD_DEPENDENCIES=OFF \
    -DBUILD_TEST=OFF \
    -DCODE_COVERAGE=OFF \
    -DCOMPILER_WARNINGS=OFF \
    -DADDRESS_SANITIZER=OFF \
    -DMEMORY_SANITIZER=OFF \
    -DTHREAD_SANITIZER=OFF \
    -DUNDEFINED_BEHAVIOR_SANITIZER=OFF \
    -DDEBUG_HEAP=OFF \
    -DALIGNED_MEMORY_MODEL=OFF \
    \
    -DCMAKE_BUILD_TYPE=Release \
    -DCMAKE_INSTALL_PREFIX=$D/usr \
"

FILES:${PN}     = "${libdir}/libcproducer.so \
                   ${libdir}/pkgconfig/*.pc"
FILES:${PN}-dev = "${includedir}/com/amazonaws/kinesis/video/*"

# Notify that libraries are not versioned
FILES_SOLIBSDEV = ""

BBCLASSEXTEND = "native nativesdk"
