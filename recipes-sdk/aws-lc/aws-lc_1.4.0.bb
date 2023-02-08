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
# -AWS Lib Crypto is a fork of BoringSSL, which is itself a fork of OpenSSL. New 
# -files from AWS-LC are made available under an Apache 2.0 license. This license is 
# -reproduced at the bottom of this file. 
# +AWS Lib Crypto is a fork of BoringSSL, which is itself a fork of OpenSSL. New
# +files from AWS-LC are made available under the Apache-2.0 license OR the ISC
# +license. These licenses are reproduced at the bottom of this file.
#   
#  BoringSSL licensing
#  -------------------
# @@ -204,6 +204,17 @@
#     products derived from this software without specific prior
#     written permission.
#  
# +
# +The code in crypto/kyber/pqcrystals-kyber_kyber512_ref carries the
# +Public Domain license:
# +
# +Public Domain (https://creativecommons.org/share-your-work/public-domain/cc0/)
# +
# +For Keccak and AES we are using public-domain
# +code from sources and by authors listed in
# +comments on top of the respective files.
# +
# +
#  Licenses for support code
#  -------------------------
#   
# @@ -453,4 +464,23 @@
#        incurred by, or claims asserted against, such Contributor by reason
#        of your accepting any such warranty or additional liability.
#   
# -   END OF TERMS AND CONDITIONS+   END OF TERMS AND CONDITIONS
# +
# +
# +ISC license for AWS-LC content
# +-------------------------------------
# +
# +
# +Copyright Amazon.com, Inc. or its affiliates.
# +
# +Permission to use, copy, modify, and/or distribute this software for any
# +purpose with or without fee is hereby granted, provided that the above
# +copyright notice and this permission notice appear in all copies.
# +
# +THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
# +WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
# +MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
# +ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
# +WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
# +ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
# +OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
# 
#

SUMMARY = "AWS libcrypto (AWS-LC)"
DESCRIPTION = "AWS-LC is a general-purpose cryptographic library maintained by the AWS Cryptography team for AWS and their customers. It іs based on code from the Google BoringSSL project and the OpenSSL project."

HOMEPAGE = "https://github.com/awslabs/aws-lc"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=974f12032b1ab549ffe204c158bcdcb2"

PROVIDES += "aws/lc"

BRANCH ?= "main"

SRC_URI = "\
    git://github.com/awslabs/aws-lc.git;protocol=https;branch=${BRANCH} \
    file://run-ptest \
    "
SRCREV = "cfbc38bfbd72cfb1a680d0483381b2e93b9a3e73"

S = "${WORKDIR}/git"

inherit cmake ptest pkgconfig

PACKAGECONFIG ??= "\
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'with-tests', '', d)} \
    "

# CMAKE_CROSSCOMPILING=ON will disable building the tests
PACKAGECONFIG[with-tests] = "-DBUILD_TESTING=ON -DCMAKE_CROSSCOMPILING=OFF,-DBUILD_TESTING=OFF,"

# enable PACKAGECONFIG = "static" to build static instead of shared libs
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
