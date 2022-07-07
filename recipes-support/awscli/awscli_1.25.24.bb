SUMMARY = "Universal Command Line Interface for Amazon Web Services"
HOMEPAGE = "https://github.com/aws/aws-cli"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=7970352423db76abb33cbe303884afbf"

SRC_URI = "git://github.com/aws/aws-cli.git;protocol=https;branch=v2"
SRCREV = "efe934bd5906f1955d576c024a3297043325615b"
S = "${WORKDIR}/git"

#version 2.x has got library link issues - so stick to version 1.x for now
UPSTREAM_CHECK_GITTAGREGEX = "1(?P<pver>.*)"

inherit setuptools3

RDEPENDS:${PN} += " \
    python3-botocore \
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
    python3-dateutil \
    python3-urllib3 \
    python3-jmespath \
    groff \
"
