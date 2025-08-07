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

# Extract component information from YAML file, allow recipe override
python __anonymous() {
    import yaml
    import os
    
    # First, check if COMPONENT_NAME and COMPONENT_VERSION are already set in recipe
    component_name = d.getVar('COMPONENT_NAME')
    component_version = d.getVar('COMPONENT_VERSION')
    
    # Check if component-recipe.yaml exists in SRC_URI
    src_uri = d.getVar('SRC_URI') or ''
    has_component_recipe = 'component-recipe.yaml' in src_uri
    
    if has_component_recipe and (not component_name or not component_version):
        # Try to find the YAML file in the recipe directory
        file_dirname = d.getVar('FILE_DIRNAME')
        pn = d.getVar('PN')
        
        # Look for component-recipe.yaml in the recipe's files directory
        possible_paths = [
            os.path.join(file_dirname, pn, 'component-recipe.yaml'),
            os.path.join(file_dirname, 'files', 'component-recipe.yaml'),
            os.path.join(file_dirname, pn.replace('greengrass-component-', ''), 'component-recipe.yaml')
        ]
        
        yaml_file = None
        for path in possible_paths:
            if os.path.exists(path):
                yaml_file = path
                break
        
        if yaml_file:
            try:
                with open(yaml_file, 'r') as f:
                    recipe = yaml.safe_load(f)
                
                # Set COMPONENT_NAME from YAML if not already set in recipe
                if not component_name:
                    yaml_component_name = recipe.get('ComponentName')
                    if yaml_component_name:
                        d.setVar('COMPONENT_NAME', yaml_component_name)
                        bb.note(f"Set COMPONENT_NAME from YAML: {yaml_component_name}")
                        component_name = yaml_component_name
                
                # Set COMPONENT_VERSION from YAML if not already set in recipe
                if not component_version:
                    yaml_component_version = recipe.get('ComponentVersion', '1.0.0')
                    d.setVar('COMPONENT_VERSION', yaml_component_version)
                    bb.note(f"Set COMPONENT_VERSION from YAML: {yaml_component_version}")
                    component_version = yaml_component_version
                    
            except Exception as e:
                bb.warn(f"Could not read component info from YAML {yaml_file}: {e}")
    
    # Ensure defaults are set if still not defined
    if not component_name:
        bb.error("COMPONENT_NAME must be set either in recipe or component-recipe.yaml")
    
    if not component_version:
        d.setVar('COMPONENT_VERSION', '1.0.0')
}

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
do_install() {
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
addtask do_install after do_compile before do_package
addtask do_deploy after do_install before do_package

# Package the appropriate directories based on mode
FILES:${PN} += "${@'${GGL_PACKAGES_DIR}/*' if d.getVar('GREENGRASS_LITE_ZERO_COPY') == '1' else '${GGL_IMAGE_COMPONENTS_ROOT}/*'}"

# Ensure proper file permissions
INSANE_SKIP:${PN} += "already-stripped"
