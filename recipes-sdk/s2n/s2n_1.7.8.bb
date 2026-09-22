SUMMARY = "s2n"
DESCRIPTION = "s2n is a C99 implementation of the TLS/SSL protocols that is designed to be simple, small, fast, and with security as a priority."
HOMEPAGE = "https://github.com/aws/s2n-tls"
LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

# aws-lc is the preferred crypto backend (enables OpenSSL 4.0 coexistence
# via ENABLE_DIST_PKG). Note: aws-lc is not available on arm32
# (COMPATIBLE_HOST:arm = "null" in aws-lc recipe).
DEPENDS = "aws-lc"

PROVIDES += "aws/s2n"

BRANCH ?= "main"
SRC_URI = "\
    git://github.com/aws/s2n-tls.git;protocol=https;branch=${BRANCH} \
    file://run-ptest \
    "

SRCREV = "3b975bf3fd9a50def97573afa859e8eb83931928"
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>.*)"

inherit cmake ptest pkgconfig

CFLAGS:append = " -Wl,-Bsymbolic"

PACKAGECONFIG ?= "\
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'with-tests', '', d)} \
    "

PACKAGECONFIG:append:x86-64 = " ${@bb.utils.contains('PTEST_ENABLED', '1', 'sanitize', '', d)}"

# enable PACKAGECONFIG = "static" to build static instead of shared libs
PACKAGECONFIG[static] = "-DBUILD_SHARED_LIBS=OFF,-DBUILD_SHARED_LIBS=ON"

PACKAGECONFIG[with-tests] = "-DBUILD_TESTING=ON,-DBUILD_TESTING=OFF,"

PACKAGECONFIG[sanitize] = "-DS2N_ADDRESS_SANITIZER=ON, -DS2N_ADDRESS_SANITIZER=OFF, gcc-sanitizers"

EXTRA_OECMAKE:append = " -DCMAKE_BUILD_TYPE=RelWithDebInfo"

# Fix "doesn't have GNU_HASH (didn't pass LDFLAGS?)" issue
TARGET_CC_ARCH += "${LDFLAGS}"

# Assume that warnings from upstream have already been evaluated
EXTRA_OECMAKE += "-DUNSAFE_TREAT_WARNINGS_AS_ERRORS=OFF"

# Use aws-lc via ENABLE_DIST_PKG. Prefer CMake config-mode packages over
# find-modules so that find_package(crypto) uses aws-lc's installed
# crypto-config.cmake rather than s2n's Findcrypto.cmake fallback (which
# searches for openssl/crypto.h and libcrypto.so — paths that don't exist
# with aws-lc's dist-pkg layout).
EXTRA_OECMAKE += "-DCMAKE_FIND_PACKAGE_PREFER_CONFIG=ON"

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

OECMAKE_CXX_FLAGS += "${@bb.utils.contains('PACKAGECONFIG', 'sanitize', '-fsanitize=address,undefined', '', d)}"
