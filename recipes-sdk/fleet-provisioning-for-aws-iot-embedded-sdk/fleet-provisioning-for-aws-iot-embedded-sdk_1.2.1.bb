SUMMARY = "Client library for using AWS IoT Fleet Provisioning service"
DESCRIPTION = "Client library for using AWS IoT Fleet Provisioning service on embedded devices"
HOMEPAGE = "https://github.com/aws/Fleet-Provisioning-for-AWS-IoT-embedded-sdk"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fbe4a2de4d0307d25b1d725d7d20d06c"

SRC_URI = "\
    gitsm://github.com/aws/Fleet-Provisioning-for-AWS-IoT-embedded-sdk.git;protocol=https;branch=main \
    file://CMakeLists.txt \
    file://Findfleetprovisioning.cmake \
"

SRCREV = "203803d8b506b51dad37b326292f7d153e9f8c2c"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE:append = " \
    -DCMAKE_C_FLAGS=-DFLEET_PROVISIONING_DO_NOT_USE_CUSTOM_CONFIG=ON \
    -DLIB_VERSION=${PV} \
    -DLIB_SOVERSION=${@d.getVar('PV').split('.')[0]} \
"

do_configure:prepend() {
    install ${WORKDIR}/CMakeLists.txt ${S}/
}

do_install:append() {
    install -d ${D}${datadir}/cmake/Modules
    install -m 0644 ${WORKDIR}/Findfleetprovisioning.cmake ${D}${datadir}/cmake/Modules/
}

FILES:${PN} += "${libdir}/libcore_http.so.*"

FILES:${PN}-dev += "\
    ${libdir}/libfleetprovisioning.so \
    ${includedir}/fleetprovisioning/* \
    ${datadir}/cmake/Modules/Findfleetprovisioning.cmake \
"

# nooelint: oelint.vars.insaneskip:INSANE_SKIP
INSANE_SKIP:${PN} += "buildpaths"
