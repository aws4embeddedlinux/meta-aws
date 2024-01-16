# FIXME: the LIC_FILES_CHKSUM values have been updated by 'devtool upgrade'.
# The following is the difference between the old and the new license text.
# Please update the LICENSE value if needed, and summarize the changes in
# the commit message via 'License-Update:' tag.
# (example: 'License-Update: copyright years updated.')
#
# The changes:
#
# --- LICENSE
# +++ LICENSE
# @@ -1,6 +1,6 @@
#   The MIT License (MIT)
#  
# - Copyright (c) 2019-2022 Anthon van der Neut, Ruamel bvba
# + Copyright (c) 2019-2023 Anthon van der Neut, Ruamel bvba
#  
#   Permission is hereby granted, free of charge, to any person obtaining a copy
#   of this software and associated documentation files (the "Software"), to deal
# 
#

SUMMARY = "YAML parser/emitter"
DESCRIPTION = "YAML parser/emitter that supports roundtrip preservation of comments, seq/map flow style, and map key order."
HOMEPAGE = "https://pypi.org/project/ruamel.yaml.clib/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1debc1593104ded7e88b0ac5659af552"

PYPI_PACKAGE = "ruamel.yaml.clib"

inherit pypi setuptools3

SRC_URI[sha256sum] = "beb2e0404003de9a4cab9753a8805a8fe9320ee6673136ed7f04255fe60bb512"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-shell \
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-netclient \
"

do_install:prepend() {
    export RUAMEL_NO_PIP_INSTALL_CHECK=1
}

BBCLASSEXTEND = "native nativesdk"
