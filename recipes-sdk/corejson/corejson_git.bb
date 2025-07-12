SUMMARY = "A parser strictly enforcing the ECMA-404 JSON standard, suitable for microcontrollers"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7ae2be7fb1637141840314b51970a9f7"

SRC_URI = "\
	gitsm://github.com/FreeRTOS/coreJSON.git;protocol=https;branch=main \
    file://CMakeLists.txt \
    file://Findcore_json.cmake \
	"

SRCREV = "40244174a1a71be54a7122b941d68f9298f2d67c"

inherit cmake

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
