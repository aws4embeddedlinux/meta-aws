SUMMARY = "A set of python modules for machine learning and data mining"
HOMEPAGE = "http://scikit-learn.org"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=c8d7e027b3e67a2b1fe7fe85ebeb17d7"

SRC_URI = "git://github.com/scikit-learn/scikit-learn.git;branch=0.24.X"
SRCREV = "45a817933ef51a24f0c5863c1026b4fe664b26fa"
S = "${WORKDIR}/git"

inherit setuptools3

export PYTHON_CROSSENV = "1"

RDEPENDS_${PN} += "${PYTHON_PN}-numpy ${PYTHON_PN}-pytest ${PYTHON_PN}-scipy ${PYTHON_PN}-threadpoolctl ${PYTHON_PN}-joblib"
DEPENDS += "${PYTHON_PN}-cython-native ${PYTHON_PN}-numpy-native ${PYTHON_PN}-scipy-native"
