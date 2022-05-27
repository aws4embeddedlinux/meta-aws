# -*- mode: Conf; -*-
SUMMARY = "AWS IoT Device Client"
DESCRIPTION = "The AWS IoT Device Client is free, open-source, modular software written in C++ that you can compile and install on your Embedded Linux based IoT devices to access AWS IoT Core, AWS IoT Device Management, and AWS IoT Device Defender features by default."
HOMEPAGE = "https://github.com/awslabs/aws-iot-device-client"
LICENSE = "Apache-2.0"
PROVIDES = "aws/aws-iot-device-client"
PACKAGES:${PN} += "aws-crt-cpp"
PREFERRED_RPROVIDER:${PN} += "aws/crt-cpp"

LIC_FILES_CHKSUM = "file://LICENSE;md5=3eb31626add6ada64ff9ac772bd3c653"

BRANCH ?= "main"

SRC_URI = "git://github.com/awslabs/aws-iot-device-client.git;protocol=https;branch=${BRANCH} \
           file://01-missing-thread-includes.patch \
           file://02-missing-thread-includes.patch \
           file://03-serviced-config.patch \
"

SRCREV = "a2583a491d95cdc2e8ba3d6a1007d07d98a8c93e"

S= "${WORKDIR}/git"
DEPENDS = "openssl aws-iot-device-sdk-cpp-v2 googletest"
RDEPENDS:${PN} = "openssl aws-iot-device-sdk-cpp-v2"

inherit cmake

do_install() {
  install -d ${D}${base_sbindir}
  install -d ${D}${sysconfdir}
  install -d -m 0700 ${D}${sysconfdir}/aws-iot-device-client
  install -d ${D}${systemd_unitdir}/system

  install -m 0755 ${WORKDIR}/build/aws-iot-device-client \
                  ${D}${base_sbindir}/aws-iot-device-client
  install -m 0644 ${S}/setup/aws-iot-device-client.service \
                  ${D}${systemd_system_unitdir}/aws-iot-device-client.service
}

OECMAKE_BUILDPATH += "${WORKDIR}/build"
OECMAKE_SOURCEPATH += "${S}"
EXTRA_OECMAKE += "-DBUILD_SDK=OFF"
EXTRA_OECMAKE += "-DBUILD_TEST_DEPS=OFF"
EXTRA_OECMAKE += "-DBUILD_TESTING=OFF"
EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=RelWithDebInfo"
EXTRA_OECMAKE += "-DCMAKE_VERBOSE_MAKEFILE=ON"
EXTRA_OECMAKE += "-DCMAKE_CXX_FLAGS_RELEASE=-s"

PACKAGECONFIG[samples] = "-DEXCLUDE_SAMPLES=OFF,-DEXCLUDE_SAMPLES=ON,,"
PACKAGECONFIG[pubsub]  = "-DEXCLUDE_PUBSUB=OFF,-DEXCLUDE_PUBSUB=ON,,"
PACKAGECONFIG[jobs]    = "-DEXCLUDE_JOBS=OFF,-DEXCLUDE_JOBS=ON,,"
PACKAGECONFIG[dd]      = "-DEXCLUDE_DD=OFF,-DEXCLUDE_DD=ON,,"
PACKAGECONFIG[st]      = "-DEXCLUDE_ST=OFF,-DEXCLUDE_DD=ON,,"
PACKAGECONFIG[fp]      = "-DEXCLUDE_FP=OFF,-DEXCLUDE_FP=ON,,"
PACKAGECONFIG[ds]      = "-DEXCLUDE_SHADOW=OFF,-DEXCLUDE_SHADOW=ON,,"
PACKAGECONFIG[dsc]     = "-DEXCLUDE_CONFIG_SHADOW=OFF,-DEXCLUDE_CONFIG_SHADOW=ON,,"
PACKAGECONFIG[dsn]     = "-DEXCLUDE_SAMPLE_SHADOW=OFF,-DEXCLUDE_SAMPLE_SHADOW=ON,,"

FILES:${PN} += "${base_sbindir}/sbin/aws-iot-device-client"
FILES:${PN} += "${systemd_system_unitdir}/aws-iot-device-client.service"
FILES:${PN} += "${sysconfdir}/aws-iot-device-client.json"

inherit systemd
SYSTEMD_AUTO_ENABLE = "enable"
SYSTEMD_SERVICE:${PN} = "aws-iot-device-client.service"


