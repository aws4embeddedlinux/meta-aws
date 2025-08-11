# oelint-disable: oelint.bbclass.underscores
# Common installation logic for AWS Greengrass (V2 Java / bin) plugins
# Requires:
#   PLUGIN_SRC_NAME  - JAR filename in ${UNPACKDIR}
#   PLUGIN_NAME       - target filename under plugins/trusted

require greengrass-common.inc

inherit deploy

S = "${UNPACKDIR}"

# Use same fragment directory variable as greengrass-component.bbclass for consistency
GG_CONFIG_FRAGMENT_DIR = "greengrass-plugin-fragments"

do_install() {
    # Install plugin jar
    install -d ${GG_ROOT}/plugins
    install -d ${GG_ROOT}/plugins/trusted
    install -m 0755 ${UNPACKDIR}/${PLUGIN_SRC_NAME} \
            ${GG_ROOT}/plugins/trusted/${PLUGIN_NAME}
}

do_deploy() {
    # Deploy fragment for greengrass-bin to merge later
    if [ -e ${UNPACKDIR}/config.yaml.template ]; then
        install -d ${DEPLOYDIR}/${GG_CONFIG_FRAGMENT_DIR}
        cp "${UNPACKDIR}/config.yaml.template" "${DEPLOYDIR}/${GG_CONFIG_FRAGMENT_DIR}/${PLUGIN_NAME}.yaml"
    fi
}
addtask deploy after do_install before do_populate_sysroot

do_deploy[cleandirs] += "${DEPLOYDIR}/${GG_CONFIG_FRAGMENT_DIR}"

# Track template file changes automatically
do_install[file-checksums] += "${@'${UNPACKDIR}/config.yaml.template:True' if os.path.exists('${UNPACKDIR}/config.yaml.template') else ''}"

FILES:${PN} += "/${GG_BASENAME}/plugins/"
