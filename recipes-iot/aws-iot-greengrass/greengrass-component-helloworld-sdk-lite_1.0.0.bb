SUMMARY = "AWS Greengrass Hello World SDK Lite Component"
DESCRIPTION = "A simple example component using AWS Greengrass SDK Lite - supports both classic and lite variants"
HOMEPAGE = "https://github.com/aws4embeddedlinux/meta-aws"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

# COMPONENT_NAME and COMPONENT_VERSION will be read from component-recipe.yaml
# They can be overridden here if needed:
# COMPONENT_NAME = "com.example.HelloWorldSDKLite"
# COMPONENT_VERSION = "1.0.0"

COMPONENT_ARTIFACTS = "hello-world-sdk-lite"

# Default to lite variant, can be overridden
GREENGRASS_VARIANT ?= "lite"

# Always need SDK Lite for this component
DEPENDS = "aws-greengrass-sdk-lite"

# Skip QA check for buildpaths in debug symbols
INSANE_SKIP:${PN}-dbg += "buildpaths"

# Package configuration options
# Default: shared library linking
# Add 'static' to PACKAGECONFIG to use static linking instead
PACKAGECONFIG ??= ""
PACKAGECONFIG[static] = "-DUSE_STATIC_LIBS=ON,-DUSE_STATIC_LIBS=OFF"

# Conditionally inherit the appropriate class based on variant
# greengrass-component.bbclass = Classic Greengrass only
# greengrass-lite-component.bbclass = Greengrass Lite only
inherit cmake ${@'greengrass-lite-component' if d.getVar('GREENGRASS_VARIANT') == 'lite' else 'greengrass-component'} ptest

SRC_URI = " \
    file://main.c \
    file://CMakeLists.txt \
    file://component-recipe.yaml \
    file://run-ptest \
"

# Conditional FILES based on variant
# Lite uses GGL_IMAGE_COMPONENTS_ROOT, Classic uses standard paths
FILES:${PN} += "${@'${GGL_IMAGE_COMPONENTS_ROOT}/' if d.getVar('GREENGRASS_VARIANT') == 'lite' else ''}"

FILES:${PN}-ptest = " \
    ${PTEST_PATH}/* \
"

# Custom do_install for SDK Lite component to handle built binary
do_install() {
    if [ "${GREENGRASS_VARIANT}" = "lite" ]; then
        # For lite variant, create the lite component structure
        install -d ${D}${GGL_IMAGE_COMPONENTS_ROOT}/${COMPONENT_NAME}/${COMPONENT_VERSION}
        
        # Install the built binary
        if [ -f ${B}/hello-world-sdk-lite ]; then
            install -m 0755 ${B}/hello-world-sdk-lite ${D}${GGL_IMAGE_COMPONENTS_ROOT}/${COMPONENT_NAME}/${COMPONENT_VERSION}/
        fi
        
        # Install component recipe
        if [ -f ${UNPACKDIR}/component-recipe.yaml ]; then
            install -m 0644 ${UNPACKDIR}/component-recipe.yaml ${D}${GGL_IMAGE_COMPONENTS_ROOT}/${COMPONENT_NAME}/${COMPONENT_VERSION}/
        fi
    else
        # For classic variant, create the classic component structure
        install -d ${D}${GG_COMPONENT_ROOT}/${COMPONENT_NAME}/${COMPONENT_VERSION}
        
        # Install the built binary
        if [ -f ${B}/hello-world-sdk-lite ]; then
            install -m 0755 ${B}/hello-world-sdk-lite ${D}${GG_COMPONENT_ROOT}/${COMPONENT_NAME}/${COMPONENT_VERSION}/
        else
            bbwarn "Built binary hello-world-sdk-lite not found in ${B}"
        fi
        
        # Install component recipe
        if [ -f ${UNPACKDIR}/component-recipe.yaml ]; then
            install -m 0644 ${UNPACKDIR}/component-recipe.yaml ${D}${GG_COMPONENT_ROOT}/${COMPONENT_NAME}/${COMPONENT_VERSION}/
        fi
    fi
}

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
    echo "hello-world-sdk-lite" > ${D}${PTEST_PATH}/test-data/expected-artifact
}

# Conditional runtime dependencies based on variant
# Lite depends on greengrass-lite, Classic depends on greengrass-bin
RDEPENDS:${PN} += "${@'greengrass-lite' if d.getVar('GREENGRASS_VARIANT') == 'lite' else 'greengrass-bin'}"

# Add runtime dependency on shared library package when using shared linking (default)
# Only skip this dependency when explicitly using static linking
RDEPENDS:${PN} += "${@'' if bb.utils.contains('PACKAGECONFIG', 'static', True, False, d) else 'aws-greengrass-sdk-lite'}"

RDEPENDS:${PN}-ptest = "${PN} bash grep"
