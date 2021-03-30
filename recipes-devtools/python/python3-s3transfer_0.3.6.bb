SUMMARY = "Amazon S3 Transfer Manager for Python"
HOMEPAGE = "https://github.com/boto/s3transfer"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=b1e01b26bacfc2232046c90a330332b3"

SRC_URI = "git://github.com/boto/s3transfer.git;protocol=https"
SRCREV = "fb386e433c11c961b1cab3178c31fd1b33056931"
S = "${WORKDIR}/git"

inherit setuptools3

RDEPENDS_${PN} += "python3-botocore \
                   python3-asyncio \
                   python3-urllib3 \
                   python3-multiprocessing"
