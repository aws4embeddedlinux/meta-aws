SUMMARY = "AWS IoT Device SDK Embedded C"
DESCRIPTION = "Sample pbulish/subscribe application"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=acc7a1bf87c055789657b148939e4b40"

TAG_sdk = "v3.0.1"
TAG_mbed = "mbedtls-2.16.6"

SRCREV_aws-iot-device-sdk-embedded-c = "${TAG_sdk}"
SRCREV_mbedtls = "${TAG_mbed}"

SRC_URI = "git://github.com/aws/aws-iot-device-sdk-embedded-C.git;protocol=https;tag=${TAG_sdk} \
		file://make.patch \
	git://github.com/ARMmbed/mbedtls.git;protocol=https;destsuffix=git/external_libs/mbedTLS;tag=${TAG_mbed} \
"

S = "${WORKDIR}/git/"
S_sample = "${S}samples/linux/subscribe_publish_sample"

export LD_FLAG = "${LDFLAGS}"

do_compile_prepend() {
  cd ${S_sample}
}

do_install() {
  cd ${S_sample}
  install -d ${D}${bindir}
  install -m 0755 subscribe_publish_sample ${D}${bindir}
}

FILES_${PN} += "${bindir}/subscribe_publish_sample"

PACKAGES = "${PN}"

# just ignore produced debug files
INSANE_SKIP_${PN} += "installed-vs-shipped"
