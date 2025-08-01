# Common installation logic for AWS Greengrass plugins
# Requires:
#   PLUGIN_SRC_NAME  - JAR filename in ${UNPACKDIR}
#   PLUGIN_NAME       - target filename under plugins/trusted

require greengrass-common.inc

inherit deploy

S = "${UNPACKDIR}"

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
        install -d ${DEPLOYDIR}/greengrass-plugin-fragments
        cp "${UNPACKDIR}/config.yaml.template" "${DEPLOYDIR}/greengrass-plugin-fragments/${PLUGIN_NAME}.yaml"
    fi
}
addtask deploy after do_install before do_populate_sysroot

FILES:${PN} += "/${GG_BASENAME}/plugins/ \
               "
