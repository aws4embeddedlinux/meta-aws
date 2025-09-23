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
# @@ -215,9 +215,52 @@
#  code from sources and by authors listed in
#  comments on top of the respective files.
#  
# +mlkem-native licensing
# +----------------------
# +
#  The code in crypto/fipsmodule/ml_kem/mlkem is imported from mlkem-native
# -(https://github.com/pq-code-package/mlkem-native) and carries the
# -Apache 2.0 license. This license is reproduced at the bottom of this file.
# +(https://github.com/pq-code-package/mlkem-native) and made available
# +under the Apache-2.0 license OR the ISC license OR the MIT license.
# +
# +ISC license for mlkem-native content:
# +
# +Copyright (c) The mlkem-native project authors
# +
# +Permission to use, copy, modify, and/or distribute this software for any purpose
# +with or without fee is hereby granted, provided that the above copyright notice
# +and this permission notice appear in all copies.
# +
# +THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH
# +REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND
# +FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
# +INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS
# +OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
# +TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF
# +THIS SOFTWARE.
# +
# +MIT license for mlkem-native content:
# +
# +Copyright (c) The mlkem-native project authors
# +
# +Permission is hereby granted, free of charge, to any person obtaining a copy of
# +this software and associated documentation files (the “Software”), to deal in
# +the Software without restriction, including without limitation the rights to
# +use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
# +the Software, and to permit persons to whom the Software is furnished to do so,
# +subject to the following conditions:
# +
# +The above copyright notice and this permission notice shall be included in all
# +copies or substantial portions of the Software.
# +
# +THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# +IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
# +FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
# +COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
# +IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
# +CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
# +
# +The Apache-2.0 license for mlkem-native content is reproduced at
# +the bottom of this file.
#  
#  Licenses for support code
#  -------------------------
# 
#

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
LIC_FILES_CHKSUM = "file://LICENSE;md5=4ae91dadf6b1388279f17467b5bf4fe6"

PROVIDES += "aws/lc"

BRANCH ?= "main"

SRC_URI = "\
    git://github.com/awslabs/aws-lc.git;protocol=https;branch=${BRANCH} \
    file://run-ptest \
    "
SRCREV = "c3605ae40645673286b4cab69fcc59eb9d236a24"
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>.*)"

S = "${WORKDIR}/git"

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

EXTRA_OECMAKE += "-DCMAKE_C_FLAGS='${CFLAGS}'"

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
