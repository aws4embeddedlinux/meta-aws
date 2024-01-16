# FIXME: the LIC_FILES_CHKSUM values have been updated by 'devtool upgrade'.
# The following is the difference between the old and the new license text.
# Please update the LICENSE value if needed, and summarize the changes in
# the commit message via 'License-Update:' tag.
# (example: 'License-Update: copyright years updated.')
#
# The changes:
#
# --- LICENSE.txt
# +++ LICENSE.txt
# @@ -1,6 +1,6 @@
#  MIT License
#  
# -Copyright (c) 2008-2020 Andrey Petrov and contributors (see CONTRIBUTORS.txt)
# +Copyright (c) 2008-2020 Andrey Petrov and contributors.
#  
#  Permission is hereby granted, free of charge, to any person obtaining a copy
#  of this software and associated documentation files (the "Software"), to deal
# 
#

SUMMARY = "Python HTTP library"
DESCRIPTION = "Python HTTP library with thread-safe connection pooling, file post support, sanity friendly, and more"
HOMEPAGE = "https://github.com/shazow/urllib3"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=52d273a3054ced561275d4d15260ecda"

SRC_URI[sha256sum] = "df7aa8afb0148fa78488e7899b2c59b5f4ffcfa82e6c54ccb9dd37c1d7b52d54"

# version 1.x.x
UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>1\.\d+(\.\d+)+)"

inherit pypi setuptools3

PYPI_PACKAGE = "urllib3"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-certifi \
    ${PYTHON_PN}-cryptography \
    ${PYTHON_PN}-email \
    ${PYTHON_PN}-idna \
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-pyopenssl \
    ${PYTHON_PN}-threading \
"

CVE_PRODUCT = "urllib3"

BBCLASSEXTEND = "native nativesdk"

RCONFLICTS:${PN} = "python3-urllib3"

RREPLACES:${PN} = "python3-urllib3 (< 2)"
