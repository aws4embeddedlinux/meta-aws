SUMMARY = "Client implementation of the MQTT 3.1.1 specification"
DESCRIPTION = "Client implementation of the MQTT 3.1.1 specification for embedded devices"
HOMEPAGE = "https://github.com/FreeRTOS/coreMQTT"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7ae2be7fb1637141840314b51970a9f7"

SRC_URI = "gitsm://github.com/FreeRTOS/coreMQTT.git;protocol=https;branch=main \
    file://CMakeLists.txt \
    file://Findcore_mqtt.cmake \
"

SRCREV = "d7b04a13002496994d737eebaf56dbe1e56aaefb"

inherit cmake

EXTRA_OECMAKE:append = " \
    -DCMAKE_C_FLAGS=-DMQTT_DO_NOT_USE_CUSTOM_CONFIG=ON \
    -DLIB_VERSION=${PV} \
    -DLIB_SOVERSION=${@d.getVar('PV').split('.')[0]} \
"

do_configure:prepend() {
    cp ${UNPACKDIR}/CMakeLists.txt ${S}/
}

do_install:append() {
    install -d ${D}${datadir}/cmake/Modules
    install -m 0644 ${UNPACKDIR}/Findcore_mqtt.cmake ${D}${datadir}/cmake/Modules/
    install ${S}/source/interface/transport_interface.h ${D}${includedir}/core_mqtt/
}

FILES:${PN} += "${libdir}/libcore_mqtt.so.*"

FILES:${PN}-dev += "\
    ${libdir}/libcore_mqtt.so \
    ${includedir}/core_mqtt/* \
    ${datadir}/cmake/Modules/Findcore_mqtt.cmake \
"
# nooelint: oelint.vars.insaneskip:INSANE_SKIP
INSANE_SKIP:${PN} += "buildpaths"