# -*- mode: Conf; -*-
SUMMARY = "AWS Crt Cpp"
DESCRIPTION = "C++ wrapper around the aws-c-* libraries. Provides Cross-Platform Transport Protocols and SSL/TLS implementations for C++."

HOMEPAGE = "https://github.com/awslabs/aws-crt-cpp"
LICENSE = "Apache-2.0"
PROVIDES += "aws/crt-cpp"

inherit cmake

LIC_FILES_CHKSUM = "file://aws-crt-cpp/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

BRANCH ?= "main"

SRC_URI = "git://github.com/awslabs/aws-c-common.git;protocol=https;branch=${BRANCH};destsuffix=${S}/aws-c-common;name=common \
           git://github.com/awslabs/aws-crt-cpp.git;protocol=https;branch=${BRANCH};destsuffix=${S}/aws-crt-cpp;name=crtcpp \
"

SRCREV_FORMAT = "crtcpp"
SRCREV_crtcpp = "626047e24d966badd8253c56f728c9ad0065722a"
SRCREV_common = "2a28532d6f13435907ae200a5aea449c01e79149"

S = "${WORKDIR}/git"

PREFERRED_VERSION_aws-c-common = "0.6.8"
PREFERRED_VERSION_aws-c-io = "0.10.7"
PREFERRED_VERSION_aws-c-mqtt = "0.7.6"
PREFERRED_VERSION_aws-c-auth = "0.6.1"
PREFERRED_VERSION_aws-c-http = "0.6.5"
PREFERRED_VERSION_aws-checksums = "0.1.11"
PREFERRED_VERSION_aws-c-event-stream = "0.2.7"
PREFERRED_VERSION_s2n = "1.0.13"

DEPENDS = "s2n aws-c-common aws-c-io aws-c-mqtt aws-c-auth aws-c-http aws-checksums aws-c-event-stream aws-c-s3 aws-lc"
RDEPENDS:${PN} = "s2n aws-c-common"

CFLAGS:append = " -Wl,-Bsymbolic"
EXTRA_OECMAKE += "-DCMAKE_MODULE_PATH=${S}/aws-c-common/cmake"
EXTRA_OECMAKE += "-DCMAKE_PREFIX_PATH=$D${prefix}"
EXTRA_OECMAKE += "-DCMAKE_INSTALL_PREFIX=$D${prefix}"
EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=Release"
EXTRA_OECMAKE += "-DBUILD_DEPS=OFF"
EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=ON"
OECMAKE_BUILDPATH += "${WORKDIR}/build"
OECMAKE_SOURCEPATH += "${S}/aws-crt-cpp"

FILES:${PN} += "${libdir}/libaws-crt-cpp.so"
FILES:${PN}-dev = "${includedir}/aws/crt/* \
                   ${libdir}/aws-crt-cpp/* \
                   ${includedir}/aws/iot/*"
FILES:${PN}-dbg = "${prefix}/src/debug/aws-crt-cpp/* \
                   ${libdir}/.debug/lib${PN}.so.1.0.0"

# Notify that libraries are not versioned
FILES_SOLIBSDEV = ""

BBCLASSEXTEND = "native nativesdk"

