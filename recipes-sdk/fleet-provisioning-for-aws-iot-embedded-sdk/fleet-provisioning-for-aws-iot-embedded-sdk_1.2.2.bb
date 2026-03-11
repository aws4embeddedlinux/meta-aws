SUMMARY = "Client library for using AWS IoT Fleet Provisioning service"
DESCRIPTION = "Client library for using AWS IoT Fleet Provisioning service on embedded devices"
HOMEPAGE = "https://github.com/aws/Fleet-Provisioning-for-AWS-IoT-embedded-sdk"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fbe4a2de4d0307d25b1d725d7d20d06c"

SRC_URI = "\
    gitsm://github.com/aws/Fleet-Provisioning-for-AWS-IoT-embedded-sdk.git;protocol=https;branch=main \
    file://CMakeLists.txt \
    file://Findfleetprovisioning.cmake \
    file://run-ptest \
"

SRCREV = "7691e8af27bd0a42b7fe2f21c8b3289fa037df17"

inherit cmake ptest

EXTRA_OECMAKE += "-DCMAKE_POLICY_VERSION_MINIMUM=3.5"

EXTRA_OECMAKE:append = " \
    -DLIB_VERSION=${PV} \
    -DLIB_SOVERSION=${@d.getVar('PV').split('.')[0]} \
"

OECMAKE_C_FLAGS:append = " -DFLEET_PROVISIONING_DO_NOT_USE_CUSTOM_CONFIG=ON"

do_configure:prepend() {
    install ${UNPACKDIR}/CMakeLists.txt ${S}/
}

do_install:append() {
    install -d ${D}${datadir}/cmake/Modules
    install -m 0644 ${UNPACKDIR}/Findfleetprovisioning.cmake ${D}${datadir}/cmake/Modules/
}

FILES:${PN} += "${libdir}/libcore_http.so.*"

FILES:${PN}-dev += "\
    ${libdir}/libfleetprovisioning.so \
    ${includedir}/fleetprovisioning/* \
    ${datadir}/cmake/Modules/Findfleetprovisioning.cmake \
"

RDEPENDS:${PN}-ptest += "aws-iot-device-sdk-embedded-c-fleet-provisioning-keys-cert"

# nooelint: oelint.vars.insaneskip:INSANE_SKIP
INSANE_SKIP:${PN} += "buildpaths"
