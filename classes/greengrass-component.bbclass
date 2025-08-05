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

# Python function to convert component recipe to config fragment
python convert_recipe_to_fragment() {
    import yaml
    import os
    
    recipe_file = d.getVar('recipe_file')
    output_file = d.getVar('output_file')
    
    try:
        with open(recipe_file, 'r') as f:
            recipe = yaml.safe_load(f)
    except Exception as e:
        bb.error(f"Error reading recipe file {recipe_file}: {e}")
        return False
    
    # Extract key information from the recipe
    component_name = recipe.get('ComponentName')
    component_version = recipe.get('ComponentVersion', '1.0.0')
    component_type = recipe.get('ComponentType', 'aws.greengrass.generic')
    
    if not component_name:
        bb.error("ComponentName not found in recipe")
        return False
    
    # Build the config fragment
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
    
    # Add dependencies structure
    fragment['services']['main'] = {
        'dependencies': [component_name],
        'lifecycle': {}
    }
    
    # Write the fragment
    try:
        with open(output_file, 'w') as f:
            yaml.dump(fragment, f, default_flow_style=False, sort_keys=False)
        bb.note(f"Converted recipe to config fragment: {output_file}")
        return True
    except Exception as e:
        bb.error(f"Error writing config fragment {output_file}: {e}")
        return False
}

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
        # Install all files from SRC_URI (excluding config files)
        for file in ${UNPACKDIR}/*; do
            if [ -f "$file" ]; then
                case "$(basename "$file")" in
                    config.yaml.template|component-recipe.yaml|component-config.yaml.template)
                        # Skip config files - they're handled in do_deploy
                        ;;
                    *)
                        install -m 0755 "$file" ${GG_ROOT}/components/${COMPONENT_NAME}/${COMPONENT_VERSION}/
                        ;;
                esac
            fi
        done
    fi
}

python do_deploy() {
    import os
    import shutil
    
    deploydir = d.getVar('DEPLOYDIR')
    unpackdir = d.getVar('S')  # Use S instead of UNPACKDIR for source files
    component_name = d.getVar('COMPONENT_NAME')
    
    # Create fragment directory
    fragment_dir = os.path.join(deploydir, 'greengrass-plugin-fragments')
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
                bb.note(f"Using direct config fragment: {config_file}")
            else:
                # Convert recipe to fragment using inline function
                d.setVar('recipe_file', config_path)
                d.setVar('output_file', fragment_file)
                
                if bb.build.exec_func('convert_recipe_to_fragment', d):
                    bb.note(f"Converted {config_file} to config fragment")
                else:
                    bb.error(f"Failed to convert {config_file}")
                    continue
            break
    else:
        bb.warn(f"No configuration file found for component {component_name}")
}

addtask deploy after do_install before do_populate_sysroot

do_deploy[cleandirs] += "${DEPLOYDIR}/greengrass-plugin-fragments"

FILES:${PN} += "/${GG_BASENAME}/components/ \
               "
