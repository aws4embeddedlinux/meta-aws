# oelint-disable: oelint.bbclass.underscores
# Greengrass Lite Component Class
# This class handles local component deployment for AWS IoT Greengrass Lite
# Supports both zero-copy (direct placement) and traditional (copy-based) deployment

DEPENDS:prepend = "python3-pyyaml-native "

# Configuration: Zero-copy deployment (default: enabled)
# Set GREENGRASS_LITE_ZERO_COPY = "0" to use traditional copy-based deployment
GREENGRASS_LITE_ZERO_COPY ??= "1"

# Deployment paths based on zero-copy setting
GGL_PACKAGES_DIR = "/var/lib/greengrass/packages"
GGL_IMAGE_COMPONENTS_ROOT = "/usr/share/greengrass-image-components"

# Choose paths based on zero-copy setting
GGL_RECIPES_DIR = "${@'${GGL_PACKAGES_DIR}/recipes' if d.getVar('GREENGRASS_LITE_ZERO_COPY') == '1' else '${GGL_IMAGE_COMPONENTS_ROOT}/recipes'}"
GGL_ARTIFACTS_DIR = "${@'${GGL_PACKAGES_DIR}/artifacts' if d.getVar('GREENGRASS_LITE_ZERO_COPY') == '1' else '${GGL_IMAGE_COMPONENTS_ROOT}/artifacts'}"

# Validate that this is for Greengrass Lite
python __anonymous() {
    variant = d.getVar('GREENGRASS_VARIANT')
    if variant != 'lite':
        bb.fatal("greengrass_lite_component class can only be used with GREENGRASS_VARIANT = 'lite'")

    zero_copy = d.getVar('GREENGRASS_LITE_ZERO_COPY')
    component_name = d.getVar('COMPONENT_NAME') or "unknown"
    deployment_mode = "zero-copy (direct placement)" if zero_copy == '1' else "traditional (copy-based)"
    bb.debug(1, f"Greengrass Lite component: {component_name} - {deployment_mode}")
}

# Extract component information from YAML file, allow recipe override
python __anonymous() {
    import os

    def parse_simple_yaml(content):
        """Simple YAML parser for basic key-value pairs"""
        result = {}
        lines = content.split('\n')
        for line in lines:
            line = line.strip()
            if line and not line.startswith('#') and ':' in line:
                key, value = line.split(':', 1)
                key = key.strip()
                value = value.strip().strip('"\'')
                if value:
                    result[key] = value
        return result

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
                    content = f.read()

                recipe = parse_simple_yaml(content)

                # Set COMPONENT_NAME from YAML if not already set in recipe
                if not component_name:
                    yaml_component_name = recipe.get('ComponentName')
                    if yaml_component_name:
                        d.setVar('COMPONENT_NAME', yaml_component_name)
                        bb.debug(1, f"Set COMPONENT_NAME from YAML: {yaml_component_name}")
                        component_name = yaml_component_name

                # Set COMPONENT_VERSION from YAML if not already set in recipe
                if not component_version:
                    yaml_component_version = recipe.get('ComponentVersion', '1.0.0')
                    d.setVar('COMPONENT_VERSION', yaml_component_version)
                    bb.debug(1, f"Set COMPONENT_VERSION from YAML: {yaml_component_version}")
                    component_version = yaml_component_version

            except Exception as e:
                bb.warn(f"Could not read component info from YAML {yaml_file}: {e}")

    # Ensure defaults are set if still not defined
    if not component_name:
        bb.error("COMPONENT_NAME must be set either in recipe or component-recipe.yaml")

    if not component_version:
        d.setVar('COMPONENT_VERSION', '1.0.0')
}

# Installation function for Greengrass Lite components
do_install:prepend() {
    # Create component directories
    install -d ${D}${GGL_RECIPES_DIR}
    install -d ${D}${GGL_ARTIFACTS_DIR}

    # Install component recipe
    install -m 0644 ${WORKDIR}/component-recipe.yaml ${D}${GGL_RECIPES_DIR}/${COMPONENT_NAME}-${COMPONENT_VERSION}.yaml

    # Create component artifact directory
    install -d ${D}${GGL_ARTIFACTS_DIR}/${COMPONENT_NAME}/${COMPONENT_VERSION}
}

# Package the appropriate directories based on mode
FILES:${PN} += "${@'${GGL_PACKAGES_DIR}/*' if d.getVar('GREENGRASS_LITE_ZERO_COPY') == '1' else '${GGL_IMAGE_COMPONENTS_ROOT}/*'}"

# Runtime dependency on greengrass-lite
RDEPENDS:${PN} += "greengrass-lite"
