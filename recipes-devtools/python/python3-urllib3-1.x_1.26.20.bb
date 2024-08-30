SUMMARY = "Python HTTP library"
DESCRIPTION = "Python HTTP library with thread-safe connection pooling, file post support, sanity friendly, and more"
HOMEPAGE = "https://github.com/shazow/urllib3"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=c2823cb995439c984fd62a973d79815c"

# version 1.x.x
UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>1\.\d+(\.\d+)+)"

inherit setuptools3

PIP_INSTALL_PACKAGE = "urllib3"

S = "${WORKDIR}/git"
SRC_URI = "git://github.com/urllib3/urllib3.git;protocol=https;branch=1.26.x"
SRCREV = "6f2ad7ca0cdde53751bab29cbc10bcc965bb4387"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-certifi \
    ${PYTHON_PN}-cryptography \
    ${PYTHON_PN}-email \
    ${PYTHON_PN}-idna \
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-pyopenssl \
    ${PYTHON_PN}-threading \
"

CVE_PRODUCT = "urllib3"

BBCLASSEXTEND = "native nativesdk"

RCONFLICTS:${PN} = "python3-urllib3"

RREPLACES:${PN} = "python3-urllib3 (< 2)"
