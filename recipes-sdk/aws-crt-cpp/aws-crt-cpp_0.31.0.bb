SUMMARY = "AWS CRT CPP"
DESCRIPTION = "C++ wrapper around the aws-c-* libraries. Provides Cross-Platform Transport Protocols and SSL/TLS implementations for C++."

HOMEPAGE = "https://github.com/awslabs/aws-crt-cpp"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS += "\
    aws-c-auth \
    aws-c-common \
    aws-c-event-stream \
    aws-c-http \
    aws-c-io \
    aws-c-mqtt \
    aws-c-s3 \
    aws-checksums \
    s2n \
    "

PROVIDES += "aws/crt-cpp"

BRANCH ?= "main"

SRC_URI = "\
    git://github.com/awslabs/aws-crt-cpp.git;protocol=https;branch=${BRANCH} \
    file://run-ptest \
    "

SRCREV = "c776ab5b38036662ebd0569d2483b98a176f9819"

S = "${WORKDIR}/git"

inherit cmake pkgconfig ptest

CFLAGS:append = " -Wl,-Bsymbolic"

EXTRA_OECMAKE += "\
    -DCMAKE_MODULE_PATH=${STAGING_LIBDIR}/cmake \
    -DCMAKE_PREFIX_PATH="${STAGING_LIBDIR}/cmake;${STAGING_LIBDIR}" \
    -DBUILD_DEPS=OFF \
    "

# for generating Makefiles to run tests
OECMAKE_GENERATOR = "Unix Makefiles"

PACKAGECONFIG ?= "\
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'with-tests', '', d)} \
    "

PACKAGECONFIG:append:x86-64 = " ${@bb.utils.contains('PTEST_ENABLED', '1', 'sanitize', '', d)}"

EXTRA_OECMAKE += "${@bb.utils.contains('PACKAGECONFIG', 'sanitize', '-DCMAKE_BUILD_TYPE=Debug', '-DCMAKE_BUILD_TYPE=Release', d)}"

# enable PACKAGECONFIG = "static" to build static instead of shared libs
PACKAGECONFIG[static] = "-DBUILD_SHARED_LIBS=OFF,-DBUILD_SHARED_LIBS=ON"

# CMAKE_CROSSCOMPILING=ON will disable building the tests
PACKAGECONFIG[with-tests] = "-DBUILD_TESTING=ON -DCMAKE_CROSSCOMPILING=OFF,-DBUILD_TESTING=OFF,"

FILES:${PN} += "${libdir}/libaws-crt-cpp.so"
FILES:${PN}-dev += "\
    ${includedir}/aws/crt/* \
    ${libdir}/aws-crt-cpp/* \
    ${includedir}/aws/iot/* \
    "

# Notify that libraries are not versioned
FILES_SOLIBSDEV = ""

BBCLASSEXTEND = "native nativesdk"

do_install_ptest () {
    install -d ${D}${PTEST_PATH}/tests
    install -m 0755 ${B}/tests/aws-crt-cpp-tests ${D}${PTEST_PATH}/tests/
}

# -fsanitize=address does cause this
# nooelint: oelint.vars.insaneskip:INSANE_SKIP
INSANE_SKIP += "${@bb.utils.contains('PACKAGECONFIG', 'sanitize', 'buildpaths', '', d)}"

PACKAGECONFIG[sanitize] = ",, gcc-sanitizers"
OECMAKE_CXX_FLAGS += "${@bb.utils.contains('PACKAGECONFIG', 'sanitize', '-fsanitize=address,undefined -fno-omit-frame-pointer', '', d)}"
