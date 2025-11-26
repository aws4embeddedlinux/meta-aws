SUMMARY = "AWS Greengrass Component SDK - Lightweight AWS IoT Greengrass SDK"
DESCRIPTION = "The aws-greengrass-component-sdk provides an API for making AWS IoT Greengrass IPC \
calls with a small footprint. It enables Greengrass components to interact with \
the Greengrass Nucleus with less binary overhead and supports components written in C."
HOMEPAGE = "https://github.com/aws-greengrass/aws-greengrass-component-sdk"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

# RISC-V 32-bit not supported
COMPATIBLE_MACHINE:riscv32 = "null"

SRCREV = "1e54e70d4000ebf71572ba09e422d09666652787"
SRC_URI = "git://github.com/aws-greengrass/aws-greengrass-component-sdk.git;protocol=https;branch=main \
           file://0001-Fix-GCC-15-compatibility-for-MapIterator.patch \
           file://0002-Remove-hardcoded-clang-compiler.patch \
"

SRC_URI:append:armv7a = " \
           file://0003-Remove-bindgen-layout-tests-for-32-bit-ARM-support.patch \
           file://0004-Fix-timespec-types-for-32-bit-platforms.patch \
"

SRC_URI:append:armv7ve = " \
           file://0003-Remove-bindgen-layout-tests-for-32-bit-ARM-support.patch \
           file://0004-Fix-timespec-types-for-32-bit-platforms.patch \
"

inherit cmake ptest cargo cargo-update-recipe-crates

CARGO_SRC_DIR = "rust"

EXTRA_OECMAKE = " \
    -DCMAKE_BUILD_TYPE=MinSizeRel \
    -DBUILD_SHARED_LIBS=ON \
    -DBUILD_SAMPLES=ON \
    -DENABLE_WERROR=OFF \
"

# Override the random seed to use a reproducible path
TARGET_CXXFLAGS:append = " -frandom-seed=${TARGET_DBGSRC_DIR}"

PACKAGECONFIG ??= "rust"
PACKAGECONFIG[rust] = ",,,"

# default is stripped, we wanna do this by yocto
EXTRA_OECMAKE:append = " -DCMAKE_BUILD_TYPE=RelWithDebInfo"

DEBUG_PREFIX_MAP += "-ffile-prefix-map=${UNPACKDIR}=${TARGET_DBGSRC_DIR}"
DEBUG_PREFIX_MAP += "-ffile-prefix-map=${TMPDIR}=${TARGET_DBGSRC_DIR}"

python () {
    if bb.utils.contains('PACKAGECONFIG', 'rust', True, False, d):
        require_file = d.expand('${BPN}-crates.inc')
        bb.parse.mark_dependency(d, require_file)
        include_file = os.path.join(os.path.dirname(d.getVar('FILE')), require_file)
        if os.path.exists(include_file):
            bb.parse.BBHandler.handle(include_file, d, True)
}

do_configure:prepend() {
    cmake_do_configure
}

do_compile() {
    cmake_do_compile

    if echo "${PACKAGECONFIG}" | grep -q "rust"; then
        bbnote "Building Rust examples"
        cargo_do_compile
    fi
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

    if ${@bb.utils.contains('PACKAGECONFIG', 'rust', 'true', 'false', d)}; then
        if [ -d "${B}/target/${CARGO_TARGET_SUBDIR}/examples" ]; then
            for example in ${B}/target/${CARGO_TARGET_SUBDIR}/examples/*; do
                if [ -f "$example" ] && [ -x "$example" ] && [ ! "${example##*.}" = "d" ]; then
                    case "$(basename $example)" in
                        *-[0-9a-f][0-9a-f][0-9a-f][0-9a-f][0-9a-f][0-9a-f][0-9a-f][0-9a-f][0-9a-f][0-9a-f][0-9a-f][0-9a-f][0-9a-f][0-9a-f][0-9a-f][0-9a-f])
                            continue
                            ;;
                    esac
                    install -m 0755 "$example" ${D}${bindir}/rust_$(basename $example)
                fi
            done
        fi

        # Install Rust crate source for other recipes to use
        install -d ${D}${datadir}/cargo/registry/gg-sdk
        cp -r ${S}/rust/* ${D}${datadir}/cargo/registry/gg-sdk/
        cp -r ${S}/src ${D}${datadir}/cargo/registry/
        cp -r ${S}/include ${D}${datadir}/cargo/registry/
        cp -r ${S}/priv_include ${D}${datadir}/cargo/registry/
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
    install -m 0755 ${WORKDIR}/sources/run-ptest ${D}${PTEST_PATH}/

    if [ -d "${B}/bin" ]; then
        for sample in ${B}/bin/*; do
            if [ -f "$sample" ]; then
                install -m 0755 "$sample" ${D}${PTEST_PATH}/
            fi
        done
    fi

    if ${@bb.utils.contains('PACKAGECONFIG', 'rust', 'true', 'false', d)}; then
        if [ -d "${B}/target/${CARGO_TARGET_SUBDIR}/examples" ]; then
            for example in ${B}/target/${CARGO_TARGET_SUBDIR}/examples/*; do
                if [ -f "$example" ] && [ -x "$example" ] && [ ! "${example##*.}" = "d" ]; then
                    case "$(basename $example)" in
                        *-[0-9a-f][0-9a-f][0-9a-f][0-9a-f][0-9a-f][0-9a-f][0-9a-f][0-9a-f][0-9a-f][0-9a-f][0-9a-f][0-9a-f][0-9a-f][0-9a-f][0-9a-f][0-9a-f])
                            continue
                            ;;
                    esac
                    install -m 0755 "$example" ${D}${PTEST_PATH}/rust_$(basename $example)
                fi
            done
        fi
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
