SUMMARY = "Universal Command Line Interface for Amazon Web Services"
DESCRIPTION = "Universal Command Line Interface for Amazon Web Services and ptest scripts"
HOMEPAGE = "https://github.com/aws/aws-cli"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=7970352423db76abb33cbe303884afbf"

SRC_URI = "\
    git://github.com/aws/aws-cli.git;protocol=https;branch=master \
    file://run-ptest \
"

SRCREV = "36458af3714e11e1207eb14663c9a775e1676c81"

# version 2.x has got library link issues - so stick to version 1.x for now
UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>1\.\d+(\.\d+)+)"

S = "${WORKDIR}/git"

inherit setuptools3 ptest

RDEPENDS:${PN} += "\
    groff \
    python3-botocore \
    python3-colorama \
    python3-dateutil \
    python3-distro \
    python3-docutils \
    python3-jmespath \
    python3-misc \
    python3-prompt-toolkit \
    python3-pyyaml \
    python3-rsa \
    python3-ruamel-yaml \
    python3-s3transfer \
    python3-sqlite3 \
    python3-unixadmin \
    python3-urllib3 \
"

RDEPENDS:${PN}-ptest += "\
        ${PYTHON_PN}-pytest \
        bash \
"

do_install_ptest() {
        install -d ${D}${PTEST_PATH}/tests
        # just install some tests with low memory (less than 4GB) consumption
        cp -rf ${S}/tests/functional/test_args.py ${D}${PTEST_PATH}/tests/
}

PACKAGES =+ "${PN}-examples"

FILES:${PN}-examples = "${libdir}/${PYTHON_DIR}/site-packages/awscli/examples"
