SUMMARY = "AWS IoT Device SDK for Embedded C - reference implentation"
DESCRIPTION = "SDK for connecting to AWS IoT from a device using embedded C - a reference implentation, using standalone versions of contained libs is the preferred way to consoume it"
HOMEPAGE = "https://github.com/aws/aws-iot-device-sdk-embedded-C"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c8c19afab7f99fb196c9287cbd60a258 "

DEPENDS = "\
    ${@bb.utils.contains('PACKAGECONFIG', 'with-demos', 'mosquitto', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'with-demos', 'openssl', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'with-tests', 'ruby-native', '', d)} \
    "

SRC_URI = "\
    gitsm://github.com/aws/aws-iot-device-sdk-embedded-C.git;protocol=https;branch=main \
    file://run-ptest \
    "

SRCREV = "da99638ec373c791a45557b0cd91fc20968d492d"

inherit cmake ptest pkgconfig

PACKAGECONFIG ??= "\
    with-demos \
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'with-tests','', d)} \
    "

EXTRA_OECMAKE += "-DCMAKE_POLICY_VERSION_MINIMUM=3.5"

EXTRA_OECMAKE += "\
    -DAWS_IOT_ENDPOINT=${AWS_IOT_ENDPOINT} \
    -DBROKER_ENDPOINT=${BROKER_ENDPOINT} \
    -DCLIENT_CERT_PATH=${CLIENT_CERT_PATH} \
    -DCLIENT_PRIVATE_KEY_PATH=${CLIENT_PRIVATE_KEY_PATH} \
    -DINSTALL_TO_SYSTEM=1 \
    -DROOT_CA_CERT_PATH=${ROOT_CA_CERT_PATH} \
    -DSERVER_HOST=${SERVER_HOST} \
    -DTHING_NAME=${THING_NAME} \
    "

do_install () {
    install -d ${D}${libdir}
    cp -r ${B}/lib/* ${D}${libdir}
    ${@bb.utils.contains('PACKAGECONFIG', 'with-demos', 'install -d ${D}${bindir}', '', d)}
    ${@bb.utils.contains('PACKAGECONFIG', 'with-demos', 'cp -r ${B}/bin/* ${D}${bindir}', '', d)}
}

do_install_ptest:append () {
   install -d ${D}${PTEST_PATH}/tests
   mv ${D}${bindir}/tests ${D}${PTEST_PATH}/
   mv ${D}${bindir}/certificates ${D}${PTEST_PATH}/
}

PACKAGECONFIG[with-demos] = "-DBUILD_DEMOS=ON ,-DBUILD_DEMOS=OFF ,"
PACKAGECONFIG[with-tests] = "-DBUILD_TESTS=ON ,-DBUILD_TESTS=OFF ,"

# nooelint: oelint.vars.insaneskip:INSANE_SKIP
INSANE_SKIP:${PN} += "buildpaths"

# nooelint: oelint.vars.insaneskip:INSANE_SKIP
INSANE_SKIP:${PN}-src += "buildpaths"

# nooelint: oelint.vars.insaneskip:INSANE_SKIP
INSANE_SKIP:${PN}-dev += "dev-elf file-rdeps"

# nooelint: oelint.vars.insaneskip:INSANE_SKIP
INSANE_SKIP:${PN}-ptest += "buildpaths rpaths"

# those variables should be overwritten in your local.conf
# e.g. AWS_IOT_ENDPOINT:pn-aws-iot-device-sdk-embedded-c = ""
AWS_IOT_ENDPOINT ?= "your-iot-endpoint"
BROKER_ENDPOINT ?= "localhost"
CLIENT_CERT_PATH ?= "${sysconfdir}"
CLIENT_PRIVATE_KEY_PATH ?= "${sysconfdir}"
ROOT_CA_CERT_PATH ?= "${sysconfdir}/ssl/certs/ca-certificates.crt"
SERVER_HOST ?= "google.com"
THING_NAME ?= "your-registered-thing-name"
