SUMMARY = "AWS Greengrass SDK Lite - Lightweight AWS IoT Greengrass SDK"
DESCRIPTION = "The aws-greengrass-sdk-lite provides an API for making AWS IoT Greengrass IPC \
calls with a small footprint. It enables Greengrass components to interact with \
the Greengrass Nucleus with less binary overhead and supports components written in C."
HOMEPAGE = "https://github.com/aws-greengrass/aws-greengrass-sdk-lite"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

SRCREV = "1ee3a5ad3de59f141973839412b5025e67ea533d"
SRC_URI = "git://github.com/aws-greengrass/aws-greengrass-sdk-lite.git;protocol=https;branch=main \
"

S = "${UNPACKDIR}/git"

inherit cmake ptest

# Build configuration for both static and shared libraries
EXTRA_OECMAKE = " \
    -DCMAKE_BUILD_TYPE=MinSizeRel \
    -DBUILD_SHARED_LIBS=ON \
    -DBUILD_SAMPLES=${@bb.utils.contains('PACKAGECONFIG', 'samples', 'ON', 'OFF', d)} \
    -DENABLE_WERROR=OFF \
"

# Package configuration options
PACKAGECONFIG ??= "samples"
PACKAGECONFIG[samples] = ",,,"

# Add ptest source files
SRC_URI:append = " \
    file://run-ptest \
    file://test-basic-api.c \
    file://test-json-ops.c \
"

# Runtime dependencies
RDEPENDS:${PN} = ""

# Build dependencies
DEPENDS += ""

# The SDK has no third-party library dependencies
# Currently supports only Linux targets using Glibc or Musl

do_install() {
    # Use cmake install to handle both libraries
    cmake --install ${B} --prefix ${D}${prefix}

    # Install samples if enabled
    if ${@bb.utils.contains('PACKAGECONFIG', 'samples', 'true', 'false', d)}; then
        install -d ${D}${bindir}

        # Install sample binaries if they exist
        if [ -d "${B}/bin" ]; then
            for sample in ${B}/bin/*; do
                if [ -f "$sample" ]; then
                    install -m 0755 "$sample" ${D}${bindir}/
                fi
            done
        fi
    fi

    # Install headers (if not already installed by cmake)
    if [ ! -d "${D}${includedir}/ggl" ]; then
        install -d ${D}${includedir}/ggl
        cp -r ${S}/include/ggl/* ${D}${includedir}/ggl/
    fi

    # Install documentation
    install -d ${D}${docdir}/${PN}
    install -m 0644 ${S}/README.md ${D}${docdir}/${PN}/
    install -m 0644 ${S}/docs/BUILD.md ${D}${docdir}/${PN}/

    # Remove any debug files that might be created automatically
    find ${D} -name ".debug" -type d -exec rm -rf {} + 2>/dev/null || true
}

do_compile_ptest() {
    # Compile test programs using shared library
    ${CC} ${CFLAGS} ${LDFLAGS} -I${S}/include -I${S}/priv_include \
        -o ${B}/test-basic-api ${UNPACKDIR}/test-basic-api.c -L${B}/lib -lggl-sdk -lpthread

    ${CC} ${CFLAGS} ${LDFLAGS} -I${S}/include -I${S}/priv_include \
        -o ${B}/test-json-ops ${UNPACKDIR}/test-json-ops.c -L${B}/lib -lggl-sdk -lpthread
}

do_install_ptest() {
    # Install test runner script
    install -m 0755 ${UNPACKDIR}/run-ptest ${D}${PTEST_PATH}/

    # Install test binaries
    install -m 0755 ${B}/test-basic-api ${D}${PTEST_PATH}/
    install -m 0755 ${B}/test-json-ops ${D}${PTEST_PATH}/

    # Remove debug files to avoid packaging issues
    rm -rf ${D}${PTEST_PATH}/.debug
}

# Package the library and development files properly
PACKAGES = "${PN} ${PN}-dev ${PN}-staticdev ${PN}-doc ${PN}-ptest ${PN}-ptest-dbg"

# Main package contains shared library and sample binaries if enabled
FILES:${PN} = " \
    ${libdir}/libggl-sdk.so* \
    ${@bb.utils.contains('PACKAGECONFIG', 'samples', '${bindir}/*', '', d)} \
"

# Development package contains headers
FILES:${PN}-dev = " \
    ${includedir}/ggl/* \
"

# Static development package contains static library
FILES:${PN}-staticdev = " \
    ${libdir}/libggl-sdk.a \
    ${libdir}/libggl-sdk++.a \
"

# Documentation package
FILES:${PN}-doc = " \
    ${docdir}/${PN}/* \
"

# Ptest package
FILES:${PN}-ptest = " \
    ${PTEST_PATH}/* \
"
FILES:${PN}-ptest-dbg = " \
    /usr/lib/aws-greengrass-sdk-lite/ptest/.debug/test-json-ops \
    /usr/lib/aws-greengrass-sdk-lite/ptest/.debug/test-basic-api \
"
# nooelint: oelint.vars.insaneskip
INSANE_SKIP:${PN} += "already-stripped"

# nooelint: oelint.vars.insaneskip
INSANE_SKIP:${PN}-ptest += "buildpaths"
# nooelint: oelint.vars.insaneskip
INSANE_SKIP:${PN}-ptest-dbg += "buildpaths"

# nooelint: oelint.vars.insaneskip
INSANE_SKIP:${PN}-staticdev += "buildpaths"

# Runtime dependencies for ptest
RDEPENDS:${PN}-ptest += "bash ${PN}"

BBCLASSEXTEND = "native nativesdk"
