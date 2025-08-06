# Greengrass Lite Component Class
# This class handles local component deployment for AWS IoT Greengrass Lite
# Supports both zero-copy (direct placement) and traditional (copy-based) deployment

# Configuration: Zero-copy deployment (default: enabled)
# Set GREENGRASS_LITE_ZERO_COPY = "0" to use traditional copy-based deployment
GREENGRASS_LITE_ZERO_COPY ??= "1"

# Deployment paths based on zero-copy setting
GGL_PACKAGES_DIR = "/var/lib/greengrass/packages"
GGL_IMAGE_COMPONENTS_ROOT = "/usr/share/greengrass-image-components"

# Choose paths based on zero-copy setting
GGL_RECIPES_DIR = "${@'${GGL_PACKAGES_DIR}/recipes' if d.getVar('GREENGRASS_LITE_ZERO_COPY') == '1' else '${GGL_IMAGE_COMPONENTS_ROOT}/recipes'}"
GGL_ARTIFACTS_DIR = "${@'${GGL_PACKAGES_DIR}/artifacts' if d.getVar('GREENGRASS_LITE_ZERO_COPY') == '1' else '${GGL_IMAGE_COMPONENTS_ROOT}/artifacts'}"

# Component metadata
COMPONENT_NAME ??= ""
COMPONENT_VERSION ??= "1.0.0"
COMPONENT_ARTIFACTS ??= ""

# Inherit base functionality
inherit systemd

# Default source directory
S = "${UNPACKDIR}"

# Validate that this is for Greengrass Lite
python __anonymous() {
    variant = d.getVar('GREENGRASS_VARIANT')
    if variant != 'lite':
        bb.fatal("greengrass_lite_component class can only be used with GREENGRASS_VARIANT = 'lite'")
    
    component_name = d.getVar('COMPONENT_NAME')
    if not component_name:
        bb.fatal("COMPONENT_NAME must be set when using greengrass_lite_component class")
    
    zero_copy = d.getVar('GREENGRASS_LITE_ZERO_COPY')
    deployment_mode = "zero-copy (direct placement)" if zero_copy == '1' else "traditional (copy-based)"
    bb.note(f"Greengrass Lite component: {component_name} - {deployment_mode}")
}

# Installation function for Greengrass Lite components
greengrass_lite_component_do_install() {
    # Create component directories
    install -d ${D}${GGL_RECIPES_DIR}
    install -d ${D}${GGL_ARTIFACTS_DIR}
    
    # Install component recipe
    if [ -f "${S}/component-recipe.yaml" ]; then
        install -m 0644 ${S}/component-recipe.yaml ${D}${GGL_RECIPES_DIR}/${COMPONENT_NAME}-${COMPONENT_VERSION}.yaml
        if [ "${GREENGRASS_LITE_ZERO_COPY}" = "1" ]; then
            bbnote "Installed component recipe directly: ${GGL_RECIPES_DIR}/${COMPONENT_NAME}-${COMPONENT_VERSION}.yaml (zero-copy mode)"
        else
            bbnote "Installed component recipe: ${GGL_RECIPES_DIR}/${COMPONENT_NAME}-${COMPONENT_VERSION}.yaml (traditional mode)"
        fi
    else
        bbfatal "component-recipe.yaml not found in ${S}"
    fi
    
    # Create component artifact directory
    install -d ${D}${GGL_ARTIFACTS_DIR}/${COMPONENT_NAME}/${COMPONENT_VERSION}
    
    # Install component artifacts
    if [ -n "${COMPONENT_ARTIFACTS}" ]; then
        for artifact in ${COMPONENT_ARTIFACTS}; do
            if [ -f "${S}/$artifact" ]; then
                install -m 0755 ${S}/$artifact ${D}${GGL_ARTIFACTS_DIR}/${COMPONENT_NAME}/${COMPONENT_VERSION}/
                if [ "${GREENGRASS_LITE_ZERO_COPY}" = "1" ]; then
                    bbnote "Installed artifact directly: ${GGL_ARTIFACTS_DIR}/${COMPONENT_NAME}/${COMPONENT_VERSION}/$artifact (zero-copy mode)"
                else
                    bbnote "Installed artifact: ${GGL_ARTIFACTS_DIR}/${COMPONENT_NAME}/${COMPONENT_VERSION}/$artifact (traditional mode)"
                fi
            else
                bbwarn "Artifact not found: $artifact"
            fi
        done
    else
        bbnote "No artifacts specified for ${COMPONENT_NAME}"
    fi
    
    # Set proper ownership for Greengrass directories (only for zero-copy mode)
    if [ "${GREENGRASS_LITE_ZERO_COPY}" = "1" ]; then
        # Note: This will be handled by pkg_postinst to ensure ggcore user exists
        bbnote "Zero-copy mode: Will set ownership to ggcore:ggcore in pkg_postinst"
    fi
}

# Set proper ownership after installation (only for zero-copy mode)
pkg_postinst:${PN}() {
    if [ "${GREENGRASS_LITE_ZERO_COPY}" = "1" ]; then
        if [ -n "$D" ]; then
            # During image creation - set ownership in rootfs
            if [ -d "$D${GGL_PACKAGES_DIR}" ]; then
                chown -R ggcore:ggcore "$D${GGL_PACKAGES_DIR}" 2>/dev/null || true
            fi
        else
            # During runtime installation - set ownership on live system
            if [ -d "${GGL_PACKAGES_DIR}" ]; then
                chown -R ggcore:ggcore "${GGL_PACKAGES_DIR}" 2>/dev/null || true
            fi
        fi
    fi
}

# Deploy task for compatibility with greengrass-bin
do_deploy() {
    # For Greengrass Lite components, deployment happens at runtime via ggl-deploy-image-components
    # This task exists for compatibility with greengrass-bin's dependency expectations
    # The actual component files are already installed in do_install
    :
}

# Add to install task
EXPORT_FUNCTIONS do_install do_deploy
addtask do_install after do_compile before do_package
addtask do_deploy after do_install before do_package

# Default do_install implementation
greengrass_lite_component_do_install[dirs] = "${D}"

# Package the appropriate directories based on mode
FILES:${PN} += "${@'${GGL_PACKAGES_DIR}/*' if d.getVar('GREENGRASS_LITE_ZERO_COPY') == '1' else '${GGL_IMAGE_COMPONENTS_ROOT}/*'}"

# Ensure proper file permissions
INSANE_SKIP:${PN} += "already-stripped"
