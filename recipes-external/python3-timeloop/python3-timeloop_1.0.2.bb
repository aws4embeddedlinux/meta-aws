SUMMARY = "Timeloop"
DESCRIPTION = "An elegant periodic task executor "
HOMEPAGE = "https://github.com/sankalpjonn/timeloop"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=030635eb18c38d9fd120c13a324721b7"

SRC_URI = "git://github.com/sankalpjonn/timeloop.git;protocol=https;branch=master"
SRCREV = "36ec5cbb133a6dcfb5316f59b5e11765c14e62c2"

S = "${WORKDIR}/git"

inherit setuptools3

RDEPENDS:${PN} += "python3-core"
