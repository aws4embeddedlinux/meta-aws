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

inherit ptest

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests

    install -m 0755 ${S}/../../../utils/parse_cert_set_result.py ${D}${PTEST_PATH}/
}

# nooelint: oelint.vars.insaneskip:INSANE_SKIP
INSANE_SKIP:${PN}-dbg += "buildpaths"

# ptest require credentials to be set before otherwise it will skipped
# this can be done by adding a file aws-iot-device-sdk-cpp-v2-samples-fleet-provisoning.bbappend in a separate layer or uncomment here
# do_install_ptest_append() {
#     install -d ${D}/home/root/.aws
#
#     echo [default] > ${D}/home/root/.aws/credentials
#     echo "aws_access_key_id = XXX" >> ${D}/home/root/.aws/credentials
#     echo "aws_secret_access_key = XXX" >> ${D}/home/root/.aws/credentials
#     echo "region = xxx" >> ${D}/home/root/.aws/credentials
# }