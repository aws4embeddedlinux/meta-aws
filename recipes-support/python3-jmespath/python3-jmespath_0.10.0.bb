SUMMARY = "JMESPath"
DESCRIPTION = ""
HOMEPAGE = ""
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=2683790f5fabb41a3f75b70558799eb4"

inherit setuptools3

SRC_URI = "git://github.com/jmespath/jmespath.py.git;tag=0.10.0"

S = "${WORKDIR}/git"

RDEPENDS_${PN} += "python3"

