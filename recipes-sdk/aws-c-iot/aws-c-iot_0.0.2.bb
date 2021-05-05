SUMMARY = "AWS C IoT"
DESCRIPTION = "C99 implementation of AWS IoT cloud services integration with devices"

HOMEPAGE = "https://github.com/awslabs/aws-c-iot"
LICENSE = "Apache-2.0"
PROVIDES += "aws/aws-c-iot"

inherit cmake

LIC_FILES_CHKSUM = "file://aws-c-iot/LICENSE;md5=2ee41112a44fe7014dce33e26468ba93"

BRANCH ?= "main"

SRC_URI = "git://github.com/awslabs/aws-c-common.git;branch=${BRANCH};destsuffix=${S}/aws-c-common;name=common;tag=v0.4.67 \
           git://github.com/awslabs/aws-c-iot.git;branch=${BRANCH};destsuffix=${S}/aws-c-iot;name=iot;tag=v0.0.2 \
"

S = "${WORKDIR}/git"

DEPENDS = "openssl s2n aws-c-common aws-c-cal aws-c-io aws-c-compression aws-c-http aws-c-mqtt"
RDEPENDS_${PN} = "s2n aws-c-common aws-c-cal aws-c-io aws-c-compression aws-c-http aws-c-mqtt"

OECMAKE_SOURCEPATH = "${S}/aws-c-iot"
CFLAGS_append = " -Wl,-Bsymbolic"
EXTRA_OECMAKE += "-DCMAKE_MODULE_PATH=${S}/aws-c-common/cmake"
EXTRA_OECMAKE += "-DCMAKE_PREFIX_PATH=$D/usr"
EXTRA_OECMAKE += "-DCMAKE_INSTALL_PREFIX=$D/usr"
OECMAKE_BUILDPATH += "${WORKDIR}/build"

PACKAGES = "${PN}"
INSANE_SKIP_${PN} += "installed-vs-shipped"
BBCLASSEXTEND = "native nativesdk"

