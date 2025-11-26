SUMMARY = "AWS Greengrass Hello World Rust Component"
DESCRIPTION = "A simple example component using AWS Greengrass Component SDK in Rust"
HOMEPAGE = "https://github.com/aws/meta-aws"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

# COMPONENT_NAME and COMPONENT_VERSION will be read from component-recipe.yaml
# They can be overridden here if needed:
# COMPONENT_NAME = "com.example.HelloWorldRust"
# COMPONENT_VERSION = "1.0.0"

# Default to lite variant, can be overwritten (classic)
GREENGRASS_VARIANT ?= "lite"

DEPENDS = "aws-greengrass-component-sdk"

SRC_URI = " \
    file://main.rs \
    file://Cargo.toml \
    file://Cargo.lock \
    file://component-recipe.yaml \
    file://run-ptest \
"
S = "${UNPACKDIR}"

inherit cargo cargo-update-recipe-crates ptest
inherit_defer ${@'greengrass-lite-component' if d.getVar('GREENGRASS_VARIANT') == 'lite' else 'greengrass-component'}

require ${BPN}-crates.inc

EXTRA_OECARGO_PATHS = "${RECIPE_SYSROOT}${datadir}/cargo/registry/gg-sdk"

do_configure:prepend() {
    # Copy gg-sdk and C sources to match build.rs expectations
    cp -r ${RECIPE_SYSROOT}${datadir}/cargo/registry/gg-sdk ${S}/
    cp -r ${RECIPE_SYSROOT}${datadir}/cargo/registry/src ${S}/gg-sdk/
    cp -r ${RECIPE_SYSROOT}${datadir}/cargo/registry/include ${S}/gg-sdk/
    cp -r ${RECIPE_SYSROOT}${datadir}/cargo/registry/priv_include ${S}/gg-sdk/
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${B}/target/${CARGO_TARGET_SUBDIR}/hello-world-rust ${D}${bindir}/

    install -d ${D}${datadir}/greengrass/components/com.example.HelloWorldRust/1.0.0
    install -m 0644 ${S}/component-recipe.yaml ${D}${datadir}/greengrass/components/com.example.HelloWorldRust/1.0.0/
}

do_install:append() {
    if [ "${GREENGRASS_VARIANT}" = "lite" ]; then
        install -m 0755 ${B}/target/${CARGO_TARGET_SUBDIR}/hello-world-rust ${D}${GGL_ARTIFACTS_DIR}/${COMPONENT_NAME}/${COMPONENT_VERSION}/
    else
        install -d ${D}/${GG_BASENAME}/components/${COMPONENT_NAME}/${COMPONENT_VERSION}
        install -m 0755 ${B}/target/${CARGO_TARGET_SUBDIR}/hello-world-rust ${D}/${GG_BASENAME}/components/${COMPONENT_NAME}/${COMPONENT_VERSION}/
    fi
}

RDEPENDS:${PN}-ptest += "grep"

do_install_ptest() {
    install -m 0755 ${S}/run-ptest ${D}${PTEST_PATH}/
    install -d ${D}${PTEST_PATH}/test-data
    install -m 0644 ${S}/component-recipe.yaml ${D}${PTEST_PATH}/test-data/
    echo "${GREENGRASS_VARIANT}" > ${D}${PTEST_PATH}/test-data/greengrass-variant
    echo "com.example.HelloWorldRust" > ${D}${PTEST_PATH}/test-data/component-name
    echo "1.0.0" > ${D}${PTEST_PATH}/test-data/component-version
    echo "hello-world-rust" > ${D}${PTEST_PATH}/test-data/expected-artifact
}

FILES:${PN} = "${bindir}/* ${datadir}/greengrass/*"
