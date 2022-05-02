SUMMARY = "NEO-AI Deep Learning Runtime"
DESCRIPTION = "Neo-AI-DLR is a common runtime for machine learning models compiled by AWS SageMaker Neo, TVM, or TreeLite."
HOMEPAGE = "https://aws.amazon.com/sagemaker/neo/"
LICENSE = "Apache-2.0 & BSD-3-Clause"

LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

BRANCH ?= "release-1.10.0"

SRC_URI = "git://github.com/neo-ai/neo-ai-dlr.git;protocol=https;branch=${BRANCH};name=neo-ai-dlr \
           file://0002-CMakeLists_remove_test_file_downloads.patch \
           https://neo-ai-dlr-test-artifacts.s3-us-west-2.amazonaws.com/tflite-models/cat224-3.txt;name=cat224-3 \
           https://neo-ai-dlr-test-artifacts.s3-us-west-2.amazonaws.com/test-data/street_small.npy;name=streetsmall \
           https://neo-ai-dlr-test-artifacts.s3-us-west-2.amazonaws.com/compiled-models/resnet_v1.5_50-ml_c4.tar.gz;name=resnet;subdir=resnet_v1_5_50 \
           https://neo-ai-dlr-test-artifacts.s3-us-west-2.amazonaws.com/compiled-models/xgboost_test.tar.gz;name=xgboost;subdir=xgboost_test \
           https://neo-ai-dlr-test-artifacts.s3-us-west-2.amazonaws.com/compiled-models/release-1.5.0/ssd_mobilenet_v1_ppn_shared_box_predictor_300x300_coco14_sync_2018_07_03-LINUX_X86_64.tar.gz;name=mobilenet;subdir=ssd_mobilenet_v1 \
           https://neo-ai-dlr-test-artifacts.s3-us-west-2.amazonaws.com/compiled-models/release-1.5.0/automl-ml_m4.tar.gz;name=automl;subdir=automl \
           https://neo-ai-dlr-test-artifacts.s3-us-west-2.amazonaws.com/compiled-models/pipeline_model1-LINUX_X86_64.tar.gz;name=model1;subdir=pipeline_model1 \
           https://neo-ai-dlr-test-artifacts.s3-us-west-2.amazonaws.com/compiled-models/release-1.5.0/pipeline_model2-LINUX_X86_64.tar.gz;name=model2;subdir=pipeline_model2 \
           https://neo-ai-dlr-test-artifacts.s3-us-west-2.amazonaws.com/compiled-models/release-1.5.0/inverselabel-ml_m4.tar.gz;name=inverselabel;subdir=inverselabel \
           https://neo-ai-dlr-test-artifacts.s3-us-west-2.amazonaws.com/compiled-models/release-1.9.0/automl_static-ml_m4.tar.gz;name=automl-static;subdir=automl_static \
           https://neo-ai-dlr-test-artifacts.s3-us-west-2.amazonaws.com/compiled-models/release-1.9.0/input_order-ml_m4.tar.gz;name=input-order;subdir=input_order \
           https://neo-ai-dlr-test-artifacts.s3-us-west-2.amazonaws.com/compiled-models/release-1.9.0/inverselabel_static-ml_m4.tar.gz;name=inverselabel-static;subdir=inverselabel_static \           
          "
SRCREV_FORMAT = "neo-ai-dlr"
SRCREV_neo-ai-dlr = "87125a9ce39c83a002f1908d5b3beed3b3a7d2c0"

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
SRC_URI[automl-static.md5sum] = "aa72b0c4e1b32e0dc983a5f86ee289d4"
SRC_URI[automl-static.sha256sum] = "afe8b20ffce1dc8d3eb59cbf13aa72d96e0e2258be7b789f0909f91e1f67580c"
SRC_URI[input-order.md5sum] = "68fa420d8f293ec42e41b7119a42f09d"
SRC_URI[input-order.sha256sum] = "a34e6c576e5c29c8b7a9cc4c47b836e8375f198a6131502b98f26607228494b8"
SRC_URI[inverselabel-static.md5sum] = "191a5ea03439040c789fe5fb0cf8eb10"
SRC_URI[inverselabel-static.sha256sum] = "aebd1e2457bc6ecb7af783ee37c7531289c2ec8761199520cf41b7ec1b3f31fe"

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

do_configure:prepend() {
  cd ${S}
  git submodule update --init --recursive
  cd ${B}
}

do_configure[network] = "true"

do_configure:append() {
  cp -f ${WORKDIR}/cat224-3.txt ${B}/cat224-3.txt
  cp -f ${WORKDIR}/street_small.npy ${B}/street_small.npy
  cp -rf ${WORKDIR}/resnet_v1_5_50 ${B}/
  cp -rf ${WORKDIR}/xgboost_test ${B}/
  cp -rf ${WORKDIR}/ssd_mobilenet_v1 ${B}/
  cp -rf ${WORKDIR}/automl ${B}/
  cp -rf ${WORKDIR}/pipeline_model1 ${B}/
  cp -rf ${WORKDIR}/pipeline_model2 ${B}/
  cp -rf ${WORKDIR}/inverselabel ${B}/
  cp -rf ${WORKDIR}/automl_static ${B}/
  cp -rf ${WORKDIR}/input_order ${B}/
  cp -rf ${WORKDIR}/inverselabel_static ${B}/
}

inherit setuptools3 cmake

OECMAKE_GENERATOR = "Unix Makefiles"

OECMAKE_BUILDPATH = "${B}"
OECMAKE_SOURCEPATH = "${S}"
SETUPTOOLS_SETUP_PATH = "${S}/python"

do_install() {
    install -d ${D}${includedir}/dlr_tflite
    install -m 0644 ${S}/include/*.h ${D}${includedir}

    # Install DLR Python binding
    setuptools3_do_install

    # setup.py install some libs under datadir, but we don't need them, so remove.
   # rm ${D}${datadir}/dlr/*.so

    # Install DLR library to Python import search path
    install -m 0644 ${B}/lib/libdlr.so ${D}${PYTHON_SITEPACKAGES_DIR}/dlr

    # Now install python test scripts
    install -d ${D}${datadir}/dlr/tests/python/integration
    install -m 0644 ${S}/tests/python/integration/*.py ${D}${datadir}/dlr/tests/python/integration
    install -m 0644 ${S}/tests/python/integration/*.npy ${D}${datadir}/dlr/tests/python/integration
}

PACKAGES =+ "${PN}-tests"
FILES:${PN}-tests = "${datadir}/dlr/tests"
RDEPENDS:${PN}-tests += "${PN}"
DEPENDS += "googletest python3-setuptools"
RDEPENDS:${PN} += "python3-core python3-shell python3-requests python3-distro python3-numpy"

# Versioned libs are not produced
FILES_SOLIBSDEV = ""
