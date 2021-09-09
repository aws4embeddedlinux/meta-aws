# -*- mode: Conf; -*-
SUMMARY = "AWS libcrypto (AWS-LC)"
DESCRIPTION = "AWS-LC is a general-purpose cryptographic library maintained by the AWS Cryptography team for AWS and their customers. It іs based on code from the Google BoringSSL project and the OpenSSL project."

HOMEPAGE = "https://github.com/awslabs/aws-lc"
LICENSE = "Apache-2.0"
PROVIDES += "aws/lc"

inherit cmake

LIC_FILES_CHKSUM = "file://LICENSE;md5=c1afc79d796415ed8191ba3258b73e3a"

BRANCH ?= "main"
TAG ?= "v${PV}"

SRC_URI = "git://github.com/awslabs/aws-lc.git;branch=${BRANCH};tag=${TAG}"

S = "${WORKDIR}/git"

DEPENDS = ""
RDEPENDS:${PN} = ""

EXTRA_OECMAKE += "-DBUILD_TESTING=OFF"
EXTRA_OECMAKE += "-DDISABLE_PERL=ON"
EXTRA_OECMAKE += "-DDISABLE_GO=ON"

OECMAKE_BUILDPATH += "${WORKDIR}/build"
OECMAKE_SOURCEPATH += "${S}"

INSANE_SKIP:${PN} += "installed-vs-shipped"
BBCLASSEXTEND = "native nativesdk"

