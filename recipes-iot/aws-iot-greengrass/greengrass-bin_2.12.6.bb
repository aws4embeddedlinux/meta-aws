SUMMARY = "AWS IoT Greengrass Nucleus - Binary Distribution"
DESCRIPTION = "The Greengrass nucleus component provides functionality for device side orchestration of deployments and lifecycle management for execution of Greengrass components and applications."
HOMEPAGE = "https://github.com/aws-greengrass/aws-greengrass-nucleus"
LICENSE = "Apache-2.0"

GG_BASENAME = "greengrass/v2"
GG_ROOT = "${D}/${GG_BASENAME}"
# GGV2_FLEETPROVISIONING_VERSION ?= "latest"
GGV2_FLEETPROVISIONING_VERSION ?= "1.2.1"
GGV2_FLEET_PROVISIONING_TEMPLATE_NAME ?= "GreengrassFleetProvisioningTemplate"

LIC_FILES_CHKSUM = "file://${WORKDIR}/LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

DEPENDS += "gettext-native"

# nooelint: oelint.vars.downloadfilename,oelint.vars.srcurichecksum:SRC_URI[payload.md5sum]
SRC_URI = "\
    https://d2s8p88vqu9w66.cloudfront.net/releases/greengrass-${PV}.zip;name=payload; \
    https://raw.githubusercontent.com/aws-greengrass/aws-greengrass-nucleus/main/LICENSE;name=license; \
    file://greengrassv2-init.yaml \
    file://run-ptest \
    "

SRC_URI:append = " ${@bb.utils.contains('PACKAGECONFIG', 'fleetprovisioning', '\
    https://d2s8p88vqu9w66.cloudfront.net/releases/aws-greengrass-FleetProvisioningByClaim/fleetprovisioningbyclaim-${GGV2_FLEETPROVISIONING_VERSION}.jar;unpack=0 \
    file://config.yaml.template \
    file://greengrass.service.diff \
    file://loader.diff \
    file://replace_board_id.sh \
    file://claim.pkey.pem \
    file://claim.cert.pem \
    file://claim.root.pem \
    ', '', d)}"

SRC_URI[payload.sha256sum] = "d48c936aa636197b8af29d374b3ea12ebf8c7a598768403bc11e0a1a397402ad"
SRC_URI[license.sha256sum] = "09e8a9bcec8067104652c168685ab0931e7868f9c8284b66f5ae6edae5f1130b"
SRC_URI[license.md5sum] = "34400b68072d710fecd0a2940a0d1658"
SRC_URI[sha256sum] = "ed4b745420bcf47e354299b2149ef10288a9bc65d5e786b859143157714da5e0"

UPSTREAM_CHECK_REGEX ?= "releases/tag/v?(?P<pver>\d+(\.\d+)+)"

UPSTREAM_CHECK_URI = "https://github.com/aws-greengrass/aws-greengrass-nucleus/tags"

GG_USESYSTEMD = "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'yes', 'no', d)}"

S = "${WORKDIR}"

inherit systemd useradd ptest pkgconfig

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
do_patch[noexec] = "1"

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
    install -m 0640 ${S}/greengrassv2-init.yaml          ${GG_ROOT}/config/config.yaml.clean
    install -m 0640 ${S}/bin/greengrass.service.template ${GG_ROOT}/packages/artifacts-unarchived/aws.greengrass.Nucleus/${PV}/aws.greengrass.nucleus/bin/greengrass.service.template
    install -m 0640 ${S}/bin/loader                      ${GG_ROOT}/packages/artifacts-unarchived/aws.greengrass.Nucleus/${PV}/aws.greengrass.nucleus/bin/loader
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
        install -m 0440 ${WORKDIR}/claim.pkey.pem ${GG_ROOT}/claim-certs
        install -m 0440 ${WORKDIR}/claim.cert.pem ${GG_ROOT}/claim-certs
        install -m 0440 ${WORKDIR}/claim.root.pem ${GG_ROOT}/claim-certs

        install -m 0740 ${WORKDIR}/fleetprovisioningbyclaim-${GGV2_FLEETPROVISIONING_VERSION}.jar ${GG_ROOT}/plugins/trusted/aws.greengrass.FleetProvisioningByClaim.jar

        install -m 0755 ${WORKDIR}/replace_board_id.sh ${GG_ROOT}/config/

        patch ${GG_ROOT}/packages/artifacts-unarchived/aws.greengrass.Nucleus/${PV}/aws.greengrass.nucleus/bin/loader -p1 < ${WORKDIR}/loader.diff
        patch ${D}${systemd_unitdir}/system/greengrass.service -p1 < ${WORKDIR}/greengrass.service.diff

        install -m 0640 ${WORKDIR}/config.yaml.template ${GG_ROOT}/config/config.yaml

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
        envsubst < ${WORKDIR}/config.yaml.template > ${GG_ROOT}/config/config.yaml
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
