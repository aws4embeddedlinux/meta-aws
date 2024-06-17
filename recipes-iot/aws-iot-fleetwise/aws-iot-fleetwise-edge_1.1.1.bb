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
    jq-native \
    "

# nooelint: oelint.file.patchsignedoff
SRC_URI = "\
           git://github.com/aws/aws-iot-fleetwise-edge.git;protocol=https;branch=main \
           file://remove-cxx-standard.patch \
           file://boost_filesystem_extension.patch \
           file://run-ptest \
           "

SRCREV = "f4e85953bac2f99ed428ad69379e6c788ca10f78"

S = "${WORKDIR}/git"

inherit cmake systemd ptest

FILES:${PN} += "${systemd_system_unitdir}"

RDEPENDS:${PN} = "protobuf"

EXTRA_OECMAKE += "-DBUILD_TESTING=OFF"

EXTRA_OECMAKE += "-DFWE_AWS_SDK_SHARED_LIBS=ON"

SYSTEMD_SERVICE:${PN} = "fwe@.service"

# Default values for configure-fwe
CERTIFICATE_FILE ?= "/etc/aws-iot-fleetwise/certificate.pem"
PRIVATE_KEY_FILE ?= "/etc/aws-iot-fleetwise/private-key.key"
LOG_LEVEL ?= "Info"
LOG_COLOR ?= "Auto"
PERSISTENCY_PATH ?= "/var/aws-iot-fleetwise/"
TOPIC_PREFIX ?= "\$aws/iotfleetwise/"
CONNECTION_TYPE ?= "iotCore"

# Non-default parameters needed to be configured
CERTIFICATE ?= "-----BEGIN-----\nXXXXX\n-----END-----\n"
PRIVATE_KEY ?= "-----BEGIN-----\nXXXXX\n-----END-----\n"
VEHICLE_NAME ?= "v1"
ENDPOINT_URL ?= "xxx.iot.region.amazonaws.com"
CAN_BUS ?= "vcan0"

do_configure:append() {

    # Execute the script with arguments to generate the file
    ${S}/tools/configure-fwe.sh \
        --input-config-file ${S}/configuration/static-config.json \
        --output-config-file ${WORKDIR}/config-0.json \
        --connection-type ${CONNECTION_TYPE} \
        --vehicle-name ${VEHICLE_NAME} \
        --endpoint-url ${ENDPOINT_URL} \
        --can-bus0 ${CAN_BUS} \
        --certificate-file ${CERTIFICATE_FILE} \
        --certificate ${CERTIFICATE} \
        --private-key ${PRIVATE_KEY} \
        --private-key-file ${PRIVATE_KEY_FILE} \
        --persistency-path ${PERSISTENCY_PATH}  \
        --topic-prefix ${TOPIC_PREFIX} \
        --log-level ${LOG_LEVEL} \
        --log-color ${LOG_COLOR}
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${B}/aws-iot-fleetwise-edge ${D}${bindir}
    install -m 0755 ${S}/tools/deploy/run-fwe.sh ${D}${bindir}
    install -d ${D}${systemd_system_unitdir}
    install -m 0755 ${S}/tools/deploy/fwe@.service ${D}${systemd_system_unitdir}
    install -d ${D}${sysconfdir}/aws-iot-fleetwise
    install -m 0755 ${WORKDIR}/config-0.json ${D}${sysconfdir}/aws-iot-fleetwise
    install -d ${D}${localstatedir}/aws-iot-fleetwise
}

do_install_ptest() {
    install -m 0755 ${S}/configuration/static-config.json ${D}${PTEST_PATH}/config-0.json
}
