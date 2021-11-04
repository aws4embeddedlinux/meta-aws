SUMMARY = "This package provides a unified command line interface to Amazon Web Services."
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=7970352423db76abb33cbe303884afbf"

PYPI_PACKAGE = "awscli"

SRC_URI[md5sum] = "99416f2ef5db9248ae8245a84928e6ef"
SRC_URI[sha256sum] = "327416401b8616269d85fa3ddfe84ad2d6ef3e8a9bcd990c84be9cdf8085516f"

inherit pypi setuptools3

RDEPENDS_${PN} += "\
    python3 \
    python3-botocore \
    python3-docutils \
    python3-s3transfer \
    python3-pyyaml \
    python3-colorama \
    python3-rsa \
    groff \
    "
