SUMMARY = "AWS C IO"
DESCRIPTION = "aws-c-io is an event driven framework for implementing application protocols. It is built on top of cross-platform abstractions that allow you as a developer to think only about the state machine and API for your protocols."

HOMEPAGE = "https://github.com/awslabs/aws-c-io"
LICENSE = "Apache-2.0"
PROVIDES += "aws/crt-c-io"

inherit cmake

LIC_FILES_CHKSUM = "file://aws-c-io/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

BRANCH ?= "main"

SRC_URI = "git://github.com/awslabs/aws-c-common.git;branch=${BRANCH};destsuffix=${S}/aws-c-common;name=common \
           git://github.com/awslabs/aws-c-io.git;branch=${BRANCH};destsuffix=${S}/aws-c-io;name=io \
"

SRCREV_common = "00c91eeb186970d50690ebbdceefdeae5c31fb4c"
SRCREV_io = "b629abed273cdc33e30e17c6a80edccc2a8daa67"

S = "${WORKDIR}/git"

DEPENDS = "openssl s2n aws-c-common aws-c-cal"
RDEPENDS_${PN} = "s2n aws-c-common aws-c-cal"

AWS_C_INSTALL = "$D/usr"
OECMAKE_SOURCEPATH = "${S}/aws-c-io"
CFLAGS_append = " -Wl,-Bsymbolic"
EXTRA_OECMAKE += "-DCMAKE_MODULE_PATH=${S}/aws-c-common/cmake"
EXTRA_OECMAKE += "-DCMAKE_PREFIX_PATH=$D/usr"
EXTRA_OECMAKE += "-DCMAKE_INSTALL_PREFIX=$D/usr"
OECMAKE_BUILDPATH += "${WORKDIR}/build"
OECMAKE_SOURCEPATH += "${S}"

INSANE_SKIP_${PN} += "installed-vs-shipped"
BBCLASSEXTEND = "native nativesdk"

