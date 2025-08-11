SUMMARY = "AWS IoT Greengrass Hello World Bash Component"
DESCRIPTION = "A simple hello world bash component for AWS IoT Greengrass v2 - supports both classic and lite variants"
HOMEPAGE = "https://github.com/aws4embeddedlinux/meta-aws"
LICENSE = "Apache-2.0"
# nooelint: oelint.var.licenseremotefile
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

# COMPONENT_NAME and COMPONENT_VERSION will be read from component-recipe.yaml
# They can be overwritten here if needed:
# COMPONENT_NAME = "com.example.BashHelloWorld"
# COMPONENT_VERSION = "1.0.0"

# No separate artifacts needed - script is embedded in component-recipe.yaml
COMPONENT_ARTIFACTS = ""

# Default to lite variant, can be overwritten in local.conf e.g.
GREENGRASS_VARIANT ?= "lite"

# Conditionally inherit the appropriate class based on variant
# greengrass-component.bbclass = Classic Greengrass only
# greengrass-lite-component.bbclass = Greengrass Lite only
inherit_defer ${@'greengrass-lite-component' if d.getVar('GREENGRASS_VARIANT') == 'lite' else 'greengrass-component'}
inherit ptest

SRC_URI = " \
    file://component-recipe.yaml \
    file://run-ptest \
    "

S = "${WORKDIR}"

FILES:${PN}-ptest += "${PTEST_PATH}/*"

do_install_ptest() {
    # Install test runner script
    install -m 0755 ${WORKDIR}/run-ptest ${D}${PTEST_PATH}/

    # Create test data directory
    install -d ${D}${PTEST_PATH}/test-data

    # Copy the component's recipe for testing
    install -m 0644 ${WORKDIR}/component-recipe.yaml ${D}${PTEST_PATH}/test-data/
}

# Add bash dependency since this is a bash component and ptest needs it
RDEPENDS:${PN} += "bash"
RDEPENDS:${PN}-ptest = "${PN} bash grep"
