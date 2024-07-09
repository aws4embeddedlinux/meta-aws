# nooelint: oelint.file.underscores
require aws-iot-device-sdk-cpp-v2-samples.inc

S = "${WORKDIR}/git/samples/fleet_provisioning/fleet_provisioning"

SRC_URI:append = " \
    file://run-ptest \
"

RDEPENDS:${PN}-ptest += "python3 aws-cli"

do_install() {
 install -d ${D}${bindir}
 install ${B}/fleet-provisioning ${D}${bindir}
}

inherit ptest pkgconfig

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests

    install -m 0755 ${S}/../../../utils/parse_cert_set_result.py ${D}${PTEST_PATH}/

    install -m 0755 ${WORKDIR}/run-ptest ${D}${PTEST_PATH}/
}
