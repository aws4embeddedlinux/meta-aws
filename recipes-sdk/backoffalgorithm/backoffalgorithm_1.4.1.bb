SUMMARY = "Algorithm for calculating exponential backoff"
DESCRIPTION = "Algorithm for calculating exponential backoff with jitter for network retry attempts."
HOMEPAGE = "https://github.com/FreeRTOS/backoffAlgorithm"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7ae2be7fb1637141840314b51970a9f7"

SRC_URI = "gitsm://github.com/FreeRTOS/backoffAlgorithm.git;protocol=https;branch=main \
    file://CMakeLists.txt \
    file://Findbackoffalgorithm.cmake \
    file://run-ptest \
"

SRCREV = "50e30d7d0aa0aa74e85134088a86f91978267d41"

S = "${WORKDIR}/git"

inherit cmake ptest

EXTRA_OECMAKE:append = " \
    -DLIB_VERSION=${PV} \
    -DLIB_SOVERSION=${@d.getVar('PV').split('.')[0]} \
"

do_configure:prepend() {
    cp ${WORKDIR}/CMakeLists.txt ${S}/
}

do_install:append() {
    install -d ${D}${datadir}/cmake/Modules
    install -m 0644 ${WORKDIR}/Findbackoffalgorithm.cmake ${D}${datadir}/cmake/Modules/
}

FILES:${PN} += "${libdir}/libbackoffalgorithm.so.*"
FILES:${PN}-dev += "\
    ${libdir}/libbackoffalgorithm.so \
    ${includedir}/backoffalgorithm/* \
    ${datadir}/cmake/Modules/Findbackoffalgorithm.cmake \
"

RDEPENDS:${PN}-ptest = "\
    aws-iot-device-sdk-embedded-c-jobs-demo-mosquitto \
"