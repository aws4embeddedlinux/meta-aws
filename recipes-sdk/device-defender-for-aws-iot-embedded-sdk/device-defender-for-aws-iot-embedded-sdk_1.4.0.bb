SUMMARY = "Client library for using AWS IoT Defender service"
DESCRIPTION = "Client library for using AWS IoT Defender service on embedded devices."
HOMEPAGE = "https://github.com/aws/Device-Defender-for-AWS-IoT-embedded-sdk"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fbe4a2de4d0307d25b1d725d7d20d06c"

SRC_URI = "\
    gitsm://github.com/aws/Device-Defender-for-AWS-IoT-embedded-sdk.git;protocol=https;branch=main \
    file://CMakeLists.txt \
    file://Finddefender.cmake \
"

SRCREV = "33f9087d4b6db3c5c024c3254426bbba30a0f10a"

inherit cmake

EXTRA_OECMAKE:append = " \
    -DCMAKE_C_FLAGS=-DDEFENDER_DO_NOT_USE_CUSTOM_CONFIG=ON \
    -DLIB_VERSION=${PV} \
    -DLIB_SOVERSION=${@d.getVar('PV').split('.')[0]} \
"

do_configure:prepend() {
    install ${UNPACKDIR}/CMakeLists.txt ${S}/
}

do_install:append() {
    install -d ${D}${datadir}/cmake/Modules
    install -m 0644 ${UNPACKDIR}/Finddefender.cmake ${D}${datadir}/cmake/Modules/
}

FILES:${PN} += "${libdir}/libcore_http.so.*"

FILES:${PN}-dev += "\
    ${libdir}/libdefender.so \
    ${includedir}/defender/* \
    ${datadir}/cmake/Modules/Finddefender.cmake \
"

# nooelint: oelint.vars.insaneskip:INSANE_SKIP
INSANE_SKIP:${PN} += "buildpaths"
