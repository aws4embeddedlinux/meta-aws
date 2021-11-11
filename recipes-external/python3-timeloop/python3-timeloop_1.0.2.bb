SUMMARY = "Timeloop"
DESCRIPTION = ""
HOMEPAGE = ""
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=030635eb18c38d9fd120c13a324721b7"

inherit setuptools3

SRC_URI = "git://github.com/sankalpjonn/timeloop.git;protocol=https;tag=v1.0.2"

S = "${WORKDIR}/git"

RDEPENDS_${PN} += "python3"

