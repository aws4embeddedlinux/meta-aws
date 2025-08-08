SUMMARY = "AWS IoT Greengrass Hello World Python Component"
DESCRIPTION = "A simple hello world Python component for AWS IoT Greengrass v2 - supports both classic and lite variants"
HOMEPAGE = "https://github.com/aws4embeddedlinux/meta-aws"
LICENSE = "Apache-2.0"
# nooelint: oelint.var.licenseremotefile
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

# Default to classic variant, can be overwritten
GREENGRASS_VARIANT ?= "classic"

SRC_URI = " \
    file://hello_world.py \
    file://component-recipe.yaml \
    file://run-ptest \
    "

S = "${UNPACKDIR}"

FILES:${PN}-ptest += "${PTEST_PATH}/*"

# Conditionally inherit the appropriate class based on variant
# greengrass-component.bbclass = Classic Greengrass only
# greengrass-lite-component.bbclass = Greengrass Lite only
inherit_defer ${@'greengrass-lite-component' if d.getVar('GREENGRASS_VARIANT') == 'lite' else 'greengrass-component'}
inherit ptest

# Always need Python SDK for this component
RDEPENDS:${PN} += "aws-iot-device-sdk-python-v2"

do_install() {
    install -m 0755 ${UNPACKDIR}/hello_world.py ${D}${GGL_ARTIFACTS_DIR}/${COMPONENT_NAME}/${COMPONENT_VERSION}/
}

do_install_ptest() {
    # Install test runner script
    install -m 0755 ${UNPACKDIR}/run-ptest ${D}${PTEST_PATH}/

    # Create test data directory
    install -d ${D}${PTEST_PATH}/test-data

    # Copy the component's recipe for testing
    install -m 0644 ${UNPACKDIR}/component-recipe.yaml ${D}${PTEST_PATH}/test-data/

    # Store expected paths for testing
    echo "${COMPONENT_NAME}" > ${D}${PTEST_PATH}/test-data/component-name
    echo "${COMPONENT_VERSION}" > ${D}${PTEST_PATH}/test-data/component-version
    echo "hello_world.py" > ${D}${PTEST_PATH}/test-data/expected-artifact
}

RDEPENDS:${PN}-ptest = "${PN} bash grep python3"
