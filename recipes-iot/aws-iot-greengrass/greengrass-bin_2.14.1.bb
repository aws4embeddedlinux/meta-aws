SUMMARY = "AWS IoT Greengrass Nucleus - Binary Distribution"
DESCRIPTION = "The Greengrass nucleus component provides functionality for device side orchestration of deployments and lifecycle management for execution of Greengrass components and applications."
HOMEPAGE = "https://github.com/aws-greengrass/aws-greengrass-nucleus"
LICENSE = "Apache-2.0"

GG_BASENAME = "greengrass/v2"
GG_ROOT = "${D}/${GG_BASENAME}"
# GGV2_FLEETPROVISIONING_VERSION ?= "latest"
GGV2_FLEETPROVISIONING_VERSION ?= "1.2.2"
GGV2_FLEET_PROVISIONING_TEMPLATE_NAME ?= "GreengrassFleetProvisioningTemplate"

LIC_FILES_CHKSUM = "file://${WORKDIR}/greengrass-bin/LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

DEPENDS += "gettext-native"

# enable fleetprovisioning for testing by default to get test coverage
PACKAGECONFIG ??= "${@bb.utils.contains('PTEST_ENABLED', '1', 'fleetprovisioning', '', d)}"

# this is to make the PACKAGECONFIG QA check happy
PACKAGECONFIG[fleetprovisioning] = ""

SRC_URI = "\
    https://d2s8p88vqu9w66.cloudfront.net/releases/greengrass-${PV}.zip;subdir=greengrass-bin \
    file://greengrassv2-init.yaml \
    file://run-ptest \
    "

SRC_URI:append = " ${@bb.utils.contains('PACKAGECONFIG', 'fleetprovisioning', '\
    https://d2s8p88vqu9w66.cloudfront.net/releases/aws-greengrass-FleetProvisioningByClaim/fleetprovisioningbyclaim-${GGV2_FLEETPROVISIONING_VERSION}.jar;name=fleetprovisioning;unpack=0 \
    file://config.yaml.template \
    file://replace_board_id.sh \
    file://claim.pkey.pem \
    file://claim.cert.pem \
    file://claim.root.pem \
    file://loader.patch \
    file://greengrass.service.patch \
    ', '', d)}"

SRC_URI[sha256sum] = "a7cbc3cee5d245bfac9c49a036a482884898edbeb2f1e6fb27d17e9321007ce8"
SRC_URI[fleetprovisioning.sha256sum] = "1e7fdc625d4e1e7795d63f0e97981feecad526277bf211154505de145009e8c1"
UPSTREAM_CHECK_REGEX ?= "releases/tag/v?(?P<pver>\d+(\.\d+)+)"

UPSTREAM_CHECK_URI = "https://github.com/aws-greengrass/aws-greengrass-nucleus/tags"

GG_USESYSTEMD = "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'yes', 'no', d)}"

inherit systemd useradd ptest pkgconfig

S = "${WORKDIR}/greengrass-bin"

FILES:${PN} += "\
    /${GG_BASENAME} \
    ${systemd_unitdir} \
    "

RDEPENDS:${PN} += "\
    ca-certificates \
    java-11 \
    python3-core \
    python3-json \
    python3-numbers \
    sudo \
    "

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${GG_ROOT}/config
    install -d ${GG_ROOT}/alts
    install -d ${GG_ROOT}/alts/init
    install -d ${GG_ROOT}/packages/artifacts-unarchived/aws.greengrass.Nucleus/${PV}/aws.greengrass.nucleus
    install -d ${GG_ROOT}/packages/artifacts-unarchived/aws.greengrass.Nucleus/${PV}/aws.greengrass.nucleus/bin
    install -d ${GG_ROOT}/packages/artifacts-unarchived/aws.greengrass.Nucleus/${PV}/aws.greengrass.nucleus/conf
    install -d ${GG_ROOT}/packages/artifacts-unarchived/aws.greengrass.Nucleus/${PV}/aws.greengrass.nucleus/lib
    ln -s /${GG_BASENAME}/packages/artifacts-unarchived/aws.greengrass.Nucleus/${PV}/aws.greengrass.nucleus ${GG_ROOT}/alts/init/distro

    install -m 0440 ${S}/LICENSE                         ${GG_ROOT}
    install -m 0640 ${UNPACKDIR}/greengrassv2-init.yaml          ${GG_ROOT}/config/config.yaml.clean
    install -m 0640 ${S}/bin/greengrass.service.template ${GG_ROOT}/packages/artifacts-unarchived/aws.greengrass.Nucleus/${PV}/aws.greengrass.nucleus/bin/greengrass.service.template
    install -m 0750 ${S}/bin/loader                      ${GG_ROOT}/packages/artifacts-unarchived/aws.greengrass.Nucleus/${PV}/aws.greengrass.nucleus/bin/loader
    install -m 0640 ${S}/conf/recipe.yaml                ${GG_ROOT}/packages/artifacts-unarchived/aws.greengrass.Nucleus/${PV}/aws.greengrass.nucleus/conf/recipe.yaml
    install -m 0740 ${S}/lib/Greengrass.jar              ${GG_ROOT}/packages/artifacts-unarchived/aws.greengrass.Nucleus/${PV}/aws.greengrass.nucleus/lib/Greengrass.jar

    ln -s /${GG_BASENAME}/alts/init ${GG_ROOT}/alts/current

    # Install systemd service file
    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${S}/bin/greengrass.service.template ${D}${systemd_unitdir}/system/greengrass.service
    sed -i -e "s,REPLACE_WITH_GG_LOADER_FILE,/${GG_BASENAME}/alts/current/distro/bin/loader,g" ${D}${systemd_unitdir}/system/greengrass.service
    sed -i -e "s,REPLACE_WITH_GG_LOADER_PID_FILE,/var/run/greengrass.pid,g" ${D}${systemd_unitdir}/system/greengrass.service

    if ${@bb.utils.contains('PACKAGECONFIG','fleetprovisioning','true','false',d)}; then

        install -d ${GG_ROOT}/claim-certs
        install -d ${GG_ROOT}/plugins
        install -d ${GG_ROOT}/plugins/trusted
        install -m 0440 ${UNPACKDIR}/claim.pkey.pem ${GG_ROOT}/claim-certs
        install -m 0440 ${UNPACKDIR}/claim.cert.pem ${GG_ROOT}/claim-certs
        install -m 0440 ${UNPACKDIR}/claim.root.pem ${GG_ROOT}/claim-certs

        install -m 0740 ${UNPACKDIR}/fleetprovisioningbyclaim-${GGV2_FLEETPROVISIONING_VERSION}.jar ${GG_ROOT}/plugins/trusted/aws.greengrass.FleetProvisioningByClaim.jar

        install -m 0755 ${UNPACKDIR}/replace_board_id.sh ${GG_ROOT}/config/

        install -m 0640 ${UNPACKDIR}/config.yaml.template ${GG_ROOT}/config/config.yaml

        AWS_DEFAULT_REGION=${GGV2_REGION} \
        PROXY_USER=ggc_user:ggc_group \
        IOT_DATA_ENDPOINT=${GGV2_DATA_EP} \
        IOT_CRED_ENDPOINT=${GGV2_CRED_EP} \
        TE_ROLE_ALIAS=${GGV2_TES_RALIAS} \
        FLEET_PROVISIONING_TEMPLATE_NAME=${GGV2_FLEET_PROVISIONING_TEMPLATE_NAME} \
        CLAIM_CERT_PATH=/${GG_BASENAME}/claim-certs/claim.cert.pem \
        CLAIM_KEY_PATH=/${GG_BASENAME}/claim-certs/claim.pkey.pem \
        ROOT_CA_PATH=/${GG_BASENAME}/claim-certs/claim.root.pem \
        THING_NAME=${GGV2_THING_NAME} \
        THING_GROUP_NAME=${GGV2_THING_GROUP} \
        envsubst < ${UNPACKDIR}/config.yaml.template > ${GG_ROOT}/config/config.yaml
    fi
}

CONFFILES:${PN} += "/${GG_BASENAME}/config/config.yaml.clean"

SYSTEMD_AUTO_ENABLE = "enable"
SYSTEMD_SERVICE:${PN} = "greengrass.service"

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "-r ggc_group"
USERADD_PARAM:${PN} = "-r -M -N -g ggc_group -s /bin/false ggc_user"
GROUP_MEMS_PARAM:${PN} = ""

#
# Disable failing QA checks:
#
#   Binary was already stripped
#   No GNU_HASH in the elf binary
#
# nooelint: oelint.vars.insaneskip
INSANE_SKIP:${PN} += "already-stripped ldflags file-rdeps"

RDEPENDS:${PN}-ptest += "\
    greengrass-bin \
    "
