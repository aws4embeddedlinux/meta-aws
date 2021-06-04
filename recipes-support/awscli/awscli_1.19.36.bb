SUMMARY = "Universal Command Line Interface for Amazon Web Services"
HOMEPAGE = "https://github.com/aws/aws-cli"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=7970352423db76abb33cbe303884afbf"

SRC_URI = "git://github.com/aws/aws-cli.git;protocol=https"
SRCREV = "079f03758d0db7f430cc5f9787e5982a0586375a"
S = "${WORKDIR}/git"

inherit setuptools3

RDEPENDS_${PN} += "python3-botocore \
                   python3-docutils \
                   python3-s3transfer \
                   python3-pyyaml \
                   python3-colorama \
                   python3-distro \
                   python3-unixadmin \
                   python3-ruamel-yaml \
                   python3-prompt-toolkit \
                   python3-sqlite3 \
                   python3-misc \
                   python3-rsa \
                   groff"
