LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7ae2be7fb1637141840314b51970a9f7"

SRC_URI = "gitsm://github.com/FreeRTOS/backoffAlgorithm.git;protocol=https;branch=main \
    file://CMakeLists.txt \
    file://Findbackoffalgorithm.cmake \
"

SRCREV = "50e30d7d0aa0aa74e85134088a86f91978267d41"

DEPENDS = "cmake-native ruby-native"

inherit cmake

do_configure:prepend() {
    cp ${UNPACKDIR}/CMakeLists.txt ${S}/
}

do_install:append() {
    install -d ${D}${datadir}/cmake/Modules
    install -m 0644 ${UNPACKDIR}/Findbackoffalgorithm.cmake ${D}${datadir}/cmake/Modules/
}

FILES:${PN} += "${libdir}/libbackoffalgorithm.so.*"
FILES:${PN}-dev += "\
    ${libdir}/libbackoffalgorithm.so \
    ${includedir}/backoffalgorithm/* \
    ${datadir}/cmake/Modules/Findbackoffalgorithm.cmake \
"
