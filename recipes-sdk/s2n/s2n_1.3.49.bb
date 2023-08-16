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
# @@ -205,7 +205,7 @@
#  ============================================================================
#      S2N SUBCOMPONENTS:
#  
# -    The s2n Project contains subcomponents with seperate copyright notices
# +    The s2n Project contains subcomponents with separate copyright notices
#      and license terms. Your use of the source code for these subcomponents is
#      subject to the terms and conditions of the following licenses.
#  
# 
#

SUMMARY = "s2n"
DESCRIPTION = "s2n is a C99 implementation of the TLS/SSL protocols that is designed to be simple, small, fast, and with security as a priority."
HOMEPAGE = "https://github.com/aws/s2n-tls"
LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=1423a53b96536406d47f23fa077ac57e"

DEPENDS = "${@bb.utils.contains('PACKAGECONFIG', 'static', 'aws-lc', 'openssl', d)}"

PROVIDES += "aws/s2n"

BRANCH ?= "main"
SRC_URI = "\
    git://github.com/aws/s2n-tls.git;protocol=https;branch=${BRANCH} \
    file://run-ptest \
    "

SRCREV = "cb32a54dd95e38088b3873d4cc9b05bc4e8d77e3"
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>.*)"

S = "${WORKDIR}/git"

inherit cmake ptest pkgconfig

CFLAGS:append = " -Wl,-Bsymbolic"

PACKAGECONFIG ??= "\
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'with-tests', '', d)} \
    "

# enable PACKAGECONFIG = "static" to build static instead of shared libs
PACKAGECONFIG[static] = "-DBUILD_SHARED_LIBS=OFF,-DBUILD_SHARED_LIBS=ON"

PACKAGECONFIG[with-tests] = "-DBUILD_TESTING=ON,-DBUILD_TESTING=OFF,"

EXTRA_OECMAKE += "\
    -DCMAKE_BUILD_TYPE=Release \
"
# Fix "doesn't have GNU_HASH (didn't pass LDFLAGS?)" issue
TARGET_CC_ARCH += "${LDFLAGS}"

# Assume that warnings from upstream have already been evaluated
EXTRA_OECMAKE += "-DUNSAFE_TREAT_WARNINGS_AS_ERRORS=OFF"

FILES:${PN}-dev += "${libdir}/*/cmake"

RDEPENDS:${PN}-ptest += "openssl"

do_install_ptest () {
   install -d ${D}${PTEST_PATH}/tests
   cp -r ${B}/bin/* ${D}${PTEST_PATH}/tests/
}

BBCLASSEXTEND = "native nativesdk"
