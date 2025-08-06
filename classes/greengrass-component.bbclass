# AWS Greengrass Classic Component Class
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

require greengrass-component-plugin-common.inc

inherit deploy

S = "${UNPACKDIR}"

# Default component version if not specified
COMPONENT_VERSION ??= "1.0.0"

# Greengrass variant configuration - Classic only
GREENGRASS_VARIANT ?= "classic"

# Validate variant and warn about Lite
python __anonymous() {
    variant = d.getVar('GREENGRASS_VARIANT')
    
    # Validate variant value
    if variant not in ['classic', 'lite']:
        bb.fatal(f"Invalid GREENGRASS_VARIANT '{variant}'. Must be 'classic' or 'lite'")
    
    if variant == 'lite':
        bb.warn("GREENGRASS_VARIANT='lite' detected. Consider using 'greengrass-lite-component' class for better Lite support and clearer image-provided component paths.")
        # Set basic Lite paths for backward compatibility
        d.setVar('GG_COMPONENT_ROOT', '/usr/components')
        d.setVar('GG_CONFIG_DIR', '${sysconfdir}/greengrass/config.d')
        d.setVar('GG_CONFIG_FRAGMENT_DIR', '${sysconfdir}/greengrass/config.d')
    else:  # classic variant
        d.setVar('GG_COMPONENT_ROOT', '/${GG_BASENAME}/components')
        d.setVar('GG_CONFIG_DIR', '/${GG_BASENAME}/config')
        d.setVar('GG_CONFIG_FRAGMENT_DIR', 'greengrass-plugin-fragments')
    
    bb.note(f"Greengrass variant configured: {variant}")
}

# Python function to convert component recipe to config fragment
python convert_recipe_to_fragment() {
    import yaml
    import os
    
    recipe_file = d.getVar('recipe_file')
    output_file = d.getVar('output_file')
    variant = d.getVar('GREENGRASS_VARIANT')
    
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
    
    # Build the config fragment based on variant
    if variant == 'lite':
        # Greengrass Lite uses simpler YAML structure
        fragment = {
            'services': {
                component_name: {
                    'componentType': component_type,
                    'componentVersion': component_version
                }
            }
        }
        
        # Add configuration if present
        config = recipe.get('ComponentConfiguration', {}).get('DefaultConfiguration', {})
        if config:
            fragment['services'][component_name]['configuration'] = config
        
        # Process manifests for lifecycle (simplified for lite)
        manifests = recipe.get('Manifests', [])
        if manifests:
            manifest = manifests[0]
            lifecycle = manifest.get('Lifecycle', {})
            if lifecycle:
                fragment['services'][component_name]['lifecycle'] = lifecycle
    else:
        # Greengrass Bin uses full structure
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
                fragment['services'][component_name]['Lifecycle'] = lifecycle
            
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
        bb.note(f"Converted recipe to config fragment for {variant} variant: {output_file}")
        d.setVar('CONVERSION_SUCCESS', '1')
    except Exception as e:
        bb.error(f"Error writing config fragment {output_file}: {e}")
        d.setVar('CONVERSION_SUCCESS', '0')
}

do_install() {
    # Create component directory structure for Greengrass Classic
    if [ "${GREENGRASS_VARIANT}" = "lite" ]; then
        bbwarn "Greengrass Lite variant detected. Consider using 'greengrass_lite_component' class for better support."
        # Basic Lite support for backward compatibility
        install -d ${D}/usr/components/${COMPONENT_NAME}/${COMPONENT_VERSION}
        COMPONENT_INSTALL_DIR="${D}/usr/components/${COMPONENT_NAME}/${COMPONENT_VERSION}"
    else
        # Greengrass Classic uses /greengrass/v2/components/
        install -d ${D}${GG_COMPONENT_ROOT}/${COMPONENT_NAME}/${COMPONENT_VERSION}
        COMPONENT_INSTALL_DIR="${D}${GG_COMPONENT_ROOT}/${COMPONENT_NAME}/${COMPONENT_VERSION}"
    fi

    # Install component artifacts
    if [ -n "${COMPONENT_ARTIFACTS}" ]; then
        # Install specific artifacts if defined
        for artifact in ${COMPONENT_ARTIFACTS}; do
            if [ -f ${UNPACKDIR}/${artifact} ]; then
                install -m 0755 ${UNPACKDIR}/${artifact} ${COMPONENT_INSTALL_DIR}/
            else
                bbwarn "Artifact ${artifact} not found in ${UNPACKDIR}"
            fi
        done
    else
        # Install all files except recipe files
        for file in ${UNPACKDIR}/*; do
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
    
    variant = d.getVar('GREENGRASS_VARIANT')
    deploydir = d.getVar('DEPLOYDIR')
    component_name = d.getVar('COMPONENT_NAME')
    
    # Ensure DEPLOYDIR exists for all variants to satisfy sstate requirements
    os.makedirs(deploydir, exist_ok=True)
    
    # Skip actual deploy logic for lite components - everything is handled in do_install
    if variant == 'lite':
        bb.note("Skipping deploy for greengrass-lite component - config handled in do_install")
        # Create a minimal marker file to satisfy deploy class expectations
        marker_file = os.path.join(deploydir, f"{component_name}-lite.deployed")
        with open(marker_file, 'w') as f:
            f.write("Greengrass Lite component deployed via do_install\n")
        return
    
    # Only handle greengrass-bin components in deploy
    unpackdir = d.getVar('S')  # Use S instead of UNPACKDIR for source files
    
    # For Greengrass Bin, use deployment directory for fragment merging
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

# Task dependencies based on variant
python __anonymous() {
    variant = d.getVar('GREENGRASS_VARIANT')
    if variant == 'lite':
        # For greengrass-lite, deploy runs after install and before populate_sysroot
        # This installs config fragments directly to the target
        d.setVarFlag('do_deploy', 'cleandirs', '')  # No cleandirs needed for lite
    else:
        # For greengrass-classic, deploy creates fragments for later merging
        d.setVarFlag('do_deploy', 'cleandirs', '${DEPLOYDIR}/greengrass-plugin-fragments')
}

addtask deploy after do_install before do_populate_sysroot

# Set FILES based on variant
python __anonymous() {
    variant = d.getVar('GREENGRASS_VARIANT')
    pn = d.getVar('PN')
    
    if variant == 'lite':
        # Greengrass Lite files
        files = d.getVar('FILES:' + pn) or ''
        files += ' /usr/components/ ${sysconfdir}/greengrass/config.d/'
        d.setVar('FILES:' + pn, files)
    else:
        # Greengrass Bin files (existing behavior)
        files = d.getVar('FILES:' + pn) or ''
        files += ' /${GG_BASENAME}/components/'
        d.setVar('FILES:' + pn, files)
}
