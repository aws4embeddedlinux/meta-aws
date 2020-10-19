SUMMARY = "NEO-AI Deep Learning Runtime"
DESCRIPTION = "Neo-AI-DLR is a common runtime for machine learning models compiled by AWS SageMaker Neo, TVM, or TreeLite."
HOMEPAGE = "https://aws.amazon.com/sagemaker/neo/"
LICENSE = "Apache-2.0 & BSD-3-Clause"

LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658 \
                    file://3rdparty/tvm/LICENSE;md5=6624767da5fc0a207875418ee416a320 \
                    file://3rdparty/tvm/3rdparty/dmlc-core/LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e \
                    file://3rdparty/tvm/3rdparty/dlpack/LICENSE;md5=f62d4e85ba68a1574b74d97ab8dea9ab \
                    file://3rdparty/treelite/LICENSE;md5=34400b68072d710fecd0a2940a0d1658 \
                    file://3rdparty/treelite/dmlc-core/LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e \
                    file://3rdparty/treelite/3rdparty/fmt/LICENSE.rst;md5=c2e38bc8629eac247a73b65c1548b2f0 \
"

PV = "1.4"

BRANCH ?= "master"

# Main DLR sources plus submodules (and submodules of submodules).
SRC_URI = "git://github.com/neo-ai/neo-ai-dlr;protocol=https;branch=${BRANCH};name=neo-ai-dlr \
           git://github.com/neo-ai/tvm;protocol=https;branch=release-1.4.0;destsuffix=${S}/3rdparty/tvm;name=neo-ai-tvm \
           git://github.com/dmlc/dmlc-core;protocol=https;branch=master;destsuffix=${S}/3rdparty/tvm/3rdparty/dmlc-core;name=neo-ai-tvm-dmlc-core \
           git://github.com/dmlc/dlpack;protocol=https;branch=master;destsuffix=${S}/3rdparty/tvm/3rdparty/dlpack;name=neo-ai-tvm-dlpack \
           git://github.com/neo-ai/treelite;protocol=https;branch=release-1.3.0;destsuffix=${S}/3rdparty/treelite;name=neo-ai-treelite \
           git://github.com/dmlc/dmlc-core;protocol=https;branch=master;destsuffix=${S}/3rdparty/treelite/dmlc-core;name=neo-ai-treelite-dmlc-core \
           git://github.com/fmtlib/fmt;protocol=https;nobranch=1;destsuffix=${S}/3rdparty/treelite/3rdparty/fmt;name=neo-ai-treelite-fmt \
           file://0001-maybe-upstream-add-condition-for-googletest-build.patch \
"

SRCREV_neo-ai-dlr = "7c5658d4e820428c49faa43e4d0e0365a3b60719"
SRCREV_neo-ai-tvm = "cab407ae6b686afbc602b254c5371f71b92f3bef"
SRCREV_neo-ai-tvm-dmlc-core = "6c401e242c59a1f4c913918246591bb13fd714e7"
SRCREV_neo-ai-tvm-dlpack = "3ec04430e89a6834e5a1b99471f415fa939bf642"
SRCREV_neo-ai-treelite = "38964865d624048d1a432af2763ee1dcdbe3ce5d"
SRCREV_neo-ai-treelite-dmlc-core = "6c401e242c59a1f4c913918246591bb13fd714e7"
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

EXTRA_OECMAKE = "-DUSE_TESTS=OFF "

PACKAGES =+ "${PN}-tests"
FILES_${PN}-tests = "${datadir}/dlr/tests"
RDEPENDS_${PN}-tests += "${PN}"

# Versioned libs are not produced
FILES_SOLIBSDEV = ""
