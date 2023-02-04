SUMMARY = "AWS Checksums"
DESCRIPTION = "Cross-Platform HW accelerated CRC32c and CRC32 with fallback to efficient SW implementations. C interface with language bindings for each of our SDKs"

HOMEPAGE = "https://github.com/awslabs/aws-checksums"
LICENSE = "Apache-2.0"
PROVIDES += "aws/checksums"

inherit cmake

LIC_FILES_CHKSUM = "file://aws-checksums/LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

BRANCH ?= "main"

SRC_URI = "https://github.com/awslabs/aws-c-common.git;branch=${BRANCH};destsuffix=${S}/aws-c-common;name=common \
           https://github.com/awslabs/aws-checksums.git;branch=${BRANCH};destsuffix=${S}/aws-checksums;name=checksums \
"

SRCREV_common = "00c91eeb186970d50690ebbdceefdeae5c31fb4c"
SRCREV_checksums = "99bb0ad4b89d335d638536694352c45e0d2188f5"

S = "${WORKDIR}/git"

DEPENDS = "openssl s2n aws-c-common"
RDEPENDS_${PN} = "s2n aws-c-common"

AWS_C_INSTALL = "$D/usr"
OECMAKE_SOURCEPATH = "${S}/aws-checksums"
CFLAGS_append = " -Wl,-Bsymbolic"
EXTRA_OECMAKE += "-DBUILD_TEST_DEPS=OFF"
EXTRA_OECMAKE += "-DBUILD_TESTING=OFF"
EXTRA_OECMAKE += "-DCMAKE_MODULE_PATH=${S}/aws-c-common/cmake"
EXTRA_OECMAKE += "-DCMAKE_PREFIX_PATH=$D/usr"
EXTRA_OECMAKE += "-DCMAKE_INSTALL_PREFIX=$D/usr"
OECMAKE_BUILDPATH += "${WORKDIR}/build"
OECMAKE_SOURCEPATH += "${S}"

PACKAGES = "${PN}"
INSANE_SKIP_${PN} += "installed-vs-shipped"
BBCLASSEXTEND = "native nativesdk"

