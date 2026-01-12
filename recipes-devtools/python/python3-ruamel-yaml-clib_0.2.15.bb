SUMMARY = "YAML parser/emitter"
DESCRIPTION = "YAML parser/emitter that supports roundtrip preservation of comments, seq/map flow style, and map key order."
HOMEPAGE = "https://pypi.org/project/ruamel.yaml.clib/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=40323d21fb440c36bfd8e28c280977de"

SRC_URI = "https://files.pythonhosted.org/packages/ea/97/60fda20e2fb54b83a61ae14648b0817c8f5d84a3821e40bfbdae1437026a/ruamel_yaml_clib-0.2.15.tar.gz"
SRC_URI[sha256sum] = "46e4cc8c43ef6a94885f72512094e482114a8a706d3c555a34ed4b0d20200600"

S = "${UNPACKDIR}/ruamel.yaml.clib-${PV}"

inherit setuptools3 ptest

SRC_URI += "file://run-ptest"

# Build dependencies for C extension
DEPENDS += "python3-setuptools-native"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-shell \
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-ruamel-yaml \
"

RDEPENDS:${PN}-ptest += "\
    ${PYTHON_PN}-unittest \
"

FILES:${PN} += "${PYTHON_SITEPACKAGES_DIR}/ruamel/yaml/clib/ ${PYTHON_SITEPACKAGES_DIR}/_ruamel_yaml*.so"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}
}

do_install:prepend() {
    export RUAMEL_NO_PIP_INSTALL_CHECK=1
    # Ensure C extension is built
    export RUAMEL_YAML_CLIB_BUILD_EXTENSION=1
}

do_install:append() {
    # Install the ruamel.yaml.clib namespace package structure
    install -d ${D}${PYTHON_SITEPACKAGES_DIR}/ruamel/yaml/clib
    cp -r ${S}/build/lib.linux-*/ruamel/yaml/clib/* ${D}${PYTHON_SITEPACKAGES_DIR}/ruamel/yaml/clib/
}

BBCLASSEXTEND = "native"
