SUMMARY = "AWS C SDKUTILS"
DESCRIPTION = "No description or website provided. "

HOMEPAGE = "https://github.com/awslabs/aws-c-sdkutils"
LICENSE = "Apache-2.0"
PROVIDES += "aws/c-sdkutils"

inherit cmake

LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

SRC_URI = "git://github.com/awslabs/aws-c-sdkutils.git;protocol=https;branch=main"

SRCREV = "3b2cbbb12e58a91ddb3b5fcd8f680b66d738e2ba"

S = "${WORKDIR}/git"

DEPENDS = "aws-c-common"
RDEPENDS:${PN} = "aws-c-common"

CFLAGS:append = " -Wl,-Bsymbolic"
EXTRA_OECMAKE += "-DBUILD_TESTING=OFF"
EXTRA_OECMAKE += "-DCMAKE_MODULE_PATH=${STAGING_LIBDIR}/aws-c-common/cmake"
EXTRA_OECMAKE += "-DCMAKE_MODULE_PATH=${STAGING_LIBDIR}/cmake"
EXTRA_OECMAKE += "-DCMAKE_PREFIX_PATH=$D/usr"
EXTRA_OECMAKE += "-DCMAKE_INSTALL_PREFIX=$D/usr"
EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=ON"
EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=Release"
