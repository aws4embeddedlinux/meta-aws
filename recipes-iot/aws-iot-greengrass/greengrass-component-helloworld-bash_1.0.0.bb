SUMMARY = "AWS IoT Greengrass Hello World bash Component"
DESCRIPTION = "A simple hello world bash component for AWS IoT Greengrass v2"
HOMEPAGE = "https://github.com/aws4embeddedlinux/meta-aws"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

COMPONENT_NAME = "com.example.BashHelloWorld"
COMPONENT_VERSION = "1.0.0"
# No separate artifacts needed - script is embedded in component-recipe.yaml
COMPONENT_ARTIFACTS = ""

# Configure for Greengrass Lite with image-provided components
GREENGRASS_VARIANT = "lite"

inherit greengrass_lite_component ptest

SRC_URI = " \
    file://component-recipe.yaml \
    file://run-ptest \
    "

# Include image-provided component files when using lite variant
FILES:${PN} += "${@bb.utils.contains('GREENGRASS_VARIANT', 'lite', '${GGL_IMAGE_COMPONENTS_ROOT}/', '', d)}"

FILES:${PN}-ptest = " \
    ${PTEST_PATH}/* \
"

do_install_ptest() {
    # Install test runner script
    install -m 0755 ${S}/run-ptest ${D}${PTEST_PATH}/

    # Create test data directory
    install -d ${D}${PTEST_PATH}/test-data

    # Copy the component's recipe for testing
    install -m 0644 ${S}/component-recipe.yaml ${D}${PTEST_PATH}/test-data/
}

RDEPENDS:${PN} = "greengrass-lite"
RDEPENDS:${PN}-ptest = "${PN} bash grep"
