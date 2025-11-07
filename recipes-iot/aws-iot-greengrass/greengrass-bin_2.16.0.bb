SUMMARY = "AWS IoT Greengrass Nucleus - Binary Distribution"
DESCRIPTION = "The Greengrass nucleus component provides functionality for device side orchestration of deployments and lifecycle management for execution of Greengrass components and applications."
HOMEPAGE = "https://github.com/aws-greengrass/aws-greengrass-nucleus"
LICENSE = "Apache-2.0"

require classes/greengrass-common.inc

LIC_FILES_CHKSUM = "file://${UNPACKDIR}/greengrass-bin/LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

DEPENDS += "gettext-native"

# for the component fragment merge
DEPENDS += "python3-pyyaml-native"

# enable fleetprovisioning for testing by default to get test coverage
PACKAGECONFIG ??= "${@bb.utils.contains('PTEST_ENABLED', '1', 'fleetprovisioning', '', d)}"

PACKAGECONFIG[fleetprovisioning] = ",,greengrass-plugin-fleetprovisioning,greengrass-plugin-fleetprovisioning"
PACKAGECONFIG[pkcs11] = ",,greengrass-plugin-pkcs11,greengrass-plugin-pkcs11"

SRC_URI = "\
    https://d2s8p88vqu9w66.cloudfront.net/releases/greengrass-${PV}.zip;subdir=greengrass-bin \
    file://001-service-time-wait.patch \
    file://greengrassv2-init.yaml \
    file://run-ptest \
    file://config.yaml.template \
    "

SRC_URI[sha256sum] = "f07742c76eca868617127b5c6c9028e41c45c2b4ec25dd0db6f3b40ef7638b4e"
UPSTREAM_CHECK_REGEX ?= "releases/tag/v?(?P<pver>\d+(\.\d+)+)"

UPSTREAM_CHECK_URI = "https://github.com/aws-greengrass/aws-greengrass-nucleus/tags"

GG_USESYSTEMD = "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'yes', 'no', d)}"

inherit systemd useradd ptest pkgconfig

S = "${UNPACKDIR}/greengrass-bin"

FILES:${PN} += "\
    /${GG_BASENAME} \
    ${systemd_unitdir} \
    "

RDEPENDS:${PN} += "\
    ca-certificates \
    java-11 \
    python3-core \
    python3-json \
    python3-numbers \
    sudo \
    "

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${GG_ROOT}/config
    install -d ${GG_ROOT}/alts
    install -d ${GG_ROOT}/alts/init
    install -d ${GG_ROOT}/packages/artifacts-unarchived/aws.greengrass.Nucleus/${PV}/aws.greengrass.nucleus
    install -d ${GG_ROOT}/packages/artifacts-unarchived/aws.greengrass.Nucleus/${PV}/aws.greengrass.nucleus/bin
    install -d ${GG_ROOT}/packages/artifacts-unarchived/aws.greengrass.Nucleus/${PV}/aws.greengrass.nucleus/conf
    install -d ${GG_ROOT}/packages/artifacts-unarchived/aws.greengrass.Nucleus/${PV}/aws.greengrass.nucleus/lib
    ln -s /${GG_BASENAME}/packages/artifacts-unarchived/aws.greengrass.Nucleus/${PV}/aws.greengrass.nucleus ${GG_ROOT}/alts/init/distro

    install -m 0440 ${S}/LICENSE                         ${GG_ROOT}
    install -m 0640 ${S}/../greengrassv2-init.yaml       ${GG_ROOT}/config/config.yaml.clean
    install -m 0640 ${S}/bin/greengrass.service.template ${GG_ROOT}/packages/artifacts-unarchived/aws.greengrass.Nucleus/${PV}/aws.greengrass.nucleus/bin/greengrass.service.template
    install -m 0750 ${S}/bin/loader                      ${GG_ROOT}/packages/artifacts-unarchived/aws.greengrass.Nucleus/${PV}/aws.greengrass.nucleus/bin/loader
    install -m 0640 ${S}/conf/recipe.yaml                ${GG_ROOT}/packages/artifacts-unarchived/aws.greengrass.Nucleus/${PV}/aws.greengrass.nucleus/conf/recipe.yaml
    install -m 0740 ${S}/lib/Greengrass.jar              ${GG_ROOT}/packages/artifacts-unarchived/aws.greengrass.Nucleus/${PV}/aws.greengrass.nucleus/lib/Greengrass.jar

    ln -s /${GG_BASENAME}/alts/init ${GG_ROOT}/alts/current

    # Install systemd service file
    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${S}/bin/greengrass.service.template ${D}${systemd_unitdir}/system/greengrass.service
    sed -i -e "s,REPLACE_WITH_GG_LOADER_FILE,/${GG_BASENAME}/alts/current/distro/bin/loader,g" ${D}${systemd_unitdir}/system/greengrass.service
    sed -i -e "s,REPLACE_WITH_GG_LOADER_PID_FILE,/var/run/greengrass.pid,g" ${D}${systemd_unitdir}/system/greengrass.service

    # Install base Greengrass config
    AWS_DEFAULT_REGION=${GGV2_REGION} \
    PROXY_USER=ggc_user:ggc_group \
    envsubst < ${UNPACKDIR}/config.yaml.template > ${GG_ROOT}/config/config.yaml

}

SYSTEMD_AUTO_ENABLE = "enable"
SYSTEMD_SERVICE:${PN} = "greengrass.service"

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "-r ggc_group"
USERADD_PARAM:${PN} = "-r -M -N -g ggc_group -s /bin/false ggc_user"
GROUP_MEMS_PARAM:${PN} = ""

#
# Disable failing QA checks:
#
#   Binary was already stripped
#   No GNU_HASH in the elf binary
#
# nooelint: oelint.vars.insaneskip
INSANE_SKIP:${PN} += "already-stripped ldflags file-rdeps"

do_merge_config() {
    # Merge configuration fragments from installed components
    python3 -c "
import os
import yaml
import glob

def deep_merge(base_dict, update_dict):
    '''Recursively merge two dictionaries, with special handling for arrays'''
    for key, value in update_dict.items():
        if key in base_dict:
            if isinstance(base_dict[key], dict) and isinstance(value, dict):
                # Recursively merge dictionaries
                deep_merge(base_dict[key], value)
            elif isinstance(base_dict[key], list) and isinstance(value, list):
                # Merge arrays, avoiding duplicates
                for item in value:
                    if item not in base_dict[key]:
                        base_dict[key].append(item)
            else:
                # Overwrite with new value
                base_dict[key] = value
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

# Get recipes that have fragments (from dependency list)
fragment_recipes = '${GREENGRASS_FRAGMENT_RECIPES}'.split()

print(f'Greengrass components/plugins with fragments: {fragment_recipes}')

# Find fragments only from recipes with fragments
fragments_to_merge = []

print('Searching for Greengrass config fragments from components/plugins...')

# Look for fragments in recipe work directories
work_dir = '${TMPDIR}/work'
for recipe in fragment_recipes:
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

addtask merge_config after do_install before do_package

# Ensure merge_config runs after all component deploy tasks (exclude ptest packages to avoid circular deps)
do_merge_config[depends] += "${@' '.join([f'{recipe}:do_deploy' for recipe in (d.getVar('IMAGE_INSTALL') or '').split() + (d.getVar('RDEPENDS:' + d.getVar('PN')) or '').split() if (recipe.startswith('greengrass-component-') or recipe.startswith('greengrass-plugin-')) and not recipe.endswith('-ptest')])}"

# Store list of components/plugins with fragments for merge script
GREENGRASS_FRAGMENT_RECIPES = "${@' '.join([recipe for recipe in (d.getVar('IMAGE_INSTALL') or '').split() + (d.getVar('RDEPENDS:' + d.getVar('PN')) or '').split() if (recipe.startswith('greengrass-component-') or recipe.startswith('greengrass-plugin-')) and not recipe.endswith('-ptest')])}"

# Ensure images rebuild when configuration changes
do_merge_config[vardepsexclude] += "BB_TASKHASH"