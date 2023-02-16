SUMMARY = "s2n"
DESCRIPTION = "s2n is a C99 implementation of the TLS/SSL protocols that is designed to be simple, small, fast, and with security as a priority."
HOMEPAGE = "https://github.com/aws/s2n-tls"
LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=26d85861cd0c0d05ab56ebff38882975"

DEPENDS += "openssl"

PROVIDES += "aws/s2n"

BRANCH ?= "main"
SRC_URI = "\
    git://github.com/aws/s2n-tls.git;protocol=https;branch=${BRANCH} \
    file://run-ptest \
    "

SRCREV = "0725d3c0bb5bc1383310e19dd94c821a9234d299"
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>.*)"

S = "${WORKDIR}/git"

inherit cmake ptest

CFLAGS:append = " -Wl,-Bsymbolic"

EXTRA_OECMAKE += "\
    -DBUILD_TESTING=OFF \
    -DCMAKE_BUILD_TYPE=Release \
    -DCMAKE_INSTALL_PREFIX=$D/usr \
    -DBUILD_SHARED_LIBS=ON \
"
# Fix "doesn't have GNU_HASH (didn't pass LDFLAGS?)" issue
TARGET_CC_ARCH += "${LDFLAGS}"

# Assume that warnings from upstream have already been evaluated
EXTRA_OECMAKE += "-DUNSAFE_TREAT_WARNINGS_AS_ERRORS=OFF"

FILES:${PN}-dev += "${libdir}/*/cmake"

RDEPENDS:${PN}-ptest += "\
    aws-c-iot \
    bash \
    ldd \
"

BBCLASSEXTEND = "native nativesdk"
