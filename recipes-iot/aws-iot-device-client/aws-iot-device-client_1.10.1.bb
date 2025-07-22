SUMMARY = "AWS IoT Device Client"
DESCRIPTION = "The AWS IoT Device Client is free, open-source, modular software written in C++ that you can compile and install on your Embedded Linux based IoT devices to access AWS IoT Core, AWS IoT Device Management, and AWS IoT Device Defender features by default."
HOMEPAGE = "https://github.com/awslabs/aws-iot-device-client"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3eb31626add6ada64ff9ac772bd3c653"

DEPENDS = "\
    ${@bb.utils.contains('PACKAGECONFIG', 'no-buildin-sdk', 'aws-iot-device-sdk-cpp-v2', '', d)} \
    openssl \
    "

# disabled googletest because of: https://github.com/awslabs/aws-iot-device-client/issues/404
# use a older and build by this package version, will be downloaded in the do_configure step
# nooelint: oelint.task.network
do_configure[network] = "1"

PROVIDES = "aws/aws-iot-device-client"

BRANCH ?= "main"

# nooelint: oelint.file.patchsignedoff:Patch
SRC_URI = "\
    git://github.com/awslabs/aws-iot-device-client.git;protocol=https;branch=${BRANCH} \
    ${@bb.utils.contains('PACKAGECONFIG', 'no-buildin-sdk', '', 'gitsm://github.com/aws/aws-iot-device-sdk-cpp-v2.git;protocol=https;branch=main;name=aws-iot-device-sdk-cpp-v2;destsuffix=aws-iot-device-sdk-cpp-v2-src', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'no-buildin-sdk', '', 'git://github.com/google/googletest.git;protocol=https;branch=main;name=googletest;destsuffix=googletest-src', d)} \
    file://run-ptest \
    file://001-disable-tests.patch \
    file://002-set-cmake-min-version-for-external-project-sdk-and-src-path.patch \
    "

SRCREV = "7f9547bca3e1a199f2824f4376e1782b082b226f"

# must match CMakeLists.txt.awssdk (check is done through failing patch)
# nooelint: oelint.vars.specific
SRCREV_aws-iot-device-sdk-cpp-v2 = "74c8b683ebe5b1cbf484f6acaa281f56aaa63948"

# must match CMakeLists.txt.googletest (check is done through failing patch)
# nooelint: oelint.vars.specific
SRCREV_googletest = "15460959cbbfa20e66ef0b5ab497367e47fc0a04"

SRCREV_FORMAT .= "_aws-iot-device-sdk-cpp-v2_googletest"

S = "${UNPACKDIR}/git"

inherit cmake systemd ptest

do_install() {
  install -d ${D}${base_sbindir}
  install -d ${D}${sysconfdir}
  install -d -m 0700 ${D}${sysconfdir}/aws-iot-device-client
  install -d ${D}${systemd_unitdir}/system

  install -m 0755 ${B}/aws-iot-device-client \
                  ${D}${base_sbindir}/aws-iot-device-client
  install -m 0644 ${S}/setup/aws-iot-device-client.service \
                  ${D}${systemd_system_unitdir}/aws-iot-device-client.service
}

EXTRA_OECMAKE += "\
    -DBUILD_TEST_DEPS=ON \
    -DCMAKE_BUILD_TYPE=RelWithDebInfo \
    -DCMAKE_VERBOSE_MAKEFILE=ON \
    -DCMAKE_CXX_FLAGS_RELEASE=-s \
"

CXXFLAGS += "-Wno-ignored-attributes"

PACKAGECONFIG ??= "dsn dsc ds st fp dd pubsub samples jobs"

# enable PACKAGECONFIG = "no-static" to build shared instead of static, this is the default as -DBUILD_SDK=ON is default,
# otherwise installing shared libs from this seems wrong. As other programs use different versions of the sdk maybe.
PACKAGECONFIG[no-static] = "-DBUILD_SHARED_LIBS=ON,-DBUILD_SHARED_LIBS=OFF,,"

# no-buildin-sdk - seems that secure tunneling is broken with newer versions of aws-iot-device-sdk-cpp-v2
# (https://github.com/aws4embeddedlinux/meta-aws/issues/13012)
# thus not using separate sdk should be the default
PACKAGECONFIG[no-buildin-sdk] = "-DBUILD_SDK=OFF,-DBUILD_SDK=ON,,"

PACKAGECONFIG[samples] = "-DEXCLUDE_SAMPLES=OFF,-DEXCLUDE_SAMPLES=ON,,"
PACKAGECONFIG[pubsub] = "-DEXCLUDE_PUBSUB=OFF,-DEXCLUDE_PUBSUB=ON,,"
PACKAGECONFIG[jobs] = "-DEXCLUDE_JOBS=OFF,-DEXCLUDE_JOBS=ON,,"
PACKAGECONFIG[dd] = "-DEXCLUDE_DD=OFF,-DEXCLUDE_DD=ON,,"
PACKAGECONFIG[st] = "-DEXCLUDE_ST=OFF,-DEXCLUDE_ST=ON,,"
PACKAGECONFIG[fp] = "-DEXCLUDE_FP=OFF,-DEXCLUDE_FP=ON,,"
PACKAGECONFIG[ds] = "-DEXCLUDE_SHADOW=OFF,-DEXCLUDE_SHADOW=ON,,"
PACKAGECONFIG[dsc] = "-DEXCLUDE_CONFIG_SHADOW=OFF,-DEXCLUDE_CONFIG_SHADOW=ON,,"
PACKAGECONFIG[dsn] = "-DEXCLUDE_SAMPLE_SHADOW=OFF,-DEXCLUDE_SAMPLE_SHADOW=ON,,"

FILES:${PN} += "${base_sbindir}/sbin/aws-iot-device-client"
FILES:${PN} += "${systemd_system_unitdir}/aws-iot-device-client.service"
FILES:${PN} += "${sysconfdir}/aws-iot-device-client.json"

SYSTEMD_AUTO_ENABLE = "enable"
SYSTEMD_SERVICE:${PN} = "aws-iot-device-client.service"

# nooelint: oelint.vars.insaneskip:INSANE_SKIP
INSANE_SKIP:${PN} += "buildpaths"

# nooelint: oelint.vars.insaneskip:INSANE_SKIP
INSANE_SKIP:${PN}-dbg += "buildpaths"
