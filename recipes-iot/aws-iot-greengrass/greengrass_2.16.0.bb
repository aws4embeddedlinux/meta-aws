SUMMARY = "AWS IoT Greengrass Nucleus - Built from Source"
DESCRIPTION = "The Greengrass nucleus component provides functionality for device side orchestration of deployments and lifecycle management for execution of Greengrass components and applications. Built from source with custom patches."
HOMEPAGE = "https://github.com/aws-greengrass/aws-greengrass-nucleus"
LICENSE = "Apache-2.0"

CONFLICTS = "greengrass-bin"

require classes/greengrass-common.inc

# Maven dependencies are pre-downloaded and stored in greengrass-maven-deps.inc
# To update after a version upgrade, run: bitbake greengrass -c update_maven_deps
require greengrass-maven-deps.inc

LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

# Build dependencies
DEPENDS += "\
    gettext-native \
    python3-pyyaml-native \
    maven-native \
    jdk-11-native \
"

# enable fleetprovisioning for testing by default to get test coverage
PACKAGECONFIG ??= "${@bb.utils.contains('PTEST_ENABLED', '1', 'fleetprovisioning', '', d)}"

PACKAGECONFIG[fleetprovisioning] = ",,greengrass-plugin-fleetprovisioning,greengrass-plugin-fleetprovisioning"
PACKAGECONFIG[pkcs11] = ",,greengrass-plugin-pkcs11,greengrass-plugin-pkcs11"

SRC_URI = "\
    git://github.com/aws-greengrass/aws-greengrass-nucleus.git;protocol=https;nobranch=1;name=greengrass \
    file://001-service-time-wait.patch \
    file://002-fix-service-exec-and-docker.patch \
    file://greengrassv2-init.yaml \
    file://run-ptest \
    file://config.yaml.template \
    "

# Pin to specific version tag
SRCREV_greengrass = "v${PV}"

UPSTREAM_CHECK_REGEX ?= "releases/tag/v?(?P<pver>\d+(\.\d+)+)"

UPSTREAM_CHECK_URI = "https://github.com/aws-greengrass/aws-greengrass-nucleus/tags"

GG_USESYSTEMD = "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'yes', 'no', d)}"

inherit systemd useradd ptest pkgconfig maven_update_deps

COMPATIBLE_MACHINE:armv7a = "(.*)"
COMPATIBLE_MACHINE:armv7ve = "(.*)"
COMPATIBLE_MACHINE:x86 = "(.*)"
COMPATIBLE_MACHINE:x86-64 = "(.*)"
COMPATIBLE_MACHINE:aarch64 = "(.*)"
COMPATIBLE_MACHINE:riscv64 = "null"

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

# Configure Maven environment
export JAVA_HOME = "${STAGING_LIBDIR_NATIVE}/jvm/jdk-11-native"
export MAVEN_OPTS = "-Xmx2048m -XX:MaxPermSize=512m"

python do_configure() {
    import os
    import shutil
    import re
    
    s_dir = d.getVar("S")
    dl_dir = d.getVar("DL_DIR")
    thisdir = d.getVar("THISDIR")
    bpn = d.getVar("BPN")
    
    # Read the mapping file
    map_file = os.path.join(thisdir, f"{bpn}-maven-map.txt")
    if not os.path.exists(map_file):
        bb.fatal("Maven mapping file not found. Run: bitbake greengrass -c update_maven_deps")
    
    # Create Maven local repository
    maven_repo = os.path.join(s_dir, ".m2", "repository")
    os.makedirs(maven_repo, exist_ok=True)
    
    # Copy artifacts from DL_DIR to Maven repository structure
    snapshot_dirs = set()
    with open(map_file, 'r') as f:
        for line in f:
            line = line.strip()
            if not line:
                continue
            
            download_name, repo_path = line.split(' ', 1)
            src_file = os.path.join(dl_dir, download_name)
            dst_file = os.path.join(maven_repo, repo_path)
            
            if not os.path.exists(src_file):
                bb.fatal(f"Downloaded file not found: {src_file}")
            
            os.makedirs(os.path.dirname(dst_file), exist_ok=True)
            shutil.copy2(src_file, dst_file)
            
            # Track SNAPSHOT directories for symlink creation
            if "SNAPSHOT" in repo_path:
                snapshot_dirs.add(os.path.dirname(dst_file))
    
    # Create generic SNAPSHOT files for Maven (copy from timestamped versions)
    for snapshot_dir in snapshot_dirs:
        # Determine the SNAPSHOT version from directory name
        # e.g., /path/to/artifact/2.1.1-SNAPSHOT/ -> 2.1.1-SNAPSHOT
        snapshot_version = os.path.basename(snapshot_dir)
        
        # Get artifact name from parent directory
        # e.g., /path/to/component-common/2.1.1-SNAPSHOT/ -> component-common
        artifact_name = os.path.basename(os.path.dirname(snapshot_dir))
        
        for timestamped_file in os.listdir(snapshot_dir):
            # Match timestamped SNAPSHOT files: artifact-version-timestamp-buildnum.ext
            # e.g., component-common-2.1.1-20240722.172146-16.jar
            match = re.match(r'(.+)-(\d{8}\.\d{6})-(\d+)\.(jar|pom|xml)$', timestamped_file)
            if match:
                _, timestamp, build_num, ext = match.groups()
                generic_name = f"{artifact_name}-{snapshot_version}.{ext}"
                generic_path = os.path.join(snapshot_dir, generic_name)
                timestamped_path = os.path.join(snapshot_dir, timestamped_file)
                
                # Copy timestamped file to generic name
                if not os.path.exists(generic_path):
                    shutil.copy2(timestamped_path, generic_path)
    
    bb.note(f"Set up Maven local repository at {maven_repo}")
}

do_compile() {
    cd ${S}

    bbnote "Building Greengrass Nucleus with Maven..."
    bbnote "JAVA_HOME: ${JAVA_HOME}"

    # Build with Maven in offline mode using pre-downloaded dependencies
    # -B: batch mode (non-interactive)
    # -o: offline mode
    # -Dmaven.repo.local: use local repository
    # -DskipTests: skip running tests
    # -Dmaven.javadoc.skip: skip javadoc generation
    # -Dmaven.source.skip: skip source jar generation
    mvn -B -o clean package \
        -Dmaven.repo.local=${S}/.m2/repository \
        -DskipTests \
        -Dmaven.javadoc.skip=true \
        -Dmaven.source.skip=true \
        -Dcheckstyle.skip=true \
        -Dspotbugs.skip=true \
        -Dlicense.skip=true

    bbnote "Maven build completed successfully"

    # Verify the JAR was built
    if [ ! -f ${S}/target/Greengrass.jar ]; then
        bbfatal "Greengrass.jar was not built! Check Maven output."
    fi
}

do_install() {
    install -d ${GG_ROOT}/config
    install -d ${GG_ROOT}/alts
    install -d ${GG_ROOT}/alts/init
    install -d ${GG_ROOT}/packages/artifacts-unarchived/aws.greengrass.Nucleus/${PV}/aws.greengrass.nucleus
    install -d ${GG_ROOT}/packages/artifacts-unarchived/aws.greengrass.Nucleus/${PV}/aws.greengrass.nucleus/bin
    install -d ${GG_ROOT}/packages/artifacts-unarchived/aws.greengrass.Nucleus/${PV}/aws.greengrass.nucleus/conf
    install -d ${GG_ROOT}/packages/artifacts-unarchived/aws.greengrass.Nucleus/${PV}/aws.greengrass.nucleus/lib
    ln -s /${GG_BASENAME}/packages/artifacts-unarchived/aws.greengrass.Nucleus/${PV}/aws.greengrass.nucleus ${GG_ROOT}/alts/init/distro

    install -m 0440 ${S}/LICENSE ${GG_ROOT}
    install -m 0640 ${UNPACKDIR}/greengrassv2-init.yaml ${GG_ROOT}/config/config.yaml.clean
    install -m 0640 ${S}/scripts/greengrass.service.template \
        ${GG_ROOT}/packages/artifacts-unarchived/aws.greengrass.Nucleus/${PV}/aws.greengrass.nucleus/bin/greengrass.service.template
    install -m 0750 ${S}/scripts/loader \
        ${GG_ROOT}/packages/artifacts-unarchived/aws.greengrass.Nucleus/${PV}/aws.greengrass.nucleus/bin/loader
    install -m 0640 ${S}/conf/recipe.yaml \
        ${GG_ROOT}/packages/artifacts-unarchived/aws.greengrass.Nucleus/${PV}/aws.greengrass.nucleus/conf/recipe.yaml
    install -m 0740 ${S}/target/Greengrass.jar \
        ${GG_ROOT}/packages/artifacts-unarchived/aws.greengrass.Nucleus/${PV}/aws.greengrass.nucleus/lib/Greengrass.jar

    ln -s /${GG_BASENAME}/alts/init ${GG_ROOT}/alts/current

    # Install systemd service file
    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${S}/scripts/greengrass.service.template ${D}${systemd_unitdir}/system/greengrass.service
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
