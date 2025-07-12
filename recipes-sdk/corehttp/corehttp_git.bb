DESCRIPTION = "Client implementation of a subset of HTTP 1.1 protocol designed for embedded devices."
WEBSITE = "https://github.com/FreeRTOS/coreHTTP"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7ae2be7fb1637141840314b51970a9f7 \
                    file://source/dependency/3rdparty/llhttp/LICENSE-MIT;md5=f5e274d60596dd59be0a1d1b19af7978"

SRC_URI = "\
	gitsm://github.com/FreeRTOS/coreHTTP.git;protocol=https;branch=main \
    file://CMakeLists.txt \
    file://Findcore_http.cmake \
"

SRCREV = "c08b1e1d27f1157d51ee9179d67645c43f05718e"

inherit cmake

EXTRA_OECMAKE:append = " -DCMAKE_C_FLAGS=-DHTTP_DO_NOT_USE_CUSTOM_CONFIG=ON"

do_configure:prepend() {
    cp ${UNPACKDIR}/CMakeLists.txt ${S}/
}

do_install:append() {
    install -d ${D}${datadir}/cmake/Modules
    install -m 0644 ${UNPACKDIR}/Findcore_http.cmake ${D}${datadir}/cmake/Modules/
    install ${S}/source/interface/transport_interface.h ${D}${includedir}/core_http/
}

FILES:${PN} += "${libdir}/libcore_http.so.*"

FILES:${PN}-dev += "\
    ${libdir}/libcore_http.so \
    ${includedir}/core_http/* \
    ${datadir}/cmake/Modules/Findcore_http.cmake \
"

# nooelint: oelint.vars.insaneskip:INSANE_SKIP
INSANE_SKIP:${PN} += "buildpaths"
