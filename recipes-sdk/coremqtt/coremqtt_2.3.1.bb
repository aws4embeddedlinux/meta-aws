SUMMARY = "Client implementation of the MQTT 3.1.1 specification"
DESCRIPTION = "Client implementation of the MQTT 3.1.1 specification for embedded devices"
HOMEPAGE = "https://github.com/FreeRTOS/coreMQTT"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7ae2be7fb1637141840314b51970a9f7"

SRC_URI = "gitsm://github.com/FreeRTOS/coreMQTT.git;protocol=https;branch=main \
    file://CMakeLists.txt \
    file://Findcore_mqtt.cmake \
    file://run-ptest \
"

SRCREV = "d7b04a13002496994d737eebaf56dbe1e56aaefb"

inherit cmake ptest

EXTRA_OECMAKE += "-DCMAKE_POLICY_VERSION_MINIMUM=3.5"

EXTRA_OECMAKE:append = " \
    -DLIB_VERSION=${PV} \
    -DLIB_SOVERSION=${@d.getVar('PV').split('.')[0]} \
"

OECMAKE_C_FLAGS:append = " -DMQTT_DO_NOT_USE_CUSTOM_CONFIG=ON"

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

RDEPENDS:${PN}-ptest += "aws-iot-device-sdk-embedded-c-jobs-demo-mosquitto"

# nooelint: oelint.vars.insaneskip:INSANE_SKIP
INSANE_SKIP:${PN} += "buildpaths"