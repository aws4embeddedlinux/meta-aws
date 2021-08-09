SUMMARY = "AWS C Event Stream"
DESCRIPTION = "C99 implementation of the vnd.amazon.event-stream content-type."

HOMEPAGE = "https://github.com/awslabs/aws-c-event-stream"
LICENSE = "Apache-2.0"
PROVIDES += "aws/crt-c-event-stream"

inherit cmake

LIC_FILES_CHKSUM = "file://aws-c-event-stream/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

BRANCH ?= "main"

SRC_URI = "git://github.com/awslabs/aws-c-common.git;branch=${BRANCH};destsuffix=${S}/aws-c-common;name=common \
           git://github.com/awslabs/aws-c-event-stream.git;branch=${BRANCH};destsuffix=${S}/aws-c-event-stream;name=es \
"

SRCREV_common = "00c91eeb186970d50690ebbdceefdeae5c31fb4c"
SRCREV_es = "5bcc8b09b7dd07205b919558becf662ea188578b"

S = "${WORKDIR}/git"

DEPENDS = "openssl s2n aws-c-common aws-checksums aws-c-io"
RDEPENDS_${PN} = "s2n aws-c-common aws-checksums aws-c-io"

AWS_C_INSTALL = "$D/usr"
OECMAKE_SOURCEPATH = "${S}/aws-c-event-stream"
CFLAGS_append = " -Wl,-Bsymbolic"
EXTRA_OECMAKE += "-DBUILD_TEST_DEPS=OFF"
EXTRA_OECMAKE += "-DBUILD_TESTING=OFF"
EXTRA_OECMAKE += "-DCMAKE_MODULE_PATH=${S}/aws-c-common/cmake"
EXTRA_OECMAKE += "-DCMAKE_PREFIX_PATH=$D/usr"
EXTRA_OECMAKE += "-DCMAKE_INSTALL_PREFIX=$D/usr"
OECMAKE_BUILDPATH += "${WORKDIR}/build"
OECMAKE_SOURCEPATH += "${S}"

INSANE_SKIP_${PN} += "installed-vs-shipped"
BBCLASSEXTEND = "native nativesdk"

