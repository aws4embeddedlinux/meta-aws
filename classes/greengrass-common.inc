# Common Greengrass definitions
# This file provides basic definitions used by all Greengrass recipes

require greengrass-base.inc

do_install:append() {
    # Merge configuration fragments from installed components
    python3 -c "
import os
import yaml
import glob

def deep_merge(base_dict, update_dict):
    '''Recursively merge two dictionaries'''
    for key, value in update_dict.items():
        if key in base_dict and isinstance(base_dict[key], dict) and isinstance(value, dict):
            deep_merge(base_dict[key], value)
        else:
            base_dict[key] = value
    return base_dict

def load_yaml_file(file_path):
    '''Load YAML file, handling both single and multiple documents'''
    with open(file_path, 'r') as f:
        content = f.read()

    # Check if file has multiple documents (contains ---)
    if '---' in content and content.count('---') > 1:
        # Multiple documents - load all and merge them
        docs = list(yaml.safe_load_all(content))
        merged = {}
        for doc in docs:
            if doc:  # Skip empty documents
                merged = deep_merge(merged, doc)
        return merged
    else:
        # Single document
        return yaml.safe_load(content)

# Get all recipes that should be installed
image_install = '${IMAGE_INSTALL}'.split()
rdepends = '${@d.getVar("RDEPENDS:" + d.getVar("PN")) or ""}'.split()
all_installed_recipes = set(image_install + rdepends)

print(f'IMAGE_INSTALL recipes: {image_install}')
print(f'RDEPENDS recipes: {rdepends}')
print(f'All installed recipes: {list(all_installed_recipes)}')

# Find fragments only from installed recipes
fragments_to_merge = []

print('Searching for Greengrass config fragments from installed components...')

# Look for fragments in recipe work directories
work_dir = '${TMPDIR}/work'
for recipe in all_installed_recipes:
    # Search for this recipe's work directory and fragments
    recipe_pattern = os.path.join(work_dir, '*', recipe, '*', f'deploy-{recipe}', 'greengrass-plugin-fragments')
    matching_dirs = glob.glob(recipe_pattern)

    for fragment_dir in matching_dirs:
        if os.path.exists(fragment_dir):
            print(f'Found fragment directory: {fragment_dir}')
            # Find all .yaml files in this recipe's fragment directory
            for fragment_file in os.listdir(fragment_dir):
                if fragment_file.endswith('.yaml'):
                    fragment_path = os.path.join(fragment_dir, fragment_file)
                    print(f'Found config fragment: {recipe} -> {fragment_path}')
                    fragments_to_merge.append(fragment_path)

if fragments_to_merge:
    print(f'Merging {len(fragments_to_merge)} Greengrass configuration fragments')

    base_config_path = '${GG_ROOT}/config/config.yaml'

    try:
        # Load base configuration
        merged_config = load_yaml_file(base_config_path)

        # Merge each fragment
        for fragment_path in fragments_to_merge:
            fragment_config = load_yaml_file(fragment_path)
            if fragment_config:
                merged_config = deep_merge(merged_config, fragment_config)
                print(f'Merged fragment: {os.path.basename(fragment_path)}')

        # Write merged configuration back
        with open(base_config_path, 'w') as f:
            yaml.dump(merged_config, f, default_flow_style=False, sort_keys=False)

        print('Configuration merge completed successfully')

        # Show what components were merged for verification
        if 'services' in merged_config:
            components = [key for key in merged_config.get('services', {}).keys() if key.startswith('com.')]
            if components:
                print('Components in merged configuration: ' + ', '.join(components))

    except Exception as e:
        print(f'Error during configuration merge: {e}')
        raise
else:
    print('No Greengrass configuration fragments found from installed components')
"
}