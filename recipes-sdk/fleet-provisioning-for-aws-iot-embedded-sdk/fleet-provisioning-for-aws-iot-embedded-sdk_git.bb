SUMMARY = "Client library for using AWS IoT Fleet Provisioning service on embedded devices"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fbe4a2de4d0307d25b1d725d7d20d06c"

SRC_URI = "\
	gitsm://github.com/aws/Fleet-Provisioning-for-AWS-IoT-embedded-sdk.git;protocol=https;branch=main \
    file://CMakeLists.txt \
    file://Findfleetprovisioning.cmake \
"

SRCREV = "629ec2a21d91ade13bfb995aace2223b36c4cd2e"

inherit cmake

EXTRA_OECMAKE:append = " -DCMAKE_C_FLAGS=-DFLEET_PROVISIONING_DO_NOT_USE_CUSTOM_CONFIG=ON"

do_configure:prepend() {
    cp ${UNPACKDIR}/CMakeLists.txt ${S}/
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

# nooelint: oelint.vars.insaneskip:INSANE_SKIP
INSANE_SKIP:${PN} += "buildpaths"
