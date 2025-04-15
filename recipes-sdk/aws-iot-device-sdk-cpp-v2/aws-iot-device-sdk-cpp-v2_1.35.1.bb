SUMMARY = "AWS IoT Device SDK for C++ v2"
DESCRIPTION = "The AWS IoT Device SDK for C++ v2 provides MQTT APIs for C++ applications"
HOMEPAGE = "https://github.com/aws/aws-iot-device-sdk-cpp-v2"
LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://documents/LICENSE;md5=f91e61641e7a96835dea6926a65f4702"

DEPENDS += "${@bb.utils.contains('PACKAGECONFIG', 'build-deps', 'openssl', 'aws-c-iot', d)}"

PROVIDES += "aws/aws-iot-device-sdk-cpp-v2"

require aws-iot-device-sdk-cpp-v2-version.inc

SRC_URI:append = " \
    file://run-ptest \
    file://openssl_suppressions.txt \
    ${@bb.utils.contains('PACKAGECONFIG', 'static', '', 'file://001-shared-static-crt-libs.patch', d)} \
    "

S = "${WORKDIR}/git"

inherit cmake pkgconfig ptest

UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+\.\d+(\.\d+)*)"

CXXFLAGS:append = " -fPIC"
LDFLAGS:append = " -Wl,-Bsymbolic"

EXTRA_OECMAKE += "\
    -DCMAKE_MODULE_PATH=${STAGING_LIBDIR}/cmake \
    -DBUILD_TESTING=OFF \
    -DCMAKE_BUILD_TYPE=Release \
    -DUSE_OPENSSL=ON  \
"

# Notify that libraries are not versioned
SOLIBS = "*.so"
FILES_SOLIBSDEV = ""

# enable PACKAGECONFIG = "static" to build static instead of shared libs
PACKAGECONFIG[static] = "-DBUILD_SHARED_LIBS=OFF,-DBUILD_SHARED_LIBS=ON"

# build-deps is enabled by default to use the aws-c-iot lib (and its dependencies) version that comes as a git submodule,
# this also means that it conflicts with the aws-c-iot as it installs the same library if installed separate.
PACKAGECONFIG[build-deps] = "-DBUILD_DEPS=ON,-DBUILD_DEPS=OFF"

PACKAGECONFIG ??= "\
    build-deps \
    "

FILES:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'build-deps', '/usr/lib/*', '', d)}"

# nooelint: oelint.vars.insaneskip:INSANE_SKIP
INSANE_SKIP += "${@bb.utils.contains('PACKAGECONFIG', 'build-deps', 'ldflags', '', d)}"

RDEPENDS:${PN}-ptest:prepend = "\
    aws-iot-device-sdk-cpp-v2-samples-mqtt5-pubsub \
    "

BBCLASSEXTEND = "native nativesdk"

EXTRA_OECMAKE:append = " -DCMAKE_BUILD_TYPE=RelWithDebInfo"
