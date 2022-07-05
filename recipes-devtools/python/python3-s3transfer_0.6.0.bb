# -*- mode: Conf; -*-
SUMMARY = "Amazon S3 Transfer Manager for Python"
HOMEPAGE = "https://github.com/boto/s3transfer"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI = "git://github.com/boto/s3transfer.git;protocol=https;branch=master"
SRCREV = "f7b9bde247a09f0938249fc94c8c4f74752969d8"

S = "${WORKDIR}/git"

inherit setuptools3
PIP_INSTALL_PACKAGE = "s3transfer"

RDEPENDS:${PN} += "python3-botocore \
                   python3-asyncio \
                   python3-urllib3 \
                   python3-multiprocessing"
