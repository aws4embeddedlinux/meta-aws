SUMMARY = "implementation of the PKCS #11 standard"
DESCRIPTION = "Software implementation of the PKCS #11 standard."
HOMEPAGE = "https://github.com/FreeRTOS/corePKCS11"
LICENSE = "MIT & OASIS & Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7ae2be7fb1637141840314b51970a9f7 \
                    file://source/dependency/3rdparty/pkcs11/LICENSE.md;md5=636524f005338d77b2c83f6b111f87cd \
                    file://source/dependency/3rdparty/mbedtls/LICENSE;md5=379d5819937a6c2f1ef1630d341e026d"

NO_GENERIC_LICENSE[OASIS] = "source/dependency/3rdparty/pkcs11/LICENSE.md"

SRC_URI = "\
    gitsm://github.com/FreeRTOS/corePKCS11.git;protocol=https;branch=main;name=corepkscs11 \
    git://github.com/Mbed-TLS/mbedtls.git;protocol=https;branch=mbedtls-2.28;name=mbedtls;destsuffix=${S}/source/dependency/3rdparty/mbedtls \
    file://core_pkcs11_config.h \
    file://logging_levels.h \
    file://logging_stack.h \
    file://CMakeLists.txt \
    file://Findcore_pkcs.cmake \
    file://corepkcs11_mbedtls_config.h \
    file://CMakeLists.txt_mbedtls \
    "

SRCREV_corepkscs11 = "0a5fb6c9dd6233d5a869ab9970440be594d2a1c8"

MBEDTLS_2_VERSION = "2.28.10"
SRCREV_mbedtls = "2fc8413bfcb51354c8e679141b17b3f1a5942561"

SRCREV_FORMAT .= "_corepkscs11_mbedtls"

EXTRA_OECMAKE:append = " \
    -DFETCHCONTENT_SOURCE_DIR_MBEDTLS=${S}/source/dependency/3rdparty/mbedtls \
    -DLIB_VERSION=${PV} \
    -DLIB_SOVERSION=${@d.getVar('PV').split('.')[0]} \
"

OECMAKE_C_FLAGS:append = " -DPKCS_DO_NOT_USE_CUSTOM_CONFIG=ON"

LDFLAGS += "-Wl,--copy-dt-needed-entries"

inherit cmake ptest

EXTRA_OECMAKE += "-DCMAKE_POLICY_VERSION_MINIMUM=3.5"

do_configure:prepend() {
    # verify that all dependencies have correct version
    grep -q ${MBEDTLS_2_VERSION} ${S}/tools/mbedtls.cmake || bbfatal "ERROR: dependency version mismatch, please update 'SRCREV_mbedtls + MBEDTLS_2_VERSION'!"
    install ${UNPACKDIR}/CMakeLists.txt ${S}/
    install ${UNPACKDIR}/corepkcs11_mbedtls_config.h ${S}/source/dependency/3rdparty/mbedtls/include/mbedtls/mbedtls_config.h
    install ${UNPACKDIR}/CMakeLists.txt_mbedtls ${S}/source/dependency/3rdparty/mbedtls/CMakeLists.txt
    install ${UNPACKDIR}/core_pkcs11_config.h ${S}/source/include/
    install ${UNPACKDIR}/logging_levels.h ${S}/source/include/
    install ${UNPACKDIR}/logging_stack.h ${S}/source/include/
}

do_install:append() {
    install -d ${D}${datadir}/cmake/Modules
    install -m 0644 ${UNPACKDIR}/Findcore_pkcs.cmake ${D}${datadir}/cmake/Modules/
    install ${S}/source/dependency/3rdparty/pkcs11/published/2-40-errata-1/* ${D}/${includedir}/core_pkcs/
    install ${S}/source/dependency/3rdparty/mbedtls_utils/mbedtls_utils.h ${D}/${includedir}/core_pkcs/
}

FILES:${PN} = "${libdir}/lib*.so.*"
FILES:${PN}-dev += "\
    ${libdir}/lib*.so \
    ${includedir}/core_pkcs/* \
    ${datadir}/cmake/Modules/Findcore_pkcs.cmake \
"

EXTRA_OECMAKE += "-DPKCS11_PLATFORM=posix"

# nooelint: oelint.vars.insaneskip:INSANE_SKIP
INSANE_SKIP:${PN} += "buildpaths"