SUMMARY = "AWS IoT Greengrass Fleet Provisioning Plugin"
DESCRIPTION = "Provides the ability to provision a Greengrass device using AWS FleetProvisioning Service."
HOMEPAGE = "https://github.com/aws-greengrass/aws-greengrass-fleet-provisioning-by-claim"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${UNPACKDIR}/LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

GGV2_FLEET_PROVISIONING_TEMPLATE_NAME ?= "GreengrassFleetProvisioningTemplate"

PLUGIN_SRC_NAME = "fleetprovisioningbyclaim-${PV}.jar"
PLUGIN_NAME = "aws.greengrass.FleetProvisioningByClaim.jar"

DEPENDS += "gettext-native"

RRECOMMENDS:${PN} += "net-tools"

inherit greengrass-plugin

SRC_URI:append = " \
    https://d2s8p88vqu9w66.cloudfront.net/releases/aws-greengrass-FleetProvisioningByClaim/fleetprovisioningbyclaim-${PV}.jar;name=fleetprovisioning;unpack=0 \
    https://raw.githubusercontent.com/aws-greengrass/aws-greengrass-fleet-provisioning-by-claim/v${PV}/LICENSE;name=license; \
    file://config.yaml.template \
    file://replace_board_id.sh \
    file://claim.pkey.pem \
    file://claim.cert.pem \
    file://claim.root.pem \
    "

SRC_URI[fleetprovisioning.sha256sum] = "1e7fdc625d4e1e7795d63f0e97981feecad526277bf211154505de145009e8c1"
SRC_URI[license.sha256sum] = "09e8a9bcec8067104652c168685ab0931e7868f9c8284b66f5ae6edae5f1130b"

do_install:prepend() {

    install -d ${GG_ROOT}/claim-certs
    install -d ${GG_ROOT}/config/

    install -m 0440 ${UNPACKDIR}/claim.pkey.pem ${GG_ROOT}/claim-certs
    install -m 0440 ${UNPACKDIR}/claim.cert.pem ${GG_ROOT}/claim-certs
    install -m 0440 ${UNPACKDIR}/claim.root.pem ${GG_ROOT}/claim-certs
    install -m 0755 ${UNPACKDIR}/replace_board_id.sh ${GG_ROOT}/config/

    AWS_DEFAULT_REGION=${GGV2_REGION} \
    IOT_DATA_ENDPOINT=${GGV2_DATA_EP} \
    IOT_CRED_ENDPOINT=${GGV2_CRED_EP} \
    TE_ROLE_ALIAS=${GGV2_TES_RALIAS} \
    FLEET_PROVISIONING_TEMPLATE_NAME=${GGV2_FLEET_PROVISIONING_TEMPLATE_NAME} \
    CLAIM_CERT_PATH=/${GG_BASENAME}/claim-certs/claim.cert.pem \
    CLAIM_KEY_PATH=/${GG_BASENAME}/claim-certs/claim.pkey.pem \
    ROOT_CA_PATH=/${GG_BASENAME}/claim-certs/claim.root.pem \
    THING_NAME=${GGV2_THING_NAME} \
    THING_GROUP_NAME=${GGV2_THING_GROUP} \
    envsubst < ${UNPACKDIR}/config.yaml.template > ${UNPACKDIR}/config.yaml.tmp

    mv ${UNPACKDIR}/config.yaml.tmp ${UNPACKDIR}/config.yaml.template

}

FILES:${PN} += "/${GG_BASENAME}/config/ \
                /${GG_BASENAME}/claim-certs/ \
               "
