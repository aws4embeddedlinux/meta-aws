SUMMARY = "AWS CRT CPP"
DESCRIPTION = "C++ wrapper around the aws-c-* libraries. Provides Cross-Platform Transport Protocols and SSL/TLS implementations for C++."

HOMEPAGE = "https://github.com/awslabs/aws-crt-cpp"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS += "${@bb.utils.contains('PACKAGECONFIG', 'build-deps', 'openssl', '\
    aws-c-auth \
    aws-c-common \
    aws-c-event-stream \
    aws-c-http \
    aws-c-io \
    aws-c-mqtt \
    aws-c-s3 \
    aws-checksums \
    s2n \
', d)}"

PROVIDES += "aws/crt-cpp"

BRANCH ?= "main"

SRC_URI = "\
    gitsm://github.com/awslabs/aws-crt-cpp.git;protocol=https;branch=${BRANCH} \
    ${@bb.utils.contains('PACKAGECONFIG', 'static', '', 'file://001-shared-static-crt-libs.patch', d)} \
    file://run-ptest \
    "

SRCREV = "78c4031387b5be86837e336730f4ef4ab6144815"

S = "${WORKDIR}/git"

inherit cmake pkgconfig ptest

CFLAGS:append = " -Wl,-Bsymbolic"

EXTRA_OECMAKE += "\
    -DCMAKE_MODULE_PATH=${STAGING_LIBDIR}/cmake \
    -DCMAKE_PREFIX_PATH="${STAGING_LIBDIR}/cmake;${STAGING_LIBDIR}" \
    -DUSE_OPENSSL=ON \
    "

# for generating Makefiles to run tests
OECMAKE_GENERATOR = "Unix Makefiles"

# build-deps is enabled by default to use the crt versions that comes as a git submodule,
# this also means that it conflicts with the other standalone versions as it installs the same library if installed separate.
PACKAGECONFIG[build-deps] = "-DBUILD_DEPS=ON,-DBUILD_DEPS=OFF"

PACKAGECONFIG ??= "\
    build-deps \
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
    ${@bb.utils.contains('PACKAGECONFIG', 'build-deps', '${libdir}/s2n/cmake', '', d)} \
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
