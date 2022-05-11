# -*- mode: Conf; -*-
SUMMARY = "AWS CRT Python"
DESCRIPTION = "Python bindings for the AWS Common Runtime"
HOMEPAGE = "https://github.com/awslabs/aws-crt-python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

BRANCH ?= "main"
SRC_URI = "gitsm://github.com/awslabs/aws-crt-python.git;protocol=https;branch=${BRANCH} \
           file://fix-library-suffix.patch \
"

SRCREV = "555e2b802f295ea93eab6e5d8d19a91744b7e72c"
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>.*)"

S = "${WORKDIR}/git"

inherit setuptools3


AWS_C_INSTALL = "${D}/usr/lib;${S}/source"
DEPENDS += "cmake-native ${PYTHON_PN}-setuptools-native s2n aws-c-common aws-c-io aws-c-mqtt aws-c-auth aws-c-http aws-checksums aws-c-event-stream aws-c-s3 aws-lc"
RDEPENDS:${PN} = "python3-core s2n aws-c-common aws-c-io aws-c-mqtt aws-c-auth aws-c-http aws-checksums aws-c-event-stream"
CFLAGS:append = " -Wl,-Bsymbolic"

# -Werror will cause similiar issue as here: https://github.com/awslabs/aws-lc/issues/487
# and a compile note is shown
# ...but Currently, GCC 12.1.0 is not supported due to a memcmp related bug reported
# in https://gcc.gnu.org/bugzilla/show_bug.cgi?id=95189.
# We strongly recommend against using the GCC 12.1.0 compiler.

# suppressing errors
CFLAGS:append = " -Wno-array-bounds  -Wno-stringop-overflow"

# and set as suggested
# MEMCMP_INVALID_STRIPPED', and compile_flags '-O2 -g -DNDEBUG'
CFLAGS:append = " -DMEMCMP_INVALID_STRIPPED -O2 -g -DNDEBUG"
