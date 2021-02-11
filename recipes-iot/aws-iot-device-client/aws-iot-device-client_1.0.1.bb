SUMMARY = "AWS IoT Device Client"
DESCRIPTION = "The AWS IoT Device Client is free, open-source, modular software written in C++ that you can compile and install on your Embedded Linux based IoT devices to access AWS IoT Core, AWS IoT Device Management, and AWS IoT Device Defender features by default."
HOMEPAGE = "https://github.com/awslabs/aws-iot-device-client"
LICENSE = "Apache-2.0"
PROVIDES += "aws/aws-iot-device-client"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3eb31626add6ada64ff9ac772bd3c653"

BRANCH ?= "main"

SRC_URI = "file://001-install-target.patch \
           git://github.com/awslabs/aws-iot-device-client.git;branch=${BRANCH}"
SRCREV = "95a5dccb9acfde5b94f246be083f19f6f0565eb6"

S= "${WORKDIR}/git"
PACKAGES = "${PN}"
INSANE_SKIP_${PN} += "installed-vs-shipped"
DEPENDS = "openssl aws-iot-device-sdk-cpp-v2 googletest"
RDEPENDS_${PN} = "openssl aws-iot-device-sdk-cpp-v2"

inherit cmake

do_install() {
  cmake_do_install
  install -D -p -m0644 ${S}/setup/aws-iot-device-client.service \
    ${D}${systemd_unitdir}/system/aws-iot-device-client.service
}

OECMAKE_BUILDPATH += "${WORKDIR}/build"
OECMAKE_SOURCEPATH += "${S}"
EXTRA_OECMAKE += "-DBUILD_SDK=OFF"
EXTRA_OECMAKE += "-DBUILD_TEST_DEPS=OFF"
EXTRA_OECMAKE += "-DBUILD_TESTING=OFF"
EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=Release"
EXTRA_OECMAKE += "-DEXCLUDE_JOBS=OFF"
EXTRA_OECMAKE += "-DEXCLUDE_DD=ON"
EXTRA_OECMAKE += "-DEXCLUDE_ST=ON"
EXTRA_OECMAKE += "-DEXCLUDE_FP=ON"

FILES_${PN} += "${bindir}/aws-iot-device-client"
FILES_${PN} += "${systemd_unitdir}/system/aws-iot-device-client.service"

inherit systemd
SYSTEMD_AUTO_ENABLE = "enable"
SYSTEMD_SERVICE_${PN} = "aws-iot-device-client.service"


