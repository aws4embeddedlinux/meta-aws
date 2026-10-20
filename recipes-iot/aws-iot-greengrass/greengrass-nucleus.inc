# Shared logic for the two Greengrass Nucleus recipes: greengrass (built from
# source) and greengrass-bin (binary distribution). Both lay out the same
# on-device Nucleus tree, user, systemd service and config/fragment merge; they
# differ only in how the Nucleus artifacts are produced. Each recipe sets the
# GG_NUCLEUS_* paths below and requires this file.

require classes/greengrass-common.inc

inherit systemd useradd ptest pkgconfig

DEPENDS += "\
    gettext-native \
    python3-pyyaml-native \
"

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

# enable fleetprovisioning for testing by default to get test coverage
PACKAGECONFIG ??= "${@bb.utils.contains('PTEST_ENABLED', '1', 'fleetprovisioning', '', d)}"

PACKAGECONFIG[fleetprovisioning] = ",,greengrass-plugin-fleetprovisioning,greengrass-plugin-fleetprovisioning"
PACKAGECONFIG[pkcs11] = ",,greengrass-plugin-pkcs11,greengrass-plugin-pkcs11"

UPSTREAM_CHECK_REGEX ?= "releases/tag/v?(?P<pver>\d+(\.\d+)+)"
UPSTREAM_CHECK_URI = "https://github.com/aws-greengrass/aws-greengrass-nucleus/tags"

GG_USESYSTEMD = "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'yes', 'no', d)}"

# Nucleus artifact locations, set by each recipe (built vs downloaded):
#   GG_NUCLEUS_BINDIR : directory holding loader and greengrass.service.template
#   GG_NUCLEUS_JAR    : the Greengrass.jar to ship
#   GG_INIT_YAML      : greengrassv2-init.yaml, installed as config.yaml.clean

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
    install -m 0640 ${GG_INIT_YAML} ${GG_ROOT}/config/config.yaml.clean
    install -m 0640 ${GG_NUCLEUS_BINDIR}/greengrass.service.template \
        ${GG_ROOT}/packages/artifacts-unarchived/aws.greengrass.Nucleus/${PV}/aws.greengrass.nucleus/bin/greengrass.service.template
    install -m 0750 ${GG_NUCLEUS_BINDIR}/loader \
        ${GG_ROOT}/packages/artifacts-unarchived/aws.greengrass.Nucleus/${PV}/aws.greengrass.nucleus/bin/loader
    install -m 0640 ${S}/conf/recipe.yaml \
        ${GG_ROOT}/packages/artifacts-unarchived/aws.greengrass.Nucleus/${PV}/aws.greengrass.nucleus/conf/recipe.yaml
    install -m 0740 ${GG_NUCLEUS_JAR} \
        ${GG_ROOT}/packages/artifacts-unarchived/aws.greengrass.Nucleus/${PV}/aws.greengrass.nucleus/lib/Greengrass.jar

    ln -s /${GG_BASENAME}/alts/init ${GG_ROOT}/alts/current

    # Install systemd service file
    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${GG_NUCLEUS_BINDIR}/greengrass.service.template ${D}${systemd_unitdir}/system/greengrass.service
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

# Merge the config fragments contributed by the installed plugins/components
# into the Nucleus config.yaml. Fragments are read from DEPLOY_DIR_IMAGE (the
# deploy-class sstate output), which is populated whether do_deploy ran or was
# restored from sstate. The previous implementation globbed each recipe's work
# directory (the sstate input), which does not exist when a plugin is restored
# from sstate (setscene); the merge then found nothing and shipped an unmerged
# config.
python do_merge_config() {
    import os
    import yaml
    import oe.sstatesig

    def deep_merge(base, update):
        '''Recursively merge two dictionaries, with special handling for arrays'''
        for key, value in update.items():
            if key in base:
                if isinstance(base[key], dict) and isinstance(value, dict):
                    deep_merge(base[key], value)
                elif isinstance(base[key], list) and isinstance(value, list):
                    for item in value:
                        if item not in base[key]:
                            base[key].append(item)
                else:
                    base[key] = value
            else:
                base[key] = value
        return base

    def load_yaml_file(path):
        # config.yaml and the fragments may be multi-document streams
        merged = {}
        with open(path) as f:
            for doc in yaml.safe_load_all(f):
                if doc:
                    deep_merge(merged, doc)
        return merged

    fragment_recipes = (d.getVar('GREENGRASS_FRAGMENT_RECIPES') or '').split()
    fragment_dir = os.path.normpath(os.path.join(d.getVar('DEPLOY_DIR_IMAGE'), 'greengrass-plugin-fragments'))
    bb.note('Greengrass components/plugins with fragments: %s' % fragment_recipes)

    # DEPLOY_DIR_IMAGE is shared and keeps fragments of recipes that were dropped
    # from the build, so take only the files that each recipe's own do_deploy
    # sstate manifest lists.
    fragments = []
    for recipe in fragment_recipes:
        manifest, _ = oe.sstatesig.find_sstate_manifest(recipe, recipe, 'deploy', d, {})
        if not manifest or not os.path.exists(manifest):
            bb.fatal('%s: deploy sstate manifest not found; its config fragment could not be resolved' % recipe)
        with open(manifest) as f:
            deployed = [os.path.normpath(line.strip()) for line in f if line.strip()]
        for path in deployed:
            if os.path.dirname(path) != fragment_dir or not path.endswith('.yaml'):
                continue
            if not os.path.isfile(path):
                bb.fatal('%s: config fragment %s listed in %s does not exist' % (recipe, path, manifest))
            bb.note('Found config fragment: %s -> %s' % (recipe, path))
            fragments.append(path)

    base_config_path = os.path.join(d.getVar('GG_ROOT'), 'config', 'config.yaml')
    merged_config = load_yaml_file(base_config_path)
    for fragment_path in fragments:
        deep_merge(merged_config, load_yaml_file(fragment_path))
        bb.note('Merged fragment: %s' % os.path.basename(fragment_path))

    with open(base_config_path, 'w') as f:
        yaml.dump(merged_config, f, default_flow_style=False, sort_keys=False)

    components = [key for key in (merged_config.get('services') or {}) if key.startswith('com.')]
    if components:
        bb.note('Components in merged configuration: ' + ', '.join(components))
}

addtask merge_config after do_install before do_package

# Ensure merge_config runs after all component deploy tasks (exclude ptest packages to avoid circular deps)
do_merge_config[depends] += "${@' '.join([f'{recipe}:do_deploy' for recipe in (d.getVar('IMAGE_INSTALL') or '').split() + (d.getVar('RDEPENDS:' + d.getVar('PN')) or '').split() if (recipe.startswith('greengrass-component-') or recipe.startswith('greengrass-plugin-')) and not recipe.endswith('-ptest')])}"

# Store list of components/plugins with fragments for the merge
GREENGRASS_FRAGMENT_RECIPES = "${@' '.join([recipe for recipe in (d.getVar('IMAGE_INSTALL') or '').split() + (d.getVar('RDEPENDS:' + d.getVar('PN')) or '').split() if (recipe.startswith('greengrass-component-') or recipe.startswith('greengrass-plugin-')) and not recipe.endswith('-ptest')])}"

# Ensure images rebuild when configuration changes
do_merge_config[vardepsexclude] += "BB_TASKHASH"

# DEPLOY_DIR_IMAGE is an absolute, MACHINE-specific path and is not in
# BB_HASHEXCLUDE_COMMON, so keep it out of the task signature.
do_merge_config[vardepsexclude] += "DEPLOY_DIR_IMAGE"
