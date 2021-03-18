SUMMARY = "NEO-AI Deep Learning Runtime"
DESCRIPTION = "Neo-AI-DLR is a common runtime for machine learning models compiled by AWS SageMaker Neo, TVM, or TreeLite."
HOMEPAGE = "https://aws.amazon.com/sagemaker/neo/"
LICENSE = "Apache-2.0 & BSD-3-Clause"

LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

BRANCH ?= "main"

SRC_URI = "git://github.com/neo-ai/neo-ai-dlr.git;branch=${BRANCH};protocol=https;name=neo-ai-dlr \
           file://0002-CMakeLists_remove_test_file_downloads.patch \
           file://0003-Change_DLR_Library_Location_in_setup_py.patch \
           https://neo-ai-dlr-test-artifacts.s3-us-west-2.amazonaws.com/tflite-models/cat224-3.txt;name=cat224-3 \
           https://neo-ai-dlr-test-artifacts.s3-us-west-2.amazonaws.com/test-data/street_small.npy;name=streetsmall \
           https://neo-ai-dlr-test-artifacts.s3-us-west-2.amazonaws.com/compiled-models/resnet_v1.5_50-ml_c4.tar.gz;name=resnet;subdir=resnet_v1_5_50 \
           https://neo-ai-dlr-test-artifacts.s3-us-west-2.amazonaws.com/compiled-models/xgboost_test.tar.gz;name=xgboost;subdir=xgboost_test \
 https://neo-ai-dlr-test-artifacts.s3-us-west-2.amazonaws.com/compiled-models/release-1.5.0/ssd_mobilenet_v1_ppn_shared_box_predictor_300x300_coco14_sync_2018_07_03-LINUX_X86_64.tar.gz;name=mobilenet;subdir=ssd_mobilenet_v1 \
           https://neo-ai-dlr-test-artifacts.s3-us-west-2.amazonaws.com/compiled-models/release-1.5.0/automl-ml_m4.tar.gz;name=automl;subdir=automl \
           https://neo-ai-dlr-test-artifacts.s3-us-west-2.amazonaws.com/compiled-models/pipeline_model1-LINUX_X86_64.tar.gz;name=model1;subdir=pipeline_model1 \
           https://neo-ai-dlr-test-artifacts.s3-us-west-2.amazonaws.com/compiled-models/release-1.5.0/pipeline_model2-LINUX_X86_64.tar.gz;name=model2;subdir=pipeline_model2 \
           https://neo-ai-dlr-test-artifacts.s3-us-west-2.amazonaws.com/compiled-models/release-1.5.0/inverselabel-ml_m4.tar.gz;name=inverselabel;subdir=inverselabel \
          "

SRCREV_neo-ai-dlr = "d363c087e2d93938beb3d3a836b0b29d0c910451"

SRC_URI[cat224-3.md5sum] = "c871a4f847b70a6913e6aba47e5a1664"
SRC_URI[cat224-3.sha256sum]    = "2befeb0f99ef581cfed173257a4b9d2b037dc1e3965d4312c93709d542403273"
SRC_URI[streetsmall.md5sum]    = "1e3f1811f75ba14e66e5181f5e04a72b"
SRC_URI[streetsmall.sha256sum] = "567c5de59dc8670b2b821487b53b21b82a666f4b13dbfd181fd9d930df667c7e"
SRC_URI[resnet.md5sum]    = "a81e66fa11f58d57f9d4d081510d0c5a"
SRC_URI[resnet.sha256sum] = "d47c42d925b7685564ee9f7b0feb62e27b3025fb977cfb50755503b94a8dda3a"
SRC_URI[xgboost.md5sum]    = "6986728151570115cd8aa1490355ec37"
SRC_URI[xgboost.sha256sum] = "b8b61cc7805e40cffd124b312b45588c9ef0e0098855260f6c2e7e039c6370ad"
SRC_URI[mobilenet.md5sum]    = "71712d7140cd294d3c7009b50fed6687"
SRC_URI[mobilenet.sha256sum] = "6e82a3bdee2d9026ce97fda2909c8f5a54f426c40f4b3d4a86120de94212893c"
SRC_URI[automl.md5sum]    = "091557786f73802b8b88120415c956bd"
SRC_URI[automl.sha256sum] = "a5466825b260ee38366f423f94718debb16a8751c6bc410a3f4003556ab0268c"
SRC_URI[model1.md5sum]    = "d9dd31f8a25343adeb164797fe399df3"
SRC_URI[model1.sha256sum] = "d6381108bc3505623a5cb66fd0be2209924882d9855c1c73a0c6461e12a61c90"
SRC_URI[model2.md5sum]    = "995b63040c42f8dbc66a5017d92fb808"
SRC_URI[model2.sha256sum] = "054963a67bc456e436d4ad2981552447a6cf8b4955099cce6ed540e73f9b9766"
SRC_URI[inverselabel.md5sum]    = "97e60c6a8040d3b2b15adbc046186b5c"
SRC_URI[inverselabel.sha256sum] = "1bd13905b526fc0e7ead51a88aa6d9e506befdd2e3a7a144b77df8bf3d5d2db7"


S = "${WORKDIR}/git"

do_configure_prepend() {
  cd ${S}
  git submodule update --init --recursive
  # hack to get the python bindings to install proper - distutils3 on dunfell
  # enforces setup.py is in ${S}
  mv ${S}/python/* ${S}/
}

do_configure_append() {
  cp -f ${WORKDIR}/cat224-3.txt ${S}/build/cat224-3.txt
  cp -f ${WORKDIR}/street_small.npy ${S}/build/street_small.npy
  cp -rf ${WORKDIR}/resnet_v1_5_50 ${S}/build/
  cp -rf ${WORKDIR}/xgboost_test ${S}/build/
  cp -rf ${WORKDIR}/ssd_mobilenet_v1 ${S}/build/
  cp -rf ${WORKDIR}/automl ${S}/build/
  cp -rf ${WORKDIR}/pipeline_model1 ${S}/build/
  cp -rf ${WORKDIR}/pipeline_model2 ${S}/build/
  cp -rf ${WORKDIR}/inverselabel ${S}/build/
}

inherit setuptools3 cmake

B = "${S}/build"

do_install() {
    install -d ${D}${includedir}/dlr_tflite
    install -m 0644 ${S}/include/*.h ${D}${includedir}

    # Install DLR Python binding
    # Need to change to physical directory where setup.py lives
    # This is fixed in gatesgarth with:
    #DISTUTILS_SETUP_PATH = "${S}/python"
    #cd ${S}/python
    distutils3_do_install

    # setup.py install some libs under datadir, but we don't need them, so remove.
    rm ${D}${datadir}/dlr/*.so

    # Install DLR library to Python import search path
    install -m 0644 ${S}/build/lib/libdlr.so ${D}${PYTHON_SITEPACKAGES_DIR}/dlr

    # Now install python test scripts
    install -d ${D}${datadir}/dlr/tests/python/integration
    install -m 0644 ${S}/tests/python/integration/*.py ${D}${datadir}/dlr/tests/python/integration
    install -m 0644 ${S}/tests/python/integration/*.npy ${D}${datadir}/dlr/tests/python/integration
}

PACKAGES =+ "${PN}-tests"
FILES_${PN}-tests = "${datadir}/dlr/tests"
RDEPENDS_${PN}-tests += "${PN}"
DEPENDS += "googletest python3-setuptools python3-distro"

# Versioned libs are not produced
FILES_SOLIBSDEV = ""
