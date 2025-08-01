# Common installation logic for AWS Greengrass v2 components
#
# This bbclass is designed for modern Greengrass v2 components (Python scripts,
# binaries, etc.) that run as separate processes managed by Greengrass.
# For Java-based plugins that load into the Greengrass nucleus JVM, use
# greengrass-plugin.bbclass instead.
#
# Required variables:
#   COMPONENT_NAME     - Component name (e.g., "com.example.HelloWorld")
#
# Optional variables:
#   COMPONENT_VERSION  - Component version (defaults to "1.0.0")
#   COMPONENT_ARTIFACTS - List of artifacts to install (defaults to all files in SRC_URI except config.yaml.template)
#
# Expected files in SRC_URI:
#   - Component artifacts (scripts, binaries, etc.)
#   - config.yaml.template (optional, for component configuration)
#
# Example usage:
#   COMPONENT_NAME = "com.example.HelloWorld"
#   COMPONENT_VERSION = "1.0.0"
#   COMPONENT_ARTIFACTS = "hello_world.py"
#   inherit greengrass-component

require greengrass-common.inc

inherit deploy

S = "${UNPACKDIR}"

# Default component version if not specified
COMPONENT_VERSION ??= "1.0.0"

do_install() {
    # Create component directory structure
    install -d ${GG_ROOT}/components/${COMPONENT_NAME}/${COMPONENT_VERSION}

    # Install component artifacts
    if [ -n "${COMPONENT_ARTIFACTS}" ]; then
        # Install specific artifacts if defined
        for artifact in ${COMPONENT_ARTIFACTS}; do
            if [ -f ${UNPACKDIR}/${artifact} ]; then
                install -m 0755 ${UNPACKDIR}/${artifact} ${GG_ROOT}/components/${COMPONENT_NAME}/${COMPONENT_VERSION}/
            else
                bbwarn "Component artifact ${artifact} not found in ${UNPACKDIR}"
            fi
        done
    else
        # Install all files from SRC_URI (excluding config.yaml.template)
        for file in ${UNPACKDIR}/*; do
            if [ -f "$file" ] && [ "$(basename "$file")" != "config.yaml.template" ]; then
                install -m 0755 "$file" ${GG_ROOT}/components/${COMPONENT_NAME}/${COMPONENT_VERSION}/
            fi
        done
    fi
}

do_deploy() {
    # Deploy fragment for greengrass-bin to merge later
    if [ -e ${UNPACKDIR}/config.yaml.template ]; then
        install -d ${DEPLOYDIR}/greengrass-plugin-fragments
        cp "${UNPACKDIR}/config.yaml.template" "${DEPLOYDIR}/greengrass-plugin-fragments/${COMPONENT_NAME}.yaml"
    fi
}
addtask deploy after do_install before do_populate_sysroot

FILES:${PN} += "/${GG_BASENAME}/components/ \
               "
