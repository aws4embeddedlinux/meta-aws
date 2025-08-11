SUMMARY = "AWS IoT Greengrass Fleet Provisioning Plugin"
DESCRIPTION = "Provides the ability to provision a Greengrass device using AWS FleetProvisioning Service."
HOMEPAGE = "https://github.com/aws-greengrass/aws-greengrass-fleet-provisioning-by-claim"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${WORKDIR}/LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

GGV2_FLEET_PROVISIONING_TEMPLATE_NAME ?= "GreengrassFleetProvisioningTemplate"

PLUGIN_SRC_NAME = "fleetprovisioningbyclaim-${PV}.jar"
PLUGIN_NAME = "aws.greengrass.FleetProvisioningByClaim.jar"

DEPENDS += "gettext-native"

RRECOMMENDS:${PN} += "net-tools"

inherit greengrass-plugin systemd

SRC_URI:append = " \
    https://d2s8p88vqu9w66.cloudfront.net/releases/aws-greengrass-FleetProvisioningByClaim/fleetprovisioningbyclaim-${PV}.jar;name=fleetprovisioning;unpack=0 \
    https://raw.githubusercontent.com/aws-greengrass/aws-greengrass-fleet-provisioning-by-claim/v${PV}/LICENSE;name=license; \
    file://config.yaml.template \
    file://greengrass-plugin-fleetprovisioning.service \
    "

# Fleet provisioning configuration - overwrite in your local config, e.g. IOT_DATA_ENDPOINT:pn-greengrass-plugin-fleetprovisioning = "xxx"
IOT_DATA_ENDPOINT ?= ""
IOT_CRED_ENDPOINT ?= ""
FLEET_PROVISIONING_TEMPLATE ?= ""
CLAIM_CERT_PATH  ?= ""
CLAIM_KEY_PATH ?= ""
ROOT_CA_PATH ?= ""
IOT_ROLE_ALIAS ?= ""
AWS_REGION ?= ""

SRC_URI[fleetprovisioning.sha256sum] = "1e7fdc625d4e1e7795d63f0e97981feecad526277bf211154505de145009e8c1"
SRC_URI[license.sha256sum] = "09e8a9bcec8067104652c168685ab0931e7868f9c8284b66f5ae6edae5f1130b"

do_install:prepend() {

    install -d ${GG_ROOT}/claim-certs
    install -d ${GG_ROOT}/config/

    # Install certificates from specified paths if provided, otherwise warn user
    if [ "${CLAIM_CERT_PATH}" != "" ] && [ "${CLAIM_KEY_PATH}" != "" ] && [ "${ROOT_CA_PATH}" != "" ]; then
        # Install claim certificates from specified external paths
        install -m 0440 ${CLAIM_CERT_PATH} ${GG_ROOT}/claim-certs/claim.cert.pem
        install -m 0440 ${CLAIM_KEY_PATH} ${GG_ROOT}/claim-certs/claim.pkey.pem
        install -m 0440 ${ROOT_CA_PATH} ${GG_ROOT}/claim-certs/claim.root.pem
    else
        bbwarn "CLAIM_CERT_PATH, CLAIM_KEY_PATH, or ROOT_CA_PATH is not set."
        bbwarn "Fleet provisioning certificates will not be installed."
        bbwarn "You will need to provide the certificates manually at /${GG_BASENAME}/claim-certs/"
        bbwarn "Required files: claim.cert.pem, claim.pkey.pem, claim.root.pem"

        # Create empty certificate directory structure for manual installation
        install -d ${GG_ROOT}/claim-certs
    fi

    # Export variables for envsubst
    export AWS_REGION="${AWS_REGION}"
    export IOT_DATA_ENDPOINT="${IOT_DATA_ENDPOINT}"
    export IOT_CRED_ENDPOINT="${IOT_CRED_ENDPOINT}"
    export IOT_ROLE_ALIAS="${IOT_ROLE_ALIAS}"
    export FLEET_PROVISIONING_TEMPLATE="${FLEET_PROVISIONING_TEMPLATE}"

    envsubst < ${WORKDIR}/config.yaml.template > ${WORKDIR}/config.yaml.tmp

    mv ${WORKDIR}/config.yaml.tmp ${WORKDIR}/config.yaml.template

    # Install systemd service file
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${systemd_unitdir}/system/
        install -m 0644 ${WORKDIR}/greengrass-plugin-fleetprovisioning.service ${D}${systemd_unitdir}/system/
    fi

}

FILES:${PN} += "/${GG_BASENAME}/config/ \
                /${GG_BASENAME}/claim-certs/ \
                ${systemd_unitdir}/system/greengrass-plugin-fleetprovisioning.service \
               "

# Systemd service configuration
SYSTEMD_SERVICE:${PN} = "greengrass-plugin-fleetprovisioning.service"
SYSTEMD_AUTO_ENABLE = "enable"

# Watch for changed certificate files to rebuild if they are changed
SSTATE_SCAN_FILES:append = " ${@' ${CLAIM_CERT_PATH} ${CLAIM_KEY_PATH} ${ROOT_CA_PATH}' if (d.getVar('CLAIM_CERT_PATH') and d.getVar('CLAIM_KEY_PATH') and d.getVar('ROOT_CA_PATH')) else ''}"
