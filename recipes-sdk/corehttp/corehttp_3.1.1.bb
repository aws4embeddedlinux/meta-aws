SUMMARY = "Client implementation of a subset of HTTP 1.1 protocol"
DESCRIPTION = "Client implementation of a subset of HTTP 1.1 protocol designed for embedded devices."
HOMEPAGE = "https://github.com/FreeRTOS/coreHTTP"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7ae2be7fb1637141840314b51970a9f7 \
                    file://source/dependency/3rdparty/llhttp/LICENSE-MIT;md5=f5e274d60596dd59be0a1d1b19af7978"

SRC_URI = "\
    gitsm://github.com/FreeRTOS/coreHTTP.git;protocol=https;branch=main \
    file://CMakeLists.txt \
    file://Findcore_http.cmake \
    file://run-ptest \
"

SRCREV = "169c2879589dee06fce4a4f9803924f76131f483"

inherit cmake ptest

EXTRA_OECMAKE += "-DCMAKE_POLICY_VERSION_MINIMUM=3.5"

EXTRA_OECMAKE:append = " \
    -DLIB_VERSION=${PV} \
    -DLIB_SOVERSION=${@d.getVar('PV').split('.')[0]} \
"

OECMAKE_C_FLAGS:append = " -DHTTP_DO_NOT_USE_CUSTOM_CONFIG=ON"

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

RDEPENDS:${PN}-ptest = "\
    aws-iot-device-sdk-embedded-c-http-demo-basic-tls \
    coreutils \
"

# nooelint: oelint.vars.insaneskip:INSANE_SKIP
INSANE_SKIP:${PN} += "buildpaths"
