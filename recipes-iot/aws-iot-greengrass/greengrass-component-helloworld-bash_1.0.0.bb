SUMMARY = "AWS IoT Greengrass Hello World bash Component"
DESCRIPTION = "A simple hello world bash component for AWS IoT Greengrass v2"
HOMEPAGE = "https://github.com/aws4embeddedlinux/meta-aws"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

COMPONENT_NAME = "com.example.BashHelloWorld"
COMPONENT_VERSION = "1.0.0"

# Configure for Greengrass Classic (Bin)
GREENGRASS_VARIANT = "classic"

inherit greengrass-component

SRC_URI = " \
    file://component-recipe.yaml \
    file://run-ptest \
    "

# Install function that handles both greengrass component setup and package files
do_install() {
    # Handle greengrass component installation based on variant
    if [ "${GREENGRASS_VARIANT}" = "lite" ]; then
        # Create config directory for lite
        install -d ${D}/etc/greengrass/config.d
        
        # Install the config template directly since it's already in lite format
        if [ -f ${UNPACKDIR}/config.yaml.template ]; then
            install -m 0644 ${UNPACKDIR}/config.yaml.template ${D}/etc/greengrass/config.d/${COMPONENT_NAME}.yaml
            bbnote "Installed direct config fragment for ${COMPONENT_NAME}"
        fi
    fi
    
    # Create a minimal marker file so the package is not empty
    install -d ${D}${datadir}/${PN}
    echo "Greengrass Bash HelloWorld Component" > ${D}${datadir}/${PN}/README
}

FILES:${PN} = " \
    ${datadir}/${PN}/* \
    /etc/greengrass/config.d/* \
"

FILES:${PN}-ptest = " \
    ${PTEST_PATH}/* \
"

do_install_ptest() {
    # Install test runner script
    install -m 0755 ${S}/run-ptest ${D}${PTEST_PATH}/
    
    # Create test data directory
    install -d ${D}${PTEST_PATH}/test-data
    
    # Copy the component's config fragment for testing
    install -m 0644 ${S}/config.yaml.template ${D}${PTEST_PATH}/test-data/
}

RDEPENDS:${PN} = "greengrass-lite"
RDEPENDS:${PN}-ptest = "${PN} bash grep"
