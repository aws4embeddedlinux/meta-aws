SUMMARY = "Example AWS Greengrass component using SDK Lite"
DESCRIPTION = "A simple example component that demonstrates using the AWS Greengrass SDK Lite \
to interact with the Greengrass Nucleus via IPC calls."
HOMEPAGE = "https://github.com/aws4embeddedlinux/meta-aws"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

DEPENDS = "aws-greengrass-sdk-lite"

SRC_URI = " \
    file://main.c \
    file://CMakeLists.txt \
    file://component-recipe.yaml \
    file://run-ptest \
"

inherit cmake greengrass-component ptest

# Component configuration
GREENGRASS_COMPONENT_NAME = "com.example.HelloWorldSDKLite"
GREENGRASS_COMPONENT_VERSION = "1.0.0"
GREENGRASS_COMPONENT_DESCRIPTION = "Example Hello World component using AWS Greengrass SDK Lite"
GREENGRASS_COMPONENT_AUTHOR = "Example Author"

# Use the standard component name variable
COMPONENT_NAME = "${GREENGRASS_COMPONENT_NAME}"
COMPONENT_VERSION = "${GREENGRASS_COMPONENT_VERSION}"
COMPONENT_ARTIFACTS = "hello-world-sdk-lite"

# Build configuration
EXTRA_OECMAKE = " \
    -DCMAKE_BUILD_TYPE=Release \
"

do_compile:append() {
    # Copy the built binary to UNPACKDIR so greengrass-component class can find it
    cp ${B}/hello-world-sdk-lite ${UNPACKDIR}/
}

do_install_ptest() {
    # Install test runner script
    install -m 0755 ${S}/run-ptest ${D}${PTEST_PATH}/
}

FILES:${PN} = " \
    /${GG_BASENAME}/components/${COMPONENT_NAME}/* \
"

FILES:${PN}-ptest = " \
    ${PTEST_PATH}/* \
"

RDEPENDS:${PN} = "greengrass-bin"
RDEPENDS:${PN}-ptest = "${PN} ldd file bash"

# Disable buildpaths QA check for debug symbols
INSANE_SKIP:${PN}-dbg += "buildpaths"
