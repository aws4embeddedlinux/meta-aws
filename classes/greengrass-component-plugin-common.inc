# Common Greengrass definitions
# This file provides basic definitions used by all Greengrass recipes

require greengrass-base.inc

# Ensure all Greengrass plugins/components wait for greengrass-bin to install base structure
python __anonymous() {
    pn = d.getVar('PN')
    if pn and pn != 'greengrass-bin*':
        # Add dependency on greengrass-bin's do_install task
        d.appendVarFlag('do_install', 'depends', ' greengrass-bin:do_install')
}
