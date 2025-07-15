SUMMARY = "A parser strictly enforcing the ECMA-404 JSON standard."
DESCRIPTION = "A parser strictly enforcing the ECMA-404 JSON standard, suitable for microcontrollers."
HOMEPAGE = "https://github.com/FreeRTOS/coreJSON"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7ae2be7fb1637141840314b51970a9f7"

SRC_URI = "\
    gitsm://github.com/FreeRTOS/coreJSON.git;protocol=https;branch=main \
    file://CMakeLists.txt \
    file://Findcore_json.cmake \
    file://run-ptest \
"

SRCREV = "03a463f23ead5240974c663477baf03fe82fc530"

inherit cmake ptest

EXTRA_OECMAKE:append = " \
    -DLIB_VERSION=${PV} \
    -DLIB_SOVERSION=${@d.getVar('PV').split('.')[0]} \
"

do_configure:prepend() {
    cp ${UNPACKDIR}/CMakeLists.txt ${S}/
}

do_install:append() {
    install -d ${D}${datadir}/cmake/Modules
    install -m 0644 ${UNPACKDIR}/Findcore_json.cmake ${D}${datadir}/cmake/Modules/
}

FILES:${PN} += "${libdir}/libcore_json.so.*"
FILES:${PN}-dev += "\
    ${libdir}/libcore_json.so \
    ${includedir}/libcore_json/* \
    ${datadir}/cmake/Modules/Findcore_json.cmake \
"

RDEPENDS:${PN}-ptest += "aws-iot-device-sdk-embedded-c-jobs-demo-mosquitto"