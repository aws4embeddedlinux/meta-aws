# oelint-disable: oelint.bbclass.underscores
# AWS Greengrass Classic Component Class (V2 Java / bin)
#
# This bbclass supports Greengrass Classic (greengrass-bin) components.
# For Greengrass Lite components, use greengrass-lite-component.bbclass instead.
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
#   COMPONENT_ARTIFACTS - List of artifacts to install (defaults to all files in SRC_URI except config files)
#
# Expected files in SRC_URI:
#   - Component artifacts (scripts, binaries, etc.)
#   - config.yaml.template (optional, for component configuration fragment)
#   - component-recipe.yaml (optional, standard Greengrass component recipe)
#   - component-config.yaml.template (optional, legacy format, converted to fragment)
#
# Priority order for configuration:
#   1. config.yaml.template (direct fragment)
#   2. component-recipe.yaml (converted to fragment)
#   3. component-config.yaml.template (legacy format, converted to fragment)
#
# Example usage:
#   COMPONENT_NAME = "com.example.HelloWorld"
#   COMPONENT_VERSION = "1.0.0"
#   COMPONENT_ARTIFACTS = "hello_world.py"
#   inherit greengrass-component

require greengrass-common.inc

DEPENDS:prepend = "python3-pyyaml-native "

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

inherit deploy

S = "${WORKDIR}"

# Greengrass variant configuration - Classic only
GREENGRASS_VARIANT ?= "classic"

# Validate variant - only classic supported
python __anonymous() {
    variant = d.getVar('GREENGRASS_VARIANT')

    # Only classic variant supported
    if variant != 'classic':
        bb.fatal(f"greengrass-component class only supports GREENGRASS_VARIANT='classic'. Use greengrass-lite-component class for lite variant.")

    # Set classic variant paths
    d.setVar('GG_COMPONENT_ROOT', '/${GG_BASENAME}/components')
    d.setVar('GG_CONFIG_DIR', '/${GG_BASENAME}/config')
    d.setVar('GG_CONFIG_FRAGMENT_DIR', 'greengrass-plugin-fragments')

    bb.note("Greengrass classic variant configured")
}

# Python function to convert component recipe to config fragment
python convert_recipe_to_fragment() {
    import yaml
    import os

    recipe_file = d.getVar('recipe_file')
    output_file = d.getVar('output_file')

    if not recipe_file or not os.path.exists(recipe_file):
        bb.error(f"Recipe file not found: {recipe_file}")
        d.setVar('CONVERSION_SUCCESS', '0')
        return

    if not output_file:
        bb.error("Output file not specified")
        d.setVar('CONVERSION_SUCCESS', '0')
        return

    try:
        with open(recipe_file, 'r') as f:
            recipe = yaml.safe_load(f)
    except Exception as e:
        bb.error(f"Error reading recipe file {recipe_file}: {e}")
        d.setVar('CONVERSION_SUCCESS', '0')
        return

    # Extract key information from the recipe
    component_name = recipe.get('ComponentName')
    component_version = recipe.get('ComponentVersion', '1.0.0')
    component_type = recipe.get('ComponentType', 'aws.greengrass.generic')

    if not component_name:
        bb.error("ComponentName not found in recipe")
        d.setVar('CONVERSION_SUCCESS', '0')
        return

    # Build the config fragment for classic variant
    fragment = {
        'services': {
            component_name: {
                'ComponentType': component_type,
                'ComponentVersion': component_version
            }
        }
    }

    # Add configuration if present
    config = recipe.get('ComponentConfiguration', {}).get('DefaultConfiguration', {})
    if config:
        fragment['services'][component_name]['configuration'] = config

        # Process manifests to extract lifecycle and artifacts
        manifests = recipe.get('Manifests', [])
        if manifests:
            # Use the first manifest (typically Linux)
            manifest = manifests[0]

            # Add lifecycle information
            lifecycle = manifest.get('Lifecycle', {})
            if lifecycle:
                # Replace {artifacts:path} placeholder with actual component path
                component_path = f"/greengrass/v2/components/{component_name}/{component_version}"
                processed_lifecycle = {}
                for key, value in lifecycle.items():
                    if isinstance(value, str):
                        processed_lifecycle[key] = value.replace('{artifacts:path}', component_path)
                    else:
                        processed_lifecycle[key] = value
                fragment['services'][component_name]['Lifecycle'] = processed_lifecycle

            # Add artifacts information
            artifacts = manifest.get('Artifacts', [])
            if artifacts:
                fragment['services'][component_name]['Artifacts'] = artifacts

        # Add dependencies structure for bin variant
        fragment['services']['main'] = {
            'dependencies': [component_name],
            'lifecycle': {}
        }

    # Write the fragment
    try:
        with open(output_file, 'w') as f:
            yaml.dump(fragment, f, default_flow_style=False, sort_keys=False)
        bb.note(f"Converted recipe to config fragment: {output_file}")
        d.setVar('CONVERSION_SUCCESS', '1')
    except Exception as e:
        bb.error(f"Error writing config fragment {output_file}: {e}")
        d.setVar('CONVERSION_SUCCESS', '0')
}

do_install:append() {
    # Create component directory structure for Greengrass Classic
    install -d ${D}${GG_COMPONENT_ROOT}/${COMPONENT_NAME}/${COMPONENT_VERSION}
    COMPONENT_INSTALL_DIR="${D}${GG_COMPONENT_ROOT}/${COMPONENT_NAME}/${COMPONENT_VERSION}"

    # Install component artifacts
    if [ -n "${COMPONENT_ARTIFACTS}" ]; then
        # Install specific artifacts if defined
        for artifact in ${COMPONENT_ARTIFACTS}; do
            if [ -f ${WORKDIR}/${artifact} ]; then
                install -m 0755 ${WORKDIR}/${artifact} ${COMPONENT_INSTALL_DIR}/
            else
                bbwarn "Artifact ${artifact} not found in ${WORKDIR}"
            fi
        done
    else
        # Auto-detect and install CMake binaries for cmake-based recipes
        if [ -d "${B}" ] && [ -f "${B}/CMakeCache.txt" ]; then
            # Look for executable files in build directory (CMake pattern)
            for binary in $(find ${B} -maxdepth 1 -type f -executable -not -path "*/CMakeFiles/*" 2>/dev/null || true); do
                if [ -f "$binary" ]; then
                    binary_name=$(basename "$binary")
                    bbdebug 1 "Installing CMake binary: $binary_name"
                    install -m 0755 "$binary" ${COMPONENT_INSTALL_DIR}/
                fi
            done
        fi

        # Install all files except recipe files from WORKDIR
        for file in ${WORKDIR}/*; do
            if [ -f "$file" ]; then
                case "$(basename "$file")" in
                    component-recipe.yaml|*.yaml)
                        # Skip recipe files - they're handled by configuration logic
                        ;;
                    *)
                        install -m 0755 "$file" ${COMPONENT_INSTALL_DIR}/
                        ;;
                esac
            fi
        done
    fi
}

python do_deploy() {
    import os
    import shutil
    import tempfile
    import subprocess

    deploydir = d.getVar('DEPLOYDIR')
    component_name = d.getVar('COMPONENT_NAME')

    # Ensure DEPLOYDIR exists to satisfy sstate requirements
    os.makedirs(deploydir, exist_ok=True)

    # Handle greengrass-bin components deployment
    unpackdir = d.getVar('S')  # Use S instead of WORKDIR for source files

    # Use deployment directory for fragment merging
    fragment_dir = os.path.join(deploydir, d.getVar('GG_CONFIG_FRAGMENT_DIR'))
    os.makedirs(fragment_dir, exist_ok=True)
    fragment_file = os.path.join(fragment_dir, f"{component_name}.yaml")

    # Priority order for configuration files
    config_files = [
        'config.yaml.template',           # Direct fragment (highest priority)
        'component-recipe.yaml',          # Standard recipe (convert)
        'component-config.yaml.template'  # Legacy format (convert)
    ]

    for config_file in config_files:
        config_path = os.path.join(unpackdir, config_file)
        if os.path.exists(config_path):
            if config_file == 'config.yaml.template':
                # Direct copy for template fragments
                shutil.copy2(config_path, fragment_file)
                bb.note(f"Using direct config fragment for greengrass-bin: {config_file}")
            else:
                # Convert recipe to fragment using inline function
                d.setVar('recipe_file', config_path)
                d.setVar('output_file', fragment_file)

                # Call conversion function and check success flag
                bb.build.exec_func('convert_recipe_to_fragment', d)
                success = d.getVar('CONVERSION_SUCCESS')

                if success == '1':
                    bb.note(f"Converted {config_file} to config fragment for greengrass-bin")
                else:
                    bb.error(f"Failed to convert {config_file}")
                    continue
            break
    else:
        bb.warn(f"No configuration file found for component {component_name}")
}

# Add deploy task to BitBake task list - required for fragment generation
# Without this, the do_deploy() Python function above won't be executed
addtask deploy after do_install before do_build

# Clean deploy directory before creating fragments to ensure stale fragments are removed
# This prevents old fragments from being merged when components are removed from builds
do_deploy[cleandirs] = "${DEPLOYDIR}"

# Task dependencies for classic variant
python __anonymous() {
    # For greengrass-classic, deploy creates fragments for later merging
    d.setVarFlag('do_deploy', 'cleandirs', '${DEPLOYDIR}/greengrass-plugin-fragments')
}

# Set FILES for classic variant
python __anonymous() {
    pn = d.getVar('PN')

    # Greengrass Bin files (existing behavior)
    files = d.getVar('FILES:' + pn) or ''
    files += ' /${GG_BASENAME}/components/'
    d.setVar('FILES:' + pn, files)
}

# Runtime dependency on greengrass-bin for classic variant
RDEPENDS:${PN} += "greengrass-bin"

# Ensure all Greengrass plugins/components wait for greengrass-bin to install base structure
python __anonymous() {
    pn = d.getVar('PN')
    if pn and pn != 'greengrass-bin*':
        # Add dependency on greengrass-bin's do_install task
        d.appendVarFlag('do_install', 'depends', ' greengrass-bin:do_install')
}
