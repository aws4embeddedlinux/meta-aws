SUMMARY = "AWS Greengrass Component SDK - Lightweight AWS IoT Greengrass SDK"
DESCRIPTION = "The aws-greengrass-component-sdk provides an API for making AWS IoT Greengrass IPC \
calls with a small footprint. It enables Greengrass components to interact with \
the Greengrass Nucleus with less binary overhead and supports components written in C."
HOMEPAGE = "https://github.com/aws-greengrass/aws-greengrass-component-sdk"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

PV = "0.4.0+git${SRCPV}"

SRCREV = "4ee9ad4ef5ae5a190a5076e7a823510f9ee2a433"
SRC_URI = "git://github.com/aws-greengrass/aws-greengrass-component-sdk.git;protocol=https;branch=main \
           file://0001-Add-bindgen-to-build.rs.patch \
           file://0003-Build-gg-sdk-as-cdylib.patch \
           file://0004-Update-Cargo.lock-for-bindgen.patch \
           file://0005-Disable-strip-in-Cargo-profile.patch \
"

# nooelint: oelint.vars.specific
SRC_URI:append:arm = " file://0002-Fix-timespec-types-for-32-bit-platforms.patch"

inherit cmake ptest cargo cargo-update-recipe-crates

DEPENDS += "clang-native"

CARGO_SRC_DIR = "rust"

# Bindgen will regenerate c.rs at build time
do_configure:prepend() {
    rm -f ${S}/rust/src/c.rs
}

# Manual bindgen task for debugging (not called automatically)
# Usage: bitbake aws-greengrass-component-sdk -c create_bindings
do_create_bindings[depends] += "bindgen-cli-native:do_populate_sysroot clang-native:do_populate_sysroot"
do_create_bindings() {
    ${STAGING_BINDIR_NATIVE}/bindgen ${S}/rust/wrapper.h -o ${S}/rust/src/c.rs \
        --allowlist-type "Gg.*" \
        --allowlist-function "gg_.*" \
        --allowlist-var "GG_.*" \
        -- -I${S}/include --target=${RUST_HOST_SYS}
    bbnote "Generated bindings at ${S}/rust/src/c.rs"
}

# Set LIBCLANG_PATH for bindgen
export LIBCLANG_PATH = "${STAGING_LIBDIR_NATIVE}"

# Set bindgen to use target sysroot
export BINDGEN_EXTRA_CLANG_ARGS = "--sysroot=${STAGING_DIR_TARGET} ${TARGET_CC_ARCH}"

EXTRA_OECMAKE = " \
    -DCMAKE_BUILD_TYPE=MinSizeRel \
    -DBUILD_SHARED_LIBS=ON \
    -DBUILD_SAMPLES=ON \
    -DENABLE_WERROR=OFF \
"

PACKAGECONFIG ??= "rust"
PACKAGECONFIG[rust] = ",,,"

# default is stripped, we wanna do this by yocto
EXTRA_OECMAKE:append = " -DCMAKE_BUILD_TYPE=RelWithDebInfo"

DEBUG_PREFIX_MAP += "-ffile-prefix-map=${UNPACKDIR}=${TARGET_DBGSRC_DIR}"
DEBUG_PREFIX_MAP += "-ffile-prefix-map=${TMPDIR}=${TARGET_DBGSRC_DIR}"

# Apply DEBUG_PREFIX_MAP to Rust builds via remap-path-prefix
RUSTFLAGS:append = " --remap-path-prefix=${UNPACKDIR}=${TARGET_DBGSRC_DIR} --remap-path-prefix=${TMPDIR}=${TARGET_DBGSRC_DIR}"

# Ensure C code built by build.rs also gets the prefix map
export CFLAGS:append = " ${DEBUG_PREFIX_MAP}"
export CXXFLAGS:append = " ${DEBUG_PREFIX_MAP}"

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

        # Install Rust shared library for runtime
        install -d ${D}${libdir}
        install -m 0755 ${B}/target/${CARGO_TARGET_SUBDIR}/libgg_sdk.so ${D}${libdir}/

        # Install Rust rlib for compile-time
        install -d ${D}${libdir}/rustlib/${RUST_HOST_SYS}/lib
        install -m 0644 ${B}/target/${CARGO_TARGET_SUBDIR}/libgg_sdk.rlib ${D}${libdir}/rustlib/${RUST_HOST_SYS}/lib/
        if [ -f ${B}/target/${CARGO_TARGET_SUBDIR}/deps/libgg_sdk-*.rmeta ]; then
            install -m 0644 ${B}/target/${CARGO_TARGET_SUBDIR}/deps/libgg_sdk-*.rmeta ${D}${libdir}/rustlib/${RUST_HOST_SYS}/lib/
        fi

        # Install C static library
        install -m 0644 ${B}/target/${CARGO_TARGET_SUBDIR}/build/gg-sdk-*/out/libgg-sdk.a ${D}${libdir}/

        # Install headers
        install -d ${D}${includedir}/gg
        cp -r ${S}/include/gg/* ${D}${includedir}/gg/
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
    if [ -f "${D}${libdir}/libgg-sdk.a" ]; then
        ${STRIP} --strip-debug ${D}${libdir}/libgg-sdk.a
    fi
    if [ -f "${D}${libdir}/libgg-sdk++.a" ]; then
        ${STRIP} --strip-debug ${D}${libdir}/libgg-sdk++.a
    fi
    if [ -f "${D}${libdir}/rustlib/${RUST_HOST_SYS}/lib/libgg_sdk.rlib" ]; then
        ${STRIP} --strip-debug ${D}${libdir}/rustlib/${RUST_HOST_SYS}/lib/libgg_sdk.rlib
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
    ${libdir}/libgg_sdk.so* \
    ${bindir}/* \
"

FILES:${PN}-dev = " \
    ${includedir}/gg/* \
    ${libdir}/rustlib/* \
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

# Skip buildpaths QA check for static libraries which contain embedded build paths
# nooelint: oelint.vars.insaneskip:INSANE_SKIP
INSANE_SKIP:${PN}-staticdev = "buildpaths"
# nooelint: oelint.vars.insaneskip:INSANE_SKIP
INSANE_SKIP:${PN}-dev = "buildpaths"

BBCLASSEXTEND = "native nativesdk"
