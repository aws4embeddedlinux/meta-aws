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

EXTRA_OECMAKE += "-DBUILD_DEPENDENCIES=OFF"
EXTRA_OECMAKE += "-DBUILD_TEST=FALSE"
EXTRA_OECMAKE += "-DCODE_COVERAGE=FALSE"
EXTRA_OECMAKE += "-DCOMPILER_WARNINGS=OFF"
EXTRA_OECMAKE += "-DADDRESS_SANITIZER=OFF"
EXTRA_OECMAKE += "-DMEMORY_SANITIZER=OFF"
EXTRA_OECMAKE += "-DTHREAD_SANITIZER=OFF"
EXTRA_OECMAKE += "-DUNDEFINED_BEHAVIOR_SANITIZER=OFF"
EXTRA_OECMAKE += "-DDEBUG_HEAP=OFF"
EXTRA_OECMAKE += "-DALIGNED_MEMORY_MODEL=OFF"

EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=Release"
EXTRA_OECMAKE += "-DCMAKE_INSTALL_PREFIX=$D/usr"

FILES:${PN}     = "${libdir}/pkgconfig/*.pc"
FILES:${PN}-dev = "${includedir}/com/amazonaws/kinesis/video/*"
FILES:${PN}-dbg = ""

# Notify that libraries are not versioned
FILES_SOLIBSDEV = ""

BBCLASSEXTEND = "native nativesdk"
