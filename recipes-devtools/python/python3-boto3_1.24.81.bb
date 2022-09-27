# -*- mode: Conf; -*-
SUMMARY = "Boto3 is the Amazon Web Services (AWS) Software Development Kit (SDK) for Python, which allows Python developers to write software that makes use of services like Amazon S3 and Amazon EC2. You can find the latest, most up to date, documentation at our doc site, including a list of services that are supported."
HOMEPAGE = "https://github.com/boto/boto3"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2ee41112a44fe7014dce33e26468ba93"

SRC_URI = "git://github.com/boto/boto3.git;protocol=https;branch=master"
SRCREV = "2c2208aea01fa5e184705b1d2042c580ed2cceee"
S = "${WORKDIR}/git"

inherit setuptools3
PIP_INSTALL_PACKAGE = "boto3"

# python3 needs to be included since there are core dependencies such
# as getpass.
RDEPENDS:${PN} += " python3 python3-jmespath python3-botocore python3-s3transfer python3-logging"
