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
# @@ -200,25 +200,3 @@
#     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#     See the License for the specific language governing permissions and
#     limitations under the License.
# -
# -
# -============================================================================
# -    S2N SUBCOMPONENTS:
# -
# -    The s2n Project contains subcomponents with separate copyright notices
# -    and license terms. Your use of the source code for these subcomponents is
# -    subject to the terms and conditions of the following licenses.
# -
# -
# -========================================================================
# -Third party MIT licenses
# -========================================================================
# -
# -The following components are provided under the MIT License. See project link for details.
# -
# -
# -    SIKE
# -      -> s2n/pq-crypto/sike_r1/LICENSE.txt
# -
# -
# -
#
#

SUMMARY = "s2n"
DESCRIPTION = "s2n is a C99 implementation of the TLS/SSL protocols that is designed to be simple, small, fast, and with security as a priority."
HOMEPAGE = "https://github.com/aws/s2n-tls"
LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS = "${@bb.utils.contains('PACKAGECONFIG', 'static', 'aws-lc', 'openssl', d)}"

PROVIDES += "aws/s2n"

BRANCH ?= "main"
SRC_URI = "\
    git://github.com/aws/s2n-tls.git;protocol=https;branch=${BRANCH} \
    file://run-ptest \
    "

SRCREV = "87f4a0585dc3056433f193b9305f587cff239be3"
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

RDEPENDS:${PN}-ptest += "\
    bash \
    openssl \
    "

do_install_ptest () {
   install -d ${D}${PTEST_PATH}/tests
   cp -r ${B}/bin/* ${D}${PTEST_PATH}/tests/
   cp -r ${S}/tests/pems ${D}${PTEST_PATH}/
}

BBCLASSEXTEND = "native nativesdk"
