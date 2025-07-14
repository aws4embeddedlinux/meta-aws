SUMMARY = "AWS library to sign AWS HTTP requests"
DESCRIPTION = "AWS library to sign AWS HTTP requests with Signature Version 4 Signing Process."
HOMEPAGE = "https://github.com/aws/SigV4-for-AWS-IoT-embedded-sdk"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=000b2cc208c380dab61c7176d8ad5cfc"

SRC_URI = "\
    gitsm://github.com/aws/SigV4-for-AWS-IoT-embedded-sdk.git;protocol=https;branch=main \
    file://CMakeLists.txt \
    file://Findcore_sigv4.cmake \
"
SRCREV = "892bcbb2d4b95daf2b7306ba3210e74b25bfae16"

inherit cmake

EXTRA_OECMAKE:append = " \
    -DCMAKE_C_FLAGS=-DSIGV4_DO_NOT_USE_CUSTOM_CONFIG=ON \
    -DLIB_VERSION=${PV} \
    -DLIB_SOVERSION=${@d.getVar('PV').split('.')[0]} \
"

do_configure:prepend() {
    install ${UNPACKDIR}/CMakeLists.txt ${S}/
}

do_install:append() {
    install -d ${D}${datadir}/cmake/Modules
    install -m 0644 ${UNPACKDIR}/Findcore_sigv4.cmake ${D}${datadir}/cmake/Modules/
}

FILES:${PN} += "${libdir}/libcore_sigv4.so.*"
FILES:${PN}-dev += "\
    ${libdir}/libcore_sigv4.so \
    ${includedir}/libcore_sigv4/* \
    ${datadir}/cmake/Modules/Findcore_sigv4.cmake \
"

# nooelint: oelint.vars.insaneskip:INSANE_SKIP
INSANE_SKIP:${PN} += "buildpaths"
