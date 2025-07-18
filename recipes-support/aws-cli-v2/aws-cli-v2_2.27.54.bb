# FIXME: the LIC_FILES_CHKSUM values have been updated by 'devtool upgrade'.
# The following is the difference between the old and the new license text.
# Please update the LICENSE value if needed, and summarize the changes in
# the commit message via 'License-Update:' tag.
# (example: 'License-Update: copyright years updated.')
#
# The changes:
#
# --- LICENSE.txt
# +++ LICENSE.txt
# @@ -1,4 +1,4 @@
# -Copyright 2012-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
# +Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
#  Licensed under the Apache License, Version 2.0 (the "License"). You
#  may not use this file except in compliance with the License. A copy of
#
#

SUMMARY = "Universal Command Line Interface for Amazon Web Services v2"
DESCRIPTION = "Universal Command Line Interface for Amazon Web Services and ptest scripts v2"
HOMEPAGE = "https://github.com/aws/aws-cli/tree/v2"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=8ad764cee43b1595056063799f3f79a5"

DEPENDS += "\
    aws-crt-python-native \
    groff \
    openssl-native \
    python3-botocore \
    python3-colorama-native \
    python3-cryptography-native \
    python3-dateutil-native \
    python3-distro-native \
    python3-docutils-native \
    python3-flit-core \
    python3-jmespath-native \
    python3-native \
    python3-prompt-toolkit-native \
    python3-pyyaml-native \
    python3-rsa \
    python3-ruamel-yaml-clib-native \
    python3-ruamel-yaml-native \
    python3-s3transfer \
    python3-urllib3-1.x-native \
    python3-zipp-native \
"

SRC_URI = "\
    git://github.com/aws/aws-cli.git;protocol=https;branch=v2 \
    file://run-ptest \
"

SRCREV = "d7e963635a72d1dd8978bf765b288cf610adb93e"

# version 2.x
UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>2\.\d+(\.\d+)+)"

inherit python_pep517 python3native python3-dir setuptools3-base ptest

export CRYPTOGRAPHY_OPENSSL_NO_LEGACY = "true"

# this package also contains aws help
PACKAGES += "${PN}-examples"

FILES:${PN}-examples += "${libdir}/${PYTHON_DIR}/site-packages/awscli/examples"

RDEPENDS:${PN} += "\
    aws-crt-python \
    openssl \
    python3-botocore \
    python3-colorama \
    python3-compression \
    python3-core \
    python3-dateutil \
    python3-distro \
    python3-docutils \
    python3-elementpath \
    python3-io \
    python3-ipaddress \
    python3-jmespath \
    python3-json \
    python3-logging \
    python3-misc \
    python3-prompt-toolkit \
    python3-rsa \
    python3-ruamel-yaml \
    python3-sqlite3 \
    python3-unixadmin \
    python3-urllib3-1.x \
    python3-zipp \
"

do_patch() {
    sed -i -E 's/(([a-zA-Z0-9_.-]+)(>=?[0-9.]+)?(,)?(<[0-9.]+\*|<=?[0-9.]+)?|([a-zA-Z0-9_.-]+)==[0-9.]+)/\2\3\6/' ${S}/pyproject.toml ${S}/requirements/bootstrap.txt
}

do_install_ptest() {
        install -d ${D}${PTEST_PATH}/tests
        # just install some tests with low memory (less than 4GB) consumption
        cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

RDEPENDS:${PN}-ptest += "\
        bash \
        python3-mock \
        python3-pytest \
        python3-venv \
"

RDEPENDS:${PN}-examples += "\
        groff \
        less \
"

RRECOMMENDS:${PN} = "${PN}-examples"

RCONFLICTS:${PN} = "awscli"
