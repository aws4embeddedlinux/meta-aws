SUMMARY = "AWS Greengrass Component SDK - Lightweight AWS IoT Greengrass SDK"
DESCRIPTION = "The aws-greengrass-component-sdk provides an API for making AWS IoT Greengrass IPC \
calls with a small footprint. It enables Greengrass components to interact with \
the Greengrass Nucleus with less binary overhead and supports components written in C."
HOMEPAGE = "https://github.com/aws-greengrass/aws-greengrass-component-sdk"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

SRCREV = "1e54e70d4000ebf71572ba09e422d09666652787"
SRC_URI = "git://github.com/aws-greengrass/aws-greengrass-component-sdk.git;protocol=https;branch=main"

S = "${WORKDIR}/git"

inherit cmake ptest

EXTRA_OECMAKE = " \
    -DCMAKE_BUILD_TYPE=MinSizeRel \
    -DBUILD_SHARED_LIBS=ON \
    -DBUILD_SAMPLES=ON \
    -DENABLE_WERROR=OFF \
"

# default is stripped, we wanna do this by yocto
EXTRA_OECMAKE:append = " -DCMAKE_BUILD_TYPE=RelWithDebInfo"

DEBUG_PREFIX_MAP += "-ffile-prefix-map=${WORKDIR}=${TARGET_DBGSRC_DIR}"
DEBUG_PREFIX_MAP += "-ffile-prefix-map=${TMPDIR}=${TARGET_DBGSRC_DIR}"

do_configure:prepend() {
    cmake_do_configure
}

SRC_URI:append = " file://run-ptest"

do_install() {
    cmake --install ${B} --prefix ${D}${prefix}

    install -d ${D}${bindir}
    if [ -d "${B}/bin" ]; then
        for sample in ${B}/bin/*; do
            if [ -f "$sample" ]; then
                install -m 0755 "$sample" ${D}${bindir}/
            fi
        done
    fi

    if [ ! -d "${D}${includedir}/gg" ]; then
        install -d ${D}${includedir}/gg
        cp -r ${S}/include/gg/* ${D}${includedir}/gg/
    fi

    # Install C++ headers and library
    if [ -d "${S}/cpp/include" ]; then
        cp -r ${S}/cpp/include/gg/* ${D}${includedir}/gg/
        if [ -f "${B}/cpp/libgg-sdk++.a" ]; then
            install -m 0644 ${B}/cpp/libgg-sdk++.a ${D}${libdir}/
        fi
    fi

    install -d ${D}${docdir}/${PN}
    install -m 0644 ${S}/README.md ${D}${docdir}/${PN}/
    install -m 0644 ${S}/docs/BUILD.md ${D}${docdir}/${PN}/

    # Strip debug info from static libraries to remove TMPDIR references
    if [ -f "${D}${libdir}/libgg-sdk++.a" ]; then
        ${STRIP} --strip-debug ${D}${libdir}/libgg-sdk++.a
    fi
}

do_install_ptest() {
    install -m 0755 ${WORKDIR}/run-ptest ${D}${PTEST_PATH}/

    if [ -d "${B}/bin" ]; then
        for sample in ${B}/bin/*; do
            if [ -f "$sample" ]; then
                install -m 0755 "$sample" ${D}${PTEST_PATH}/
            fi
        done
    fi

     rm -rf ${D}${PTEST_PATH}/.debug
}

PACKAGES = "${PN} ${PN}-dev ${PN}-staticdev ${PN}-doc ${PN}-ptest ${PN}-dbg"

FILES:${PN} = " \
    ${libdir}/libgg-sdk.so* \
    ${bindir}/* \
"

FILES:${PN}-dev = " \
    ${includedir}/gg/* \
    ${datadir}/cargo/* \
"

FILES:${PN}-staticdev = " \
    ${libdir}/libgg-sdk.a \
    ${libdir}/libgg-sdk++.a \
"

FILES:${PN}-doc = " \
    ${docdir}/${PN}/* \
"

FILES:${PN}-ptest = " \
    ${PTEST_PATH}/* \
"

FILES:${PN}-dbg = " \
    ${libdir}/.debug/* \
    ${bindir}/.debug/* \
    ${libdir}/aws-greengrass-component-sdk/ptest/.debug/* \
"

# nooelint: oelint.vars.insaneskip:INSANE_SKIP needs to be added above
INSANE_SKIP:${PN}-staticdev = "buildpaths"

BBCLASSEXTEND = "native nativesdk"
