SUMMARY = "AWS C Auth"
DESCRIPTION = "C99 library implementation of AWS client-side authentication: standard credentials providers and signing."

HOMEPAGE = "https://github.com/awslabs/aws-c-auth"
LICENSE = "Apache-2.0"
PROVIDES += "aws/crt-c-auth"

inherit cmake

LIC_FILES_CHKSUM = "file://aws-c-auth/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

BRANCH ?= "main"

SRC_URI = "git://github.com/awslabs/aws-c-common.git;branch=${BRANCH};destsuffix=${S}/aws-c-common;name=common \
           git://github.com/awslabs/aws-c-auth.git;branch=${BRANCH};destsuffix=${S}/aws-c-auth;name=auth \
"

SRCREV_common = "00c91eeb186970d50690ebbdceefdeae5c31fb4c"
SRCREV_auth = "f58e2028d183e0e889728249a5aa5097d58a5e5c"

S= "${WORKDIR}/git"

DEPENDS = "openssl s2n aws-c-common aws-c-cal aws-c-io aws-c-compression aws-c-http"
RDEPENDS_${PN} = "s2n aws-c-common aws-c-cal aws-c-io aws-c-compression aws-c-http"

CFLAGS_append = " -Wl,-Bsymbolic"

OECMAKE_SOURCEPATH = "${S}/aws-c-auth"
EXTRA_OECMAKE += "-DBUILD_TESTING=OFF"
EXTRA_OECMAKE += "-DCMAKE_MODULE_PATH=${S}/aws-c-common/cmake"
EXTRA_OECMAKE += "-DCMAKE_PREFIX_PATH=$D/usr"
EXTRA_OECMAKE += "-DCMAKE_INSTALL_PREFIX=$D/usr"
OECMAKE_BUILDPATH += "${WORKDIR}/build"
OECMAKE_SOURCEPATH += "${S}"

INSANE_SKIP_${PN} += "installed-vs-shipped"
BBCLASSEXTEND = "native nativesdk"
