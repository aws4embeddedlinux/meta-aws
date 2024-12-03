SUMMARY = "YAML parser/emitter"
DESCRIPTION = "YAML parser/emitter that supports roundtrip preservation of comments, seq/map flow style, and map key order."
HOMEPAGE = "https://pypi.org/project/ruamel.yaml.clib/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3628bf49cf3a3776a82e22f36f8857da"

PYPI_PACKAGE = "ruamel.yaml.clib"

inherit pypi setuptools3 native

SRC_URI[sha256sum] = "6c8fbb13ec503f99a91901ab46e0b07ae7941cd527393187039aec586fdfd36f"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-shell \
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-netclient \
"

do_install:prepend() {
    export RUAMEL_NO_PIP_INSTALL_CHECK=1
}

BBCLASSEXTEND = "native"
