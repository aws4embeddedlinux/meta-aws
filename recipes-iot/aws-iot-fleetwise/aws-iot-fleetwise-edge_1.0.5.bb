SUMMARY = "AWS IoT FleetWise Edge Agent"
DESCRIPTION = "AWS IoT FleetWise is a service that makes it easy for Automotive OEMs, Fleet operators, Independent Software vendors (ISVs) to collect, store, organize, and monitor data from vehicles at scale."
HOMEPAGE = "https://github.com/aws/aws-iot-fleetwise-edge"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

# nooelint: oelint.vars.dependsordered
DEPENDS = "\
    aws-sdk-cpp \
    boost \
    jsoncpp \
    protobuf \
    protobuf-native \
    snappy \
    "

# nooelint: oelint.file.patchsignedoff
SRC_URI = "\
           git://github.com/aws/aws-iot-fleetwise-edge.git;protocol=https;branch=main \
           file://GCC13.patch \
           file://run-ptest \
           "

SRCREV = "7aae74008aa3bb1c8b30111fc82c61a3d1622952"

S = "${WORKDIR}/git"

inherit cmake systemd ptest

FILES:${PN} += "${systemd_system_unitdir}"

RDEPENDS:${PN} = "protobuf"

EXTRA_OECMAKE += "-DBUILD_TESTING=OFF"

EXTRA_OECMAKE += "-DFWE_AWS_SDK_SHARED_LIBS=ON"

SYSTEMD_SERVICE:${PN} = "fwe@0.service"
do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${B}/src/executionmanagement/aws-iot-fleetwise-edge ${D}${bindir}
    install -d ${D}${systemd_system_unitdir}
    install -m 0755 ${S}/tools/deploy/fwe@.service ${D}${systemd_system_unitdir}
    install -d ${D}${sysconfdir}/aws-iot-fleetwise
    install -m 0755 ${S}/configuration/static-config.json ${D}${systemd_system_unitdir}/config-0.json
    install -d ${D}${localstatedir}/aws-iot-fleetwise
}

do_install_ptest() {
    install -m 0755 ${S}/configuration/static-config.json ${D}${PTEST_PATH}/config-0.json
}
