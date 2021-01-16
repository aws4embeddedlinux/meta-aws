LICENSE = "BSD"
LIC_FILES_CHKSUM  = "file://LICENSE.txt;md5=17e3ee10f678b8903ff95af5cda9023a"

SRC_URI += "https://github.com/Toblerity/Shapely/archive/${PV}.tar.gz"
SRC_URI[sha256sum] = "8a39f78a5bb21bc7a0f6e3aa9d78291fad53a433346ffa2474076698da47f24c"

S = "${WORKDIR}/Shapely-${PV}"

inherit setuptools3
export PYTHON_CROSSENV = "1"

DEPENDS += "geos"
RDEPENDS_${PN} += "${PYTHON_PN}-numpy geos"
