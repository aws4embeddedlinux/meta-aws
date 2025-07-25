SUMMARY = "Client library for using AWS IoT Shadow service"
DESCRIPTION = "Client library for using AWS IoT Shadow service on embedded devices"
HOMEPAGE = "https://github.com/aws/device-shadow-for-aws-iot-embedded-sdk"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c8c19afab7f99fb196c9287cbd60a258"

SRC_URI = "\
    gitsm://github.com/aws/Device-Shadow-for-AWS-IoT-embedded-sdk.git;protocol=https;branch=main \
    file://CMakeLists.txt \
    file://Findshadow.cmake \
    file://run-ptest \
"

SRCREV = "28ca8cb66b185c5ee9e2458d2ae0259ccac86a8d"

inherit cmake ptest

EXTRA_OECMAKE += "-DCMAKE_POLICY_VERSION_MINIMUM=3.5"

OECMAKE_C_FLAGS:append = " -DSHADOW_DO_NOT_USE_CUSTOM_CONFIG=ON"

do_configure:prepend() {
    install ${UNPACKDIR}/CMakeLists.txt ${S}/
}

do_install:append() {
    install -d ${D}${datadir}/cmake/Modules
    install -m 0644 ${UNPACKDIR}/Findshadow.cmake ${D}${datadir}/cmake/Modules/
}

FILES:${PN} += "${libdir}/libshadow.so.*"

FILES:${PN}-dev += "\
    ${libdir}/libshadow.so \
    ${includedir}/shadow/* \
    ${datadir}/cmake/Modules/Findshadow.cmake \
    "

RDEPENDS:${PN}-ptest = "\
    aws-iot-device-sdk-embedded-c-shadow-demo-main \
"

# nooelint: oelint.vars.insaneskip:INSANE_SKIP
INSANE_SKIP:${PN} += "buildpaths"
