# -*- mode: Conf; -*-
SUMMARY = "AWS C++ SDK"
HOMEPAGE = "https://github.com/aws/aws-sdk-cpp"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

SRC_URI = " \
    gitsm://github.com/aws/aws-sdk-cpp.git;protocol=https;branch=main"
 

SRCREV = "e5e38a781307ce2d7b894df152728aa51d2c1394"

S = "${WORKDIR}/git"

inherit cmake

DEPENDS = " \
    curl \
    openssl \
"

PACKAGES =+ " \
    ${PN}-iot \
    ${PN}-s3-crt \
"

PROVIDES += "${PACKAGES}"

FILES_${PN} += "${libdir}/libaws-cpp-sdk-core.so"
FILES_${PN}-iot = "${libdir}/libaws-cpp-sdk-iot.so"
FILES_${PN}-s3-crt = "${libdir}/libaws-cpp-sdk-s3-crt.so"

FILES_SOLIBSDEV = ""

# We can't have spaces in -DBUILD_ONLY, hence the strange formatting
EXTRA_OECMAKE += "-DBUILD_ONLY='\
iot;\
s3-crt;\
'"
EXTRA_OECMAKE += "-DBUILD_DEPS=ON"
EXTRA_OECMAKE += "-DENABLE_TESTING=OFF"
EXTRA_OECMAKE += "-DAUTORUN_UNIT_TESTS=OFF"
EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=OFF"
EXTRA_OECMAKE += "-DAWS_CUSTOM_MEMORY_MANAGEMENT=ON"

# -Werror will cause deprecation warnings to fail the build e.g. OpenSSL cause one, so disable these warnings
OECMAKE_CXX_FLAGS += "-Wno-deprecated-declarations"

ALLOW_EMPTY_${PN} = "1"
ALLOW_EMPTY_${PN}-dbg = "1"

FILES_${PN}-staticdev += "${libdir}"

# Patch the resulting aws-cpp-sdk-core-targets.cmake to remove absolute paths to libcurl.so and libz.so
# This can be an issue with older versions of CMake
do_install_append() {
   sed -i -E 's#;[^;]+libcurl\.so;#;libcurl.so;#' ${D}/usr/lib/cmake/aws-cpp-sdk-core/aws-cpp-sdk-core-targets.cmake
   sed -i -E 's#;[^;]+libz\.so;#;libz.so;#' ${D}/usr/lib/cmake/aws-cpp-sdk-core/aws-cpp-sdk-core-targets.cmake
}
