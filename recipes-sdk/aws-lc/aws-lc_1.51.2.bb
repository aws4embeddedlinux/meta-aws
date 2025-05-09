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
# @@ -215,6 +215,9 @@
#  code from sources and by authors listed in
#  comments on top of the respective files.
#  
# +The code in crypto/fipsmodule/ml_kem/mlkem is imported from mlkem-native
# +(https://github.com/pq-code-package/mlkem-native) and carries the
# +Apache 2.0 license. This license is reproduced at the bottom of this file.
#  
#  Licenses for support code
#  -------------------------
# @@ -286,10 +289,10 @@
#  OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
#   
#   
# -Apache 2.0 license for AWS-LC content
# --------------------------------------
# - 
# - 
# + 
# + 
# +Apache 2.0 license for AWS-LC content and mlkem-native
# +------------------------------------------------------
#                                   Apache License
#                             Version 2.0, January 2004
#                          http://www.apache.org/licenses/
# 
#

SUMMARY = "AWS libcrypto (AWS-LC)"
DESCRIPTION = "AWS-LC is a general-purpose cryptographic library maintained by the AWS Cryptography team for AWS and their customers. It іs based on code from the Google BoringSSL project and the OpenSSL project."

HOMEPAGE = "https://github.com/awslabs/aws-lc"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4d2ce8260a297b70a89c38c25266076e"

PROVIDES += "aws/lc"

BRANCH ?= "main"

SRC_URI = "\
    git://github.com/awslabs/aws-lc.git;protocol=https;branch=${BRANCH} \
    file://run-ptest \
    "
SRCREV = "a614f97527d16461d5c904ef90d3bb647e35265f"
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>.*)"

S = "${UNPACKDIR}/git"

inherit cmake ptest pkgconfig

PACKAGECONFIG ??= "\
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'with-tests', '', d)} \
    "

# CMAKE_CROSSCOMPILING=ON will disable building the tests
PACKAGECONFIG[with-tests] = "-DBUILD_TESTING=ON -DCMAKE_CROSSCOMPILING=OFF,-DBUILD_TESTING=OFF,"

# enable PACKAGECONFIG = "static" to build static instead of shared libs
# this will conflict with PTESTS, do disable them in your local.conf
# by setting
# PTEST_ENABLED:pn-aws-lc = "0"
PACKAGECONFIG[static] = "-DBUILD_SHARED_LIBS=OFF,-DBUILD_SHARED_LIBS=ON"

do_install_ptest () {
   install -d ${D}${PTEST_PATH}/tests
   cp -r ${B}/libboringssl_gtest.so ${D}${PTEST_PATH}/tests/
   install -m 0755 ${B}/ssl/ssl_test ${D}${PTEST_PATH}/tests/
}

EXTRA_OECMAKE += "\
    -DDISABLE_PERL=ON \
    -DDISABLE_GO=ON \
"

CXXFLAGS += "-Wno-ignored-attributes"

FILES:${PN} += "\
    ${libdir}/libcrypto.so \
    ${libdir}/libssl.so \
    ${libdir}/libdecrepit.so \
    "

FILES:${PN}-dev += "${libdir}/*/cmake"

# also test depend-on-us packages build
RDEPENDS:${PN}-ptest = "\
    aws-c-auth \
    aws-c-cal \
    aws-c-compression \
    aws-c-io \
    aws-c-s3 \
    aws-checksums \
    "

# Notify that libraries are not versioned
FILES_SOLIBSDEV = ""

BBCLASSEXTEND = "native nativesdk"

inherit update-alternatives
ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE:${PN} = "openssl"

ALTERNATIVE_TARGET[openssl] = "${bindir}/openssl"
