# -*- mode: Conf; -*-
SUMMARY = "The low-level, core functionality of boto 3."
HOMEPAGE = "https://github.com/boto/botocore"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=2ee41112a44fe7014dce33e26468ba93"

SRC_URI = "git://github.com/boto/botocore.git;protocol=https;branch=master"
SRCREV = "2d4b5282422ccc738fbba55c7a2d9ed10904ddf0"
S = "${WORKDIR}/git"

inherit setuptools3
PIP_INSTALL_PACKAGE = "botocore"

RDEPENDS:${PN} += "python3-jmespath python3-dateutil python3-logging"
