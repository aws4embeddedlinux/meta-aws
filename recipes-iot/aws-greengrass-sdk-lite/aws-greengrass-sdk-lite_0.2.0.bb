SUMMARY = "AWS Greengrass SDK Lite - Lightweight AWS IoT Greengrass SDK"
DESCRIPTION = "The aws-greengrass-sdk-lite provides an API for making AWS IoT Greengrass IPC \
calls with a small footprint. It enables Greengrass components to interact with \
the Greengrass Nucleus with less binary overhead and supports components written in C."
HOMEPAGE = "https://github.com/aws-greengrass/aws-greengrass-sdk-lite"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

SRCREV = "0835c6ad20948ab55d86fbd5864fd38e62559ed2"
SRC_URI = "git://github.com/aws-greengrass/aws-greengrass-sdk-lite.git;protocol=https;branch=main \
           file://fix-crc32-syntax.patch \
"

inherit cmake ptest

# Build configuration
EXTRA_OECMAKE = " \
    -DCMAKE_BUILD_TYPE=MinSizeRel \
    -DBUILD_SAMPLES=${@bb.utils.contains('PACKAGECONFIG', 'samples', 'ON', 'OFF', d)} \
    -DENABLE_WERROR=OFF \
"

# Package configuration options
PACKAGECONFIG ??= "samples"
PACKAGECONFIG[samples] = ",,,"

# Add ptest source files
SRC_URI += " \
    file://run-ptest \
    file://test-basic-api.c \
    file://test-buffer-ops.c \
    file://test-json-ops.c \
"

# Runtime dependencies
RDEPENDS:${PN} = ""

# Build dependencies
DEPENDS = ""

# The SDK has no third-party library dependencies
# Currently supports only Linux targets using Glibc or Musl

do_install() {
    # Install the main library
    install -d ${D}${libdir}
    install -m 0644 ${B}/libggl-sdk.a ${D}${libdir}/

    # Install headers
    install -d ${D}${includedir}/ggl
    cp -r ${S}/include/ggl/* ${D}${includedir}/ggl/

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

    # Install documentation
    install -d ${D}${docdir}/${PN}
    install -m 0644 ${S}/README.md ${D}${docdir}/${PN}/
    install -m 0644 ${S}/docs/BUILD.md ${D}${docdir}/${PN}/
}

do_compile_ptest() {
    # Compile test programs
    ${CC} ${CFLAGS} ${LDFLAGS} -I${S}/include -I${S}/priv_include \
        -o ${B}/test-basic-api ${WORKDIR}/sources/test-basic-api.c ${B}/libggl-sdk.a -lpthread
    
    ${CC} ${CFLAGS} ${LDFLAGS} -I${S}/include -I${S}/priv_include \
        -o ${B}/test-buffer-ops ${WORKDIR}/sources/test-buffer-ops.c ${B}/libggl-sdk.a -lpthread
    
    ${CC} ${CFLAGS} ${LDFLAGS} -I${S}/include -I${S}/priv_include \
        -o ${B}/test-json-ops ${WORKDIR}/sources/test-json-ops.c ${B}/libggl-sdk.a -lpthread
}

do_install_ptest() {
    # Install test runner script
    install -m 0755 ${WORKDIR}/sources/run-ptest ${D}${PTEST_PATH}/
    
    # Install test binaries
    install -m 0755 ${B}/test-basic-api ${D}${PTEST_PATH}/
    install -m 0755 ${B}/test-buffer-ops ${D}${PTEST_PATH}/
    install -m 0755 ${B}/test-json-ops ${D}${PTEST_PATH}/
}

# Package the library and development files
PACKAGES = "${PN} ${PN}-dev ${PN}-doc ${PN}-ptest"

# Main package contains sample binaries if enabled
FILES:${PN} = "${@bb.utils.contains('PACKAGECONFIG', 'samples', '${bindir}/*', '', d)}"

# Development package contains headers and static library
FILES:${PN}-dev = " \
    ${includedir}/ggl/* \
    ${libdir}/libggl-sdk.a \
"

# Documentation package
FILES:${PN}-doc = " \
    ${docdir}/${PN}/* \
"

# Allow empty main package if samples are disabled
ALLOW_EMPTY:${PN} = "1"

# Static library, so no shared library versioning needed
SOLIBS = ""
FILES_SOLIBSDEV = ""

# Skip QA checks for already-stripped sample binaries and build paths in static library
INSANE_SKIP:${PN} += "already-stripped"
INSANE_SKIP:${PN}-dev += "buildpaths staticdev"

# Runtime dependencies for ptest
RDEPENDS:${PN}-ptest += "bash"

BBCLASSEXTEND = "native nativesdk"
