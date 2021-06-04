# -*- mode: Conf; -*-
SUMMARY = "AWS Crt Cpp"
DESCRIPTION = "C++ wrapper around the aws-c-* libraries. Provides Cross-Platform Transport Protocols and SSL/TLS implementations for C++."

HOMEPAGE = "https://github.com/awslabs/aws-crt-cpp"
LICENSE = "Apache-2.0"
PROVIDES += "aws/crt-cpp"

inherit cmake

LIC_FILES_CHKSUM = "file://aws-crt-cpp/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

BRANCH ?= "main"

SRC_URI = "git://github.com/awslabs/aws-c-common.git;branch=${BRANCH};destsuffix=${S}/aws-c-common;name=common \
           git://github.com/awslabs/aws-crt-cpp.git;branch=${BRANCH};destsuffix=${S}/aws-crt-cpp;name=crtcpp \
           file://01-aws-c-common-strict-flags-bypass.patch \
           file://02-aws-crt-cpp-strict-flags-bypass.patch \
           file://03-crt-0.11.8-missing-include-StringView.h.patch \
"

SRCREV_common = "00c91eeb186970d50690ebbdceefdeae5c31fb4c"
SRCREV_crtcpp = "5c4d306b5637e9c64bfefcbce7b0c03ac64611cc"

S = "${WORKDIR}/git"

PREFERRED_VERSION_aws-c-common = "0.4.67"
PREFERRED_VERSION_aws-c-io = "0.8.3"
PREFERRED_VERSION_aws-c-mqtt = "0.5.5"
PREFERRED_VERSION_aws-c-auth = "0.4.9"
PREFERRED_VERSION_aws-c-http = "0.5.9"
PREFERRED_VERSION_aws-checksums = "0.1.11"
PREFERRED_VERSION_aws-c-event-stream = "0.2.6"
PREFERRED_VERSION_s2n = "0.10.26"

DEPENDS = "openssl s2n aws-c-common aws-c-io aws-c-mqtt aws-c-auth aws-c-http aws-checksums aws-c-event-stream"
RDEPENDS_${PN} = "s2n aws-c-common"

OECMAKE_SOURCEPATH = "${S}/aws-crt-cpp"
CFLAGS_append = " -Wl,-Bsymbolic"
EXTRA_OECMAKE += "-DCMAKE_MODULE_PATH=${S}/aws-c-common/cmake"
EXTRA_OECMAKE += "-DCMAKE_PREFIX_PATH=$D/usr"
EXTRA_OECMAKE += "-DCMAKE_INSTALL_PREFIX=$D/usr"
EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=Release"
OECMAKE_BUILDPATH += "${WORKDIR}/build"
OECMAKE_SOURCEPATH += "${S}"

PACKAGES = "${PN}"
INSANE_SKIP_${PN} += "installed-vs-shipped"
BBCLASSEXTEND = "native nativesdk"

