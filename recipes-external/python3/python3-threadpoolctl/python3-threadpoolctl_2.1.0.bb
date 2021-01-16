SUMMARY = "Python helpers to limit the number of threads used in native libraries that handle their own internal threadpool (BLAS and OpenMP implementations)"
HOMEPAGE = "https://github.com/joblib/threadpoolctl"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8f2439cfddfbeebdb5cac3ae4ae80eaf"

PYPI_PACKAGE = "threadpoolctl"

SRC_URI[md5sum] = "7f7f76e1ac12be086c9e02912a4caf68"
SRC_URI[sha256sum] = "ddc57c96a38beb63db45d6c159b5ab07b6bced12c45a1f07b2b92f272aebfa6b"

inherit pypi
inherit setuptools3