DESCRIPTION = "AWS IoT FleetWise Edge Agent" 
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"
DEPENDS = "protobuf protobuf-native boost jsoncpp aws-sdk-cpp snappy"
RDEPENDS:${PN} = "protobuf"
SRC_URI = "git://github.com/aws/aws-iot-fleetwise-edge.git;protocol=https;branch=main;name=fwe \
           git://github.com/hartkopp/can-isotp.git;protocol=https;name=isotp;destsuffix=isotp \
           "
SRCREV_fwe = "f03cc0048e65999e723bd92d5c885d8c83719ffd"
SRCREV_isotp = "beb4650660179963a8ed5b5cbf2085cc1b34f608"
S = "${WORKDIR}/git"
EXTRA_OECMAKE += "-DBUILD_TESTING=OFF"
EXTRA_OECMAKE += "-DFWE_AWS_SDK_SHARED_LIBS=ON"
# Bring in <linux/can/isotp.h> ((GPL-2.0 WITH Linux-syscall-note) OR BSD-3-Clause) independently of the kernel headers
CXXFLAGS += "-I${WORKDIR}/isotp/include/uapi"
inherit cmake systemd
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
FILES:${PN} += "${systemd_system_unitdir}"
