SUMMARY = "Amazon Kinesis Video Streams PIC"
DESCRIPTION = "Platform independent code layer for KVS Producer SDK"
HOMEPAGE = "https://github.com/awslabs/amazon-kinesis-video-streams-pic"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"
PROVIDES += "aws/amazon-kvs-producer-pic"

BRANCH ?= "master"
SRC_URI = "git://github.com/awslabs/amazon-kinesis-video-streams-pic.git;protocol=https;branch=${BRANCH} \
    file://run-ptest \
    "

# this recipe should be released only together with amazon-kvs-producer-sdk-c and amazon-kvs-producer-sdk-cpp
UPSTREAM_VERSION_UNKNOWN = "1"
# set to match only git_invalid_tag_regex because UPSTREAM_VERSION_UNKNOWN seems to be broken for git
UPSTREAM_CHECK_GITTAGREGEX = "git_invalid_tag_regex"
# this SRCREV commit id should not different than this:
# https://github.com/awslabs/amazon-kinesis-video-streams-producer-c/blob/80c74ac9200b58427a8fcb7782a03b1774020983/CMake/Dependencies/libkvspic-CMakeLists.txt#L10
SRCREV = "c8325887faa3a4a296c4367b281c778be69875b6"

S = "${WORKDIR}/git"

inherit cmake pkgconfig ptest

PACKAGECONFIG ??= "\
     ${@bb.utils.contains('PTEST_ENABLED', '1', 'with-tests', '', d)} \
     "

# enable PACKAGECONFIG = "static" to build static instead of shared libs
PACKAGECONFIG[static] = "-DBUILD_SHARED_LIBS=OFF,-DBUILD_SHARED_LIBS=ON,"

PACKAGECONFIG[with-tests] = "-DBUILD_TEST=ON,-DBUILD_TEST=OFF,gtest"

CFLAGS:append = " -Wl,-Bsymbolic"

EXTRA_OECMAKE += "\
    -DBUILD_DEPENDENCIES=OFF \
    -DCODE_COVERAGE=OFF \
    -DADDRESS_SANITIZER=OFF \
    -DMEMORY_SANITIZER=OFF \
    -DTHREAD_SANITIZER=OFF \
    -DUNDEFINED_BEHAVIOR_SANITIZER=OFF \
    -DDEBUG_HEAP=OFF \
    -DALIGNED_MEMORY_MODEL=OFF \
    -DCMAKE_BUILD_TYPE=Release \
    -DCMAKE_INSTALL_PREFIX=$D/usr \
"

FILES:${PN} += "${libdir}/pkgconfig/*.pc"

FILES:${PN}-dev += "${includedir}/com/amazonaws/kinesis/video/*"

FILES:${PN}-ptest += "${libdir}/*.so"

# Notify that libraries are not versioned
FILES_SOLIBSDEV = ""

do_install_ptest () {
   install -d ${D}${PTEST_PATH}/tests
   cp -r ${B}/kvspic_test ${D}${PTEST_PATH}/tests/
}

BBCLASSEXTEND = "native nativesdk"
