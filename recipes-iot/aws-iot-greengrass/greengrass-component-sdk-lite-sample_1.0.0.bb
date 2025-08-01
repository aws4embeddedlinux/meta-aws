SUMMARY = "Example AWS Greengrass component using SDK Lite"
DESCRIPTION = "A simple example component that demonstrates using the AWS Greengrass SDK Lite \
to interact with the Greengrass Nucleus via IPC calls."
HOMEPAGE = "https://github.com/aws4embeddedlinux/meta-aws"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

# Depend on the AWS Greengrass SDK Lite development package
DEPENDS = "aws-greengrass-sdk-lite"

# Source files are provided inline
SRC_URI = " \
    file://main.c \
    file://CMakeLists.txt \
    file://component-config.yaml.template \
    file://run-ptest \
    file://test-component.c \
"

inherit cmake greengrass-component ptest

# Component configuration
GREENGRASS_COMPONENT_NAME = "com.example.HelloWorld"
GREENGRASS_COMPONENT_VERSION = "1.0.0"
GREENGRASS_COMPONENT_DESCRIPTION = "Example Hello World component using AWS Greengrass SDK Lite"
GREENGRASS_COMPONENT_AUTHOR = "Example Author"

# Build configuration
EXTRA_OECMAKE = " \
    -DCMAKE_BUILD_TYPE=Release \
"

do_install() {
    # Install the component binary
    install -d ${D}${bindir}
    install -m 0755 ${B}/hello-world-component ${D}${bindir}/

    # Install component configuration
    install -d ${D}${datadir}/greengrass/components/${GREENGRASS_COMPONENT_NAME}
    install -m 0644 ${WORKDIR}/component-config.yaml.template ${D}${datadir}/greengrass/components/${GREENGRASS_COMPONENT_NAME}/
}

do_compile_ptest() {
    # Compile component test
    ${CC} ${CFLAGS} ${LDFLAGS} -I${STAGING_INCDIR}/ggl \
        -o ${B}/test-component ${WORKDIR}/test-component.c -lggl-sdk -lpthread
}

do_install_ptest() {
    # Install test runner script
    install -m 0755 ${WORKDIR}/run-ptest ${D}${PTEST_PATH}/
    
    # Install test binary
    install -m 0755 ${B}/test-component ${D}${PTEST_PATH}/
    
    # Install component binary for testing
    install -m 0755 ${B}/hello-world-component ${D}${PTEST_PATH}/
}

FILES:${PN} = " \
    ${bindir}/hello-world-component \
    ${datadir}/greengrass/components/${GREENGRASS_COMPONENT_NAME}/* \
"

PACKAGES += "${PN}-ptest"

RDEPENDS:${PN} = "greengrass-bin"
RDEPENDS:${PN}-ptest = "${PN} aws-greengrass-sdk-lite-dev"
