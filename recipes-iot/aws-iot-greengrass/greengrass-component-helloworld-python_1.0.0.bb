SUMMARY = "AWS IoT Greengrass Hello World Python Component"
DESCRIPTION = "A simple hello world Python component for AWS IoT Greengrass v2 with MQTT publishing"
HOMEPAGE = "https://github.com/aws4embeddedlinux/meta-aws"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

COMPONENT_NAME = "com.example.HelloWorldPython"
COMPONENT_VERSION = "1.0.0"
COMPONENT_ARTIFACTS = "hello_world.py"

# Configure for Greengrass Lite with image-provided components
GREENGRASS_VARIANT = "lite"

RDEPENDS:${PN} += "aws-iot-device-sdk-python-v2"

inherit greengrass_lite_component ptest

SRC_URI = " \
    file://hello_world.py \
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
    
    # Store expected paths for testing
    echo "${COMPONENT_NAME}" > ${D}${PTEST_PATH}/test-data/component-name
    echo "${COMPONENT_VERSION}" > ${D}${PTEST_PATH}/test-data/component-version
    echo "hello_world.py" > ${D}${PTEST_PATH}/test-data/expected-artifact
}

RDEPENDS:${PN}-ptest = "${PN} bash grep python3"
