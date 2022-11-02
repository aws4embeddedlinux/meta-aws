# -*- mode: Conf; -*-
SUMMARY = "Amazon Kinesis Video Streams PIC"
DESCRIPTION = "Platform independent code layer for KVS Producer SDK"
HOMEPAGE = "https://github.com/awslabs/amazon-kinesis-video-streams-pic"
LICENSE = "Apache-2.0"
PROVIDES += "aws/amazon-kvs-producer-pic"

inherit cmake

BRANCH ?= "master"
SDIR ?= "amazon-kvs-producer-pic"

LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

SRC_URI = "git://github.com/awslabs/amazon-kinesis-video-streams-pic.git;protocol=https;branch=${BRANCH}"

# this project do not use version tags, use latest commit
UPSTREAM_CHECK_COMMITS = "1"
SRCREV = "afc15aff555090424d6b92ef316116e85004a479"

S = "${WORKDIR}/git"

DEPENDS = "gtest "
RDEPENDS:${PN} = ""
CFLAGS:append = " -Wl,-Bsymbolic"
OECMAKE_BUILDPATH += "${WORKDIR}/build"

EXTRA_OECMAKE += " \
    -DBUILD_DEPENDENCIES=OFF \
    -DBUILD_TEST=FALSE \
    -DCODE_COVERAGE=FALSE \
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

FILES:${PN}     = "${libdir}/pkgconfig/*.pc"
FILES:${PN}-dev = "${includedir}/com/amazonaws/kinesis/video/*"

# Notify that libraries are not versioned
FILES_SOLIBSDEV = ""

BBCLASSEXTEND = "native nativesdk"
