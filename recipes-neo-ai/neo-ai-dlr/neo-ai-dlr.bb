SUMMARY = "NEO-AI Deep Learning Runtime"
DESCRIPTION = "Neo-AI-DLR is a common runtime for machine learning models compiled by AWS SageMaker Neo, TVM, or TreeLite."
HOMEPAGE = "https://aws.amazon.com/sagemaker/neo/"
LICENSE = "Apache-2.0 & BSD-3-Clause"

LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658 \
                    file://3rdparty/tvm/LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e \
                    file://3rdparty/tvm/3rdparty/dmlc-core/LICENSE;md5=0ca7d6e8f4af26868cb42025ad83374b \
                    file://3rdparty/tvm/3rdparty/dlpack/LICENSE;md5=f62d4e85ba68a1574b74d97ab8dea9ab \
                    file://3rdparty/tvm/3rdparty/HAlideIR/LICENSE;md5=9910386e68f0616e1ecf1037479fa97e \
                    file://3rdparty/treelite/LICENSE;md5=34400b68072d710fecd0a2940a0d1658 \
                    file://3rdparty/treelite/dmlc-core/LICENSE;md5=0ca7d6e8f4af26868cb42025ad83374b \
                    file://3rdparty/treelite/3rdparty/fmt/LICENSE.rst;md5=c2e38bc8629eac247a73b65c1548b2f0 \
"

PV = "1.1"

BRANCH ?= "master"

# Main DLR sources plus submodules (and submodules of submodules).
SRC_URI = "git://github.com/neo-ai/neo-ai-dlr;protocol=https;branch=${BRANCH};name=neo-ai-dlr \
           git://github.com/neo-ai/tvm;protocol=https;branch=stable;destsuffix=${S}/3rdparty/tvm;name=neo-ai-tvm \
           git://github.com/dmlc/dmlc-core;protocol=https;branch=master;destsuffix=${S}/3rdparty/tvm/3rdparty/dmlc-core;name=neo-ai-tvm-dmlc-core \
           git://github.com/dmlc/dlpack;protocol=https;branch=master;destsuffix=${S}/3rdparty/tvm/3rdparty/dlpack;name=neo-ai-tvm-dlpack \
           git://github.com/dmlc/HalideIR;protocol=https;branch=master;destsuffix=${S}/3rdparty/tvm/3rdparty/HAlideIR;name=neo-ai-tvm-halideir \
           git://github.com/neo-ai/treelite;protocol=https;branch=master;destsuffix=${S}/3rdparty/treelite;name=neo-ai-treelite \
           git://github.com/dmlc/dmlc-core;protocol=https;branch=master;destsuffix=${S}/3rdparty/treelite/dmlc-core;name=neo-ai-treelite-dmlc-core \
           git://github.com/fmtlib/fmt;protocol=https;nobranch=1;destsuffix=${S}/3rdparty/treelite/3rdparty/fmt;name=neo-ai-treelite-fmt \
           file://0001-CMakeLists-skip-cloning-of-googletests.patch \
"

SRCREV_neo-ai-dlr = "35ed4fa2607056608451d85508fea70f458a14a6"
SRCREV_neo-ai-tvm = "44779571412930681eef9e8b9d32aa845b8cc5ad"
SRCREV_neo-ai-tvm-dmlc-core = "4d49691f1a9d944c3b0aa5e63f1db3cad1f941f8"
SRCREV_neo-ai-tvm-dlpack = "bee4d1dd8dc1ee4a1fd8fa6a96476c2f8b7492a3"
SRCREV_neo-ai-tvm-halideir = "e4a4c02764d37c9c3db0d64c4996651a3ef9513c"
SRCREV_neo-ai-treelite = "0972ce97687b4a1fb1262fad56232e7cc61116eb"
SRCREV_neo-ai-treelite-dmlc-core = "4d49691f1a9d944c3b0aa5e63f1db3cad1f941f8"
SRCREV_neo-ai-treelite-fmt = "135ab5cf71ed731fc9fa0653051e7d4884a3652f"

S = "${WORKDIR}/git"

inherit setuptools3 cmake python3native

# Set B so that DLR Python installation can find the library
B = "${S}/build"

do_install() {
    # This does not do anything
    #cmake_do_install

    install -d ${D}${includedir}/dlr_tflite
    install -m 0644 ${S}/include/*.h ${D}${includedir}
    install -m 0644 ${S}/include/dlr_tflite/*.h ${D}${includedir}/dlr_tflite

    # Install DLR Python binding
    cd ${S}/python
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

# Versioned libs are not produced
FILES_SOLIBSDEV = ""
