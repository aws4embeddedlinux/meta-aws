SUMMARY = "AWS Greengrass Hello World C++ Component"
DESCRIPTION = "A simple example component using AWS Greengrass Component SDK in C++"
HOMEPAGE = "https://github.com/aws/meta-aws"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

# Default to lite variant, can be overwritten (classic)
GREENGRASS_VARIANT ?= "lite"

DEPENDS = "aws-greengrass-component-sdk"

TARGET_CXXFLAGS:remove = "-flto=auto -ffat-lto-objects"
TARGET_CXXFLAGS:append = " -fno-lto"
TARGET_LDFLAGS:remove = "-flto=auto -ffat-lto-objects"
TARGET_LDFLAGS:append = " -fno-lto"
TARGET_LDFLAGS:remove = "-Wl,--as-needed"
TARGET_LDFLAGS:append = " -Wl,--no-as-needed"

SRC_URI = " \
    file://main.cpp \
    file://CMakeLists.txt \
    file://component-recipe.yaml \
    file://run-ptest \
"

S = "${WORKDIR}"

inherit cmake ptest
inherit_defer ${@'greengrass-lite-component' if d.getVar('GREENGRASS_VARIANT') == 'lite' else 'greengrass-component'}

# nooelint: oelint.vars.insaneskip:INSANE_SKIP
INSANE_SKIP:${PN}-dbg += "buildpaths"

RDEPENDS:${PN}-ptest += "grep"

do_install_ptest() {
    install -m 0755 ${S}/run-ptest ${D}${PTEST_PATH}/
    install -d ${D}${PTEST_PATH}/test-data
    install -m 0644 ${S}/component-recipe.yaml ${D}${PTEST_PATH}/test-data/
    echo "${GREENGRASS_VARIANT}" > ${D}${PTEST_PATH}/test-data/greengrass-variant
    echo "com.example.HelloWorldCpp" > ${D}${PTEST_PATH}/test-data/component-name
    echo "1.0.0" > ${D}${PTEST_PATH}/test-data/component-version
    echo "hello-world-cpp" > ${D}${PTEST_PATH}/test-data/expected-artifact
}

do_install:append() {
    if [ "${GREENGRASS_VARIANT}" = "lite" ]; then
        install -m 0755 ${B}/hello-world-cpp ${D}${GGL_ARTIFACTS_DIR}/${COMPONENT_NAME}/${COMPONENT_VERSION}/
    fi
}

FILES:${PN} = "${bindir}/* ${datadir}/greengrass/*"
