SUMMARY = "AWS library to sign AWS HTTP requests"
DESCRIPTION = "AWS library to sign AWS HTTP requests with Signature Version 4 Signing Process."
HOMEPAGE = "https://github.com/aws/SigV4-for-AWS-IoT-embedded-sdk"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=000b2cc208c380dab61c7176d8ad5cfc"

SRC_URI = "\
    gitsm://github.com/aws/SigV4-for-AWS-IoT-embedded-sdk.git;protocol=https;branch=main \
    file://CMakeLists.txt \
    file://Findsigv4.cmake \
    file://run-ptest \
"
SRCREV = "892bcbb2d4b95daf2b7306ba3210e74b25bfae16"

inherit cmake ptest

EXTRA_OECMAKE += "-DCMAKE_POLICY_VERSION_MINIMUM=3.5"

EXTRA_OECMAKE:append = " \
    -DLIB_VERSION=${PV} \
    -DLIB_SOVERSION=${@d.getVar('PV').split('.')[0]} \
"

OECMAKE_C_FLAGS:append = " -DSIGV4_DO_NOT_USE_CUSTOM_CONFIG=ON"

do_configure:prepend() {
    install ${UNPACKDIR}/CMakeLists.txt ${S}/
}

do_install:append() {
    install -d ${D}${datadir}/cmake/Modules
    install -m 0644 ${UNPACKDIR}/Findsigv4.cmake ${D}${datadir}/cmake/Modules/
}

FILES:${PN} += "${libdir}/libsigv4.so.*"
FILES:${PN}-dev += "\
    ${libdir}/libsigv4.so \
    ${includedir}/libsigv4/* \
    ${datadir}/cmake/Modules/Findsigv4.cmake \
"

RDEPENDS:${PN}-ptest += "aws-iot-device-sdk-embedded-c-http-demo-s3-download"

# nooelint: oelint.vars.insaneskip:INSANE_SKIP
INSANE_SKIP:${PN} += "buildpaths"
