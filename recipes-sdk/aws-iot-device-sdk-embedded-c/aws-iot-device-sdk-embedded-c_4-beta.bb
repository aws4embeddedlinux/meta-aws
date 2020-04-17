SUMMARY = "AWS IoT Device SDK Embedded C"
DESCRIPTION = "Shared libs and sample applications"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b980284408dfdf6b4e464f4cb4233e06"

inherit cmake

OECMAKE_FIND_ROOT_PATH_MODE_PROGRAM = "BOTH"

BRANCH = "v4_beta"
SRCREV = "${BRANCH}"

SRC_URI = "git://github.com/aws/aws-iot-device-sdk-embedded-C.git;protocol=https;branch=${BRANCH} \
		   file://demo-link.patch"

S = "${WORKDIR}/git/"
OUT = "${WORKDIR}/build/output"

do_compile_append() {
  cd ${OUT}/bin
  chrpath -dk *
  cd ${OUT}/lib
  chrpath -dk * 
}

do_install() {
  install -d ${D}${bindir}
  install -m 0755 ${OUT}/bin/* ${D}${bindir}

  install -d ${D}${libdir}
  install -m 0755 ${OUT}/lib/* ${D}${libdir}
}

FILES_${PN} += "${bindir}/aws_iot_demo_jobs"
FILES_${PN} += "${bindir}/aws_iot_demo_shadow"
FILES_${PN} += "${bindir}/iot_demo_mqtt"
FILES_${PN} += "${bindir}/aws_iot_demo_defender"
FILES_${PN} += "${libdir}/libawsiotdefender.so"
FILES_${PN} += "${libdir}/libawsiotshadow.so"
FILES_${PN} += "${libdir}/libiotmqtt.so"
FILES_${PN} += "${libdir}/libawsiotjobs.so"
FILES_${PN} += "${libdir}/libiotserializer.so"
FILES_${PN} += "${libdir}/libtinycbor.so"
FILES_${PN} += "${libdir}/libiotbase.so"
FILES_${PN} += "${libdir}/libmbedtls.so"
FILES_${PN} += "${libdir}/libawsiotcommon.so"

PACKAGES = "${PN}"

# just ignore produced debug files by cmake build system
INSANE_SKIP_${PN} += "installed-vs-shipped"
