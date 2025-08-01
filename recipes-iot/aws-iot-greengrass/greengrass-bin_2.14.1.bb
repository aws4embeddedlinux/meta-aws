SUMMARY = "AWS IoT Greengrass Nucleus - Binary Distribution"
DESCRIPTION = "The Greengrass nucleus component provides functionality for device side orchestration of deployments and lifecycle management for execution of Greengrass components and applications."
HOMEPAGE = "https://github.com/aws-greengrass/aws-greengrass-nucleus"
LICENSE = "Apache-2.0"

require classes/greengrass-common.inc

LIC_FILES_CHKSUM = "file://${UNPACKDIR}/greengrass-bin/LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

DEPENDS += "gettext-native"

# enable fleetprovisioning for testing by default to get test coverage
PACKAGECONFIG ??= "${@bb.utils.contains('PTEST_ENABLED', '1', 'fleetprovisioning', '', d)}"

PACKAGECONFIG[fleetprovisioning] = ",,greengrass-plugin-fleetprovisioning,greengrass-plugin-fleetprovisioning"
PACKAGECONFIG[pkcs11] = ",,greengrass-plugin-pkcs11,greengrass-plugin-pkcs11"

SRC_URI = "\
    https://d2s8p88vqu9w66.cloudfront.net/releases/greengrass-${PV}.zip;subdir=greengrass-bin \
    file://greengrassv2-init.yaml \
    file://run-ptest \
    file://config.yaml.template \
    "

SRC_URI:append = " ${@bb.utils.contains('PACKAGECONFIG', 'fleetprovisioning', '\
    file://loader.patch \
    file://greengrass.service.patch \
    ', '', d)}"

SRC_URI[sha256sum] = "a7cbc3cee5d245bfac9c49a036a482884898edbeb2f1e6fb27d17e9321007ce8"
UPSTREAM_CHECK_REGEX ?= "releases/tag/v?(?P<pver>\d+(\.\d+)+)"

UPSTREAM_CHECK_URI = "https://github.com/aws-greengrass/aws-greengrass-nucleus/tags"

GG_USESYSTEMD = "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'yes', 'no', d)}"

inherit systemd useradd ptest pkgconfig
DEPENDS:append = " yq-native"

S = "${UNPACKDIR}/greengrass-bin"

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
    install -m 0640 ${S}/../greengrassv2-init.yaml       ${GG_ROOT}/config/config.yaml.clean
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

    # Install base config.yml
    AWS_DEFAULT_REGION=${GGV2_REGION} \
    PROXY_USER=ggc_user:ggc_group \
    envsubst < ${UNPACKDIR}/config.yaml.template > ${GG_ROOT}/config/config.yaml
}

do_merge_config() {
    # Merge config fragments
    yq eval-all '. as $item ireduce ({}; . * $item)' \
                "${GG_ROOT}/config/config.yaml" \
                "${DEPLOY_DIR_IMAGE}/greengrass-plugin-fragments/"*.yaml \
                > "${GG_ROOT}/config/config.yaml.tmp"

    mv "${GG_ROOT}/config/config.yaml.tmp" "${GG_ROOT}/config/config.yaml"

}
addtask merge_config after do_install before do_package

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
