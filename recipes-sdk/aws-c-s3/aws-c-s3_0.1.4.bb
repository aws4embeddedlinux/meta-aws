SUMMARY = "AWS C S3"
DESCRIPTION = "C99 library implementation for communicating with the S3 service, designed for maximizing throughput on high bandwidth EC2 instances."

HOMEPAGE = "https://github.com/awslabs/aws-c-s3"
LICENSE = "Apache-2.0"
PROVIDES += "aws/crt-c-s3"

inherit cmake

LIC_FILES_CHKSUM = "file://aws-c-s3/LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

BRANCH ?= "main"

SRC_URI = "git://github.com/awslabs/aws-c-common.git;branch=${BRANCH};destsuffix=${S}/aws-c-common;name=common \
           git://github.com/awslabs/aws-c-s3.git;branch=${BRANCH};destsuffix=${S}/aws-c-s3;name=s3 \
"

SRCREV_common = "00c91eeb186970d50690ebbdceefdeae5c31fb4c"
SRCREV_s3 = "f732c85e72666efba93833ead87786e455cf26ee"

S= "${WORKDIR}/git"

DEPENDS = "openssl aws-c-auth"
RDEPENDS_${PN} = "aws-c-auth"

CFLAGS_append = " -Wl,-Bsymbolic"

OECMAKE_SOURCEPATH = "${S}/aws-c-s3"
EXTRA_OECMAKE += "-DBUILD_TESTING=OFF"
EXTRA_OECMAKE += "-DCMAKE_MODULE_PATH=${S}/aws-c-common/cmake"
EXTRA_OECMAKE += "-DCMAKE_PREFIX_PATH=$D/usr"
EXTRA_OECMAKE += "-DCMAKE_INSTALL_PREFIX=$D/usr"
OECMAKE_BUILDPATH += "${WORKDIR}/build"
OECMAKE_SOURCEPATH += "${S}"

INSANE_SKIP_${PN} += "installed-vs-shipped"
BBCLASSEXTEND = "native nativesdk"
