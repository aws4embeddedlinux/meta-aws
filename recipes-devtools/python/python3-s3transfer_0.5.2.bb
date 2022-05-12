# -*- mode: Conf; -*-
SUMMARY = "Amazon S3 Transfer Manager for Python"
HOMEPAGE = "https://github.com/boto/s3transfer"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI = "git://github.com/boto/s3transfer.git;protocol=https;branch=master"
SRCREV = "dea830bc3215b12ab1b5cb64f442c67576c1ecfb"

S = "${WORKDIR}/git"

inherit setuptools3

RDEPENDS:${PN} += "python3-botocore \
                   python3-asyncio \
                   python3-urllib3 \
                   python3-multiprocessing"
