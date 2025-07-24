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
    openssl \
    "

SRC_URI = "\
    ${@bb.utils.contains('PACKAGECONFIG', 'no-buildin-sdk', \
     'git://github.com/awslabs/aws-crt-python.git;protocol=https;branch=main',\
     'gitsm://github.com/awslabs/aws-crt-python.git;protocol=https;branch=main', \
    d)} \
    file://001-fix-cross-compilation-support.patch \
    file://run-ptest \
    "

SRCREV = "5cf5e7d70f5ecd3a88fae15fa3f427b82f30fbae"
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>.*)"

inherit setuptools3_legacy ptest

CFLAGS:append = " -Wl,-Bsymbolic"

do_configure:prepend(){
    sed -i "s/__version__ = '1.0.0.dev0'/__version__ = '${PV}'/" ${S}/awscrt/__init__.py
}

# Create CMake toolchain file for cross-compilation as setuptools is using cmake internally
# and we can not inherit cmake class as this conflicts with setuptools3_legacy
do_compile:prepend(){

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
EOF

    # aws-crt-python to use the libcrypto included on your system
    export AWS_CRT_BUILD_USE_SYSTEM_LIBCRYPTO="1"

    # Set up cross-compilation environment for CMake
    export CMAKE_TOOLCHAIN_FILE="${WORKDIR}/toolchain.cmake"
    export OECORE_TARGET_SYSROOT="${STAGING_DIR_TARGET}"
    export CROSS_COMPILE="${TARGET_PREFIX}"
}

RDEPENDS:${PN} += "\
    python3 \
    python3-asyncio \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/* ${D}${PTEST_PATH}/tests/
}

BBCLASSEXTEND = "native nativesdk"

# nooelint: oelint.vars.insaneskip
INSANE_SKIP:${PN} += "buildpaths"
# nooelint: oelint.vars.insaneskip
INSANE_SKIP:${PN}-dbg += "buildpaths"
# nooelint: oelint.vars.insaneskip
INSANE_SKIP:${PN}-ptest += "arch file-rdeps"
