SUMMARY = "AWS libcrypto (AWS-LC)"
DESCRIPTION = "AWS-LC is a general-purpose cryptographic library maintained by the AWS Cryptography team for AWS and their customers. It іs based on code from the Google BoringSSL project and the OpenSSL project."

HOMEPAGE = "https://github.com/awslabs/aws-lc"
LICENSE = "Apache-2.0"
PROVIDES += "aws/lc"

inherit cmake

LIC_FILES_CHKSUM = "file://LICENSE;md5=c91257e3cc0bd6026b93fb15aecf6f1c"

BRANCH ?= "main"

SRC_URI = "git://github.com/awslabs/aws-lc.git;protocol=https;branch=${BRANCH}"
SRCREV = "038628d24feab1c4fce75c9b6a47bfa99b7c5edc"

S = "${WORKDIR}/git"

DEPENDS = ""
RDEPENDS:${PN} = ""

EXTRA_OECMAKE += "-DBUILD_TESTING=OFF"
EXTRA_OECMAKE += "-DDISABLE_PERL=ON"
EXTRA_OECMAKE += "-DDISABLE_GO=ON"

EXTRA_OECMAKE += "-DCMAKE_PREFIX_PATH=${libdir}/aws-lc"
EXTRA_OECMAKE += "-DCMAKE_INSTALL_PREFIX=${libdir}/aws-lc"
EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=ON"


FILES:${PN}     = "${libdir}/aws-lc/lib/libssl.so \
                   ${libdir}/aws-lc/lib/libcrypto.so \
                   ${libdir}/aws-lc/lib/libdecrepit.so"
FILES:${PN}-dev = "${libdir}/aws-lc/include/openssl/* \
                   ${libdir}/aws-lc/lib/ssl/* \
                   ${libdir}/aws-lc/lib/AWSLC/* \
                   ${libdir}/aws-lc/lib/crypto/*"
FILES:${PN}-dbg = "$D{$prefix}/src/debug/aws-lc/*"

BBCLASSEXTEND = "native nativesdk"

# -Werror will cause https://github.com/awslabs/aws-lc/issues/487
OECMAKE_C_FLAGS += "-Wno-array-parameter"
