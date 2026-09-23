SUMMARY = "AWS CRT Python"
DESCRIPTION = "Python bindings for the AWS Common Runtime"
HOMEPAGE = "https://github.com/awslabs/aws-crt-python"
BUGTRACKER = "https://github.com/awslabs/aws-crt-python/issues"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"
CVE_PRODUCT = "aws-crt-python"

# nooelint: oelint.vars.dependsordered
DEPENDS += "\
    python3-setuptools-native \
    ${@bb.utils.contains('PACKAGECONFIG', 'no-buildin-sdk', '\
    aws-checksums \
    aws-c-auth \
    aws-c-cal \
    aws-c-common \
    aws-c-compression \
    aws-c-event-stream \
    aws-c-http \
    aws-c-io \
    aws-c-mqtt \
    aws-c-s3 \
    aws-c-sdkutils \
    s2n \
    ', '', d)} \
    aws-lc \
    "

SRC_URI = "\
    ${@bb.utils.contains('PACKAGECONFIG', 'no-buildin-sdk', \
     'git://github.com/awslabs/aws-crt-python.git;protocol=https;branch=main',\
     'gitsm://github.com/awslabs/aws-crt-python.git;protocol=https;branch=main', \
    d)} \
    file://001-fix-cross-compilation-support.patch \
    file://run-ptest \
    "

SRCREV = "241be07f82ebd3031a812492443d545676cb2018"
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>.*)"

inherit setuptools3_legacy ptest
# nooelint: oelint.vars.specific
COMPATIBLE_HOST:arm = "null"

CFLAGS:append = " -Wl,-Bsymbolic"

# https://github.com/aws4embeddedlinux/meta-aws/issues/13929
# nooelint: oelint.vars.specific
LDFLAGS:append:arm = " ${@bb.utils.contains('PACKAGECONFIG', 'no-buildin-sdk', '', ' -latomic', d)}"
LDFLAGS:append:mips = " ${@bb.utils.contains('PACKAGECONFIG', 'no-buildin-sdk', '', ' -latomic', d)}"
LDFLAGS:append:mipsel = " ${@bb.utils.contains('PACKAGECONFIG', 'no-buildin-sdk', '', ' -latomic', d)}"
LDFLAGS:append:powerpc = " ${@bb.utils.contains('PACKAGECONFIG', 'no-buildin-sdk', '', ' -latomic', d)}"
LDFLAGS:append:riscv32 = " ${@bb.utils.contains('PACKAGECONFIG', 'no-buildin-sdk', '', ' -latomic', d)}"

# use the libcrypto included on your system
export AWS_CRT_BUILD_USE_SYSTEM_LIBCRYPTO = "1"

# When vendoring (no no-buildin-sdk), create static libs to not conflict
# with system ones. When using system SDK, link against shared libs.
export AWS_CRT_BUILD_FORCE_STATIC_LIBS = "${@bb.utils.contains('PACKAGECONFIG', 'no-buildin-sdk', '0', '1', d)}"

# Use system-installed SDK libraries instead of vendoring from git submodules.
# This ensures aws-crt-python links against the same aws-lc (ENABLE_DIST_PKG)
# as the rest of the SDK chain.
PACKAGECONFIG ??= "no-buildin-sdk"
PACKAGECONFIG[no-buildin-sdk] = ",,"

do_configure:prepend(){
    sed -i "s/__version__ = '1.0.0.dev0'/__version__ = '${PV}'/" ${S}/awscrt/__init__.py
}

# Create CMake toolchain file for cross-compilation as setuptools is using cmake internally
# and we can not inherit cmake class as this conflicts with setuptools3_legacy
do_compile:prepend(){

    if [ "${PN}" != "${BPN}-native" ]; then
        cat > ${WORKDIR}/toolchain.cmake << 'EOF'
set(CMAKE_SYSTEM_NAME Linux)
set(CMAKE_SYSTEM_PROCESSOR ${TARGET_ARCH})
set(CMAKE_SYSROOT ${STAGING_DIR_TARGET})

set(CMAKE_C_COMPILER ${CC})
set(CMAKE_CXX_COMPILER ${CXX})

set(CMAKE_FIND_ROOT_PATH ${STAGING_DIR_TARGET})
set(CMAKE_FIND_ROOT_PATH_MODE_PROGRAM NEVER)
set(CMAKE_FIND_ROOT_PATH_MODE_LIBRARY ONLY)
set(CMAKE_FIND_ROOT_PATH_MODE_INCLUDE ONLY)
set(CMAKE_FIND_ROOT_PATH_MODE_PACKAGE ONLY)

set(CMAKE_C_FLAGS "${CFLAGS}" CACHE STRING "C flags")
set(CMAKE_CXX_FLAGS "${CXXFLAGS}" CACHE STRING "CXX flags")
set(CMAKE_EXE_LINKER_FLAGS "${LDFLAGS}" CACHE STRING "Linker flags")

# Use aws-lc from the target sysroot (installed with ENABLE_DIST_PKG)
# Prefer config-mode packages so find_package(crypto) uses aws-lc's
# crypto-config.cmake rather than searching for openssl-style paths.
set(CMAKE_FIND_PACKAGE_PREFER_CONFIG ON)
set(CMAKE_PREFIX_PATH "${STAGING_DIR_TARGET}/usr/lib/cmake;${STAGING_DIR_TARGET}/usr/lib" CACHE STRING "Prefix path")
EOF

        # Set up cross-compilation environment for CMake
        export CMAKE_TOOLCHAIN_FILE="${WORKDIR}/toolchain.cmake"
        export OECORE_TARGET_SYSROOT="${STAGING_DIR_TARGET}"
        export CROSS_COMPILE="${TARGET_PREFIX}"
    else
        # For native builds, set crypto paths to aws-lc
        export CMAKE_FIND_PACKAGE_PREFER_CONFIG=ON
    fi
}

RDEPENDS:${PN} += "\
    python3-core \
    python3-asyncio \
"

RDEPENDS:${PN}-ptest += "\
    python3 \
    python3-websockets \
    bash \
"

# nooelint: oelint.task.nocopy
do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/test ${D}${PTEST_PATH}/tests/
}

BBCLASSEXTEND = "native nativesdk"

# nooelint: oelint.vars.insaneskip
INSANE_SKIP:${PN} += "buildpaths"
# nooelint: oelint.vars.insaneskip
INSANE_SKIP:${PN}-dbg += "buildpaths"
