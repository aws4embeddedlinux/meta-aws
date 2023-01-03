# -*- mode: Conf; -*-
SUMMARY     = "AWS IoT Greengrass Nucleus - Binary Distribution"
DESCRIPTION = ""
LICENSE     = "Apache-2.0"

S                          = "${WORKDIR}"
GG_BASENAME                = "greengrass/v2"
GG_ROOT                    = "${D}/${GG_BASENAME}"
LIC_FILES_CHKSUM           = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"
SRC_URI                    = "https://d2s8p88vqu9w66.cloudfront.net/releases/greengrass-${PV}.zip;name=payload; \
                              https://raw.githubusercontent.com/aws-greengrass/aws-greengrass-nucleus/main/LICENSE;name=license; \
                              file://greengrassv2-init.yaml \
                              "
SRC_URI[payload.sha256sum] = "976a5c135d78de9e614750c4abfc13f91a122c43b088a6ec056026c8c08093b7"

SRC_URI[license.sha256sum] = "09e8a9bcec8067104652c168685ab0931e7868f9c8284b66f5ae6edae5f1130b"

# also available in master (not kirkstone) in classes-recipe: github-releases
UPSTREAM_CHECK_REGEX ?= "releases/tag/v?(?P<pver>\d+(\.\d+)+)"

UPSTREAM_CHECK_URI = "https://github.com/aws-greengrass/aws-greengrass-nucleus/tags"


GG_USESYSTEMD = "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'yes', 'no', d)}"
RDEPENDS:${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'ntp-systemd', '', d)}"
RDEPENDS:${PN} += "corretto-11-bin ca-certificates python3-core python3-json python3-numbers sudo"

do_configure[noexec] = "1"
do_compile[noexec]   = "1"

do_install() {
    install -d ${GG_ROOT}/config
    install -d ${GG_ROOT}/alts
    install -d ${GG_ROOT}/alts/init
    install -d ${GG_ROOT}/alts/init/distro
    install -d ${GG_ROOT}/alts/init/distro/bin
    install -d ${GG_ROOT}/alts/init/distro/conf
    install -d ${GG_ROOT}/alts/init/distro/lib
    
    install -m 0440 ${WORKDIR}/LICENSE                         ${GG_ROOT}
    install -m 0640 ${WORKDIR}/greengrassv2-init.yaml          ${GG_ROOT}/config/config.yaml.clean
    install -m 0640 ${WORKDIR}/bin/greengrass.service.template ${GG_ROOT}/alts/init/distro/bin/greengrass.service.template
    install -m 0640 ${WORKDIR}/bin/loader                      ${GG_ROOT}/alts/init/distro/bin/loader
    install -m 0640 ${WORKDIR}/conf/recipe.yaml                ${GG_ROOT}/alts/init/distro/conf/recipe.yaml
    install -m 0740 ${WORKDIR}/lib/Greengrass.jar              ${GG_ROOT}/alts/init/distro/lib/Greengrass.jar

    ln -s /${GG_ROOT}/alts/init /${GG_ROOT}/alts/current
    
    # Install systemd service file
    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${WORKDIR}/bin/greengrass.service.template ${D}${systemd_unitdir}/system/greengrass.service
    sed -i -e "s,REPLACE_WITH_GG_LOADER_FILE,/${GG_BASENAME}/alts/current/distro/bin/loader,g" ${D}${systemd_unitdir}/system/greengrass.service
    sed -i -e "s,REPLACE_WITH_GG_LOADER_PID_FILE,/var/run/greengrass.pid,g" ${D}${systemd_unitdir}/system/greengrass.service
}

FILES:${PN} = "/${GG_BASENAME} \
               ${sysconfdir} \
               ${systemd_unitdir}"

CONFFILES:${PN} += "/${GG_BASENAME}/config/config.yaml.clean"

inherit systemd
SYSTEMD_AUTO_ENABLE = "enable"
SYSTEMD_SERVICE:${PN} = "greengrass.service"

inherit useradd

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
INSANE_SKIP:${PN} += "already-stripped ldflags file-rdeps"

