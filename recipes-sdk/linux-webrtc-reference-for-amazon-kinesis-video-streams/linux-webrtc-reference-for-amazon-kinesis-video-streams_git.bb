SUMMARY = "linux-webrtc-reference-for-amazon-kinesis-video-streams"
DESCRIPTION = "Pure C WebRTC Client for Amazon Kinesis Video Streams - with minimal dependencies - This repository is currently in development and not recommended for production use."
HOMEPAGE = "https://github.com/awslabs/linux-webrtc-reference-for-amazon-kinesis-video-streams"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

DEPENDS += "\
  libwebsockets \
  gstreamer1.0 \
  gstreamer1.0-plugins-base \
  gstreamer1.0-plugins-good \
  gstreamer1.0-plugins-bad \
  "

# default is stripped, we wanna do this by yocto
EXTRA_OECMAKE:append = " -DCMAKE_BUILD_TYPE=RelWithDebInfo"

# set log message debug level
EXTRA_OECMAKE:append = " -DLIBRARY_LOG_LEVEL=LOG_VERBOSE"

###
# Use this for development to specify a local folder as source dir (cloned repo)
# inherit externalsrc
# EXTERNALSRC = "${THISDIR}/../../.."

# this will force recipe to always rebuild
# SSTATE_SKIP_CREATION = '1'
###
# SRC_URI = "\
#    file://run-ptest \
# "

CFLAGS:append:arm = " -Wno-error=format="

SRC_URI = "\
     gitsm://github.com/awslabs/linux-webrtc-reference-for-amazon-kinesis-video-streams.git;protocol=https;branch=main \
     file://run-ptest \
"

SRCREV = "74a81e023d07bc2b6b3064121c134818e946d215"

S = "${WORKDIR}/git"

PACKAGECONFIG:append:x86-64 = " ${@bb.utils.contains('PTEST_ENABLED', '1', 'sanitize', '', d)}"

PACKAGECONFIG[sanitize] = ",, gcc-sanitizers"

RDEPENDS:${PN} += "ca-certificates"

#RDEPENDS:${PN}-ptest += "\
#    amazon-kvs-webrtc-sdk \
#    coreutils \
#    util-linux \
#"

do_configure:append() {
  cp ${S}/examples/demo_config/demo_config_template.h ${S}/examples/demo_config/demo_config.h
  sed -i '/^#if defined( AWS_ACCESS_KEY_ID ) && defined( AWS_IOT_THING_ROLE_ALIAS )/i\
  #define AWS_CREDENTIALS_ENDPOINT ""\
  #define AWS_IOT_THING_NAME ""\
  #define AWS_IOT_THING_ROLE_ALIAS ""\
  #define AWS_IOT_THING_CERT_PATH "certificate.pem"\
  #define AWS_IOT_THING_PRIVATE_KEY_PATH "private.key"\
  ' ${S}/examples/demo_config/demo_config.h
}

inherit cmake pkgconfig ptest

do_install() {
  install -d ${D}${bindir}
  install -m 0755 ${B}/WebRTCLinuxApplicationMaster ${D}${bindir}
  install -m 0755 ${B}/WebRTCLinuxApplicationGstMaster ${D}${bindir}

  install -d ${D}${bindir}/examples/app_media_source/samples/h264SampleFrames
  cp -r ${S}/examples/app_media_source/samples/h264SampleFrames/* ${D}${bindir}/examples/app_media_source/samples/h264SampleFrames/

  install -d ${D}${bindir}/examples/app_media_source/samples/opusSampleFrames
  cp -r ${S}/examples/app_media_source/samples/opusSampleFrames/* ${D}${bindir}/examples/app_media_source/samples/opusSampleFrames/

  install -d ${D}${sysconfdir}
  install -m 0664 ${S}/cert/cert.pem ${D}${sysconfdir}/
#  install -m 0664 ${S}/certificate.pem ${D}${sysconfdir}/
#  install -m 0664 ${S}/private.key ${D}${sysconfdir}/
}

# do_install_ptest:append() {
#   install -d ${D}${sysconfdir}
#   install ${S}/tests/iot-credentials/THING_NAME ${D}${sysconfdir}/
#   install ${S}/tests/iot-credentials/CHANNEL_NAME ${D}${sysconfdir}/
#   install ${S}/tests/iot-credentials/ROLE_ALIAS ${D}${sysconfdir}/
#   install ${S}/tests/iot-credentials/CREDENTIALS_ENDPOINT ${D}${sysconfdir}/
#   install ${S}/tests/iot-credentials/AWS_REGION ${D}${sysconfdir}/
# }

OECMAKE_CXX_FLAGS += "${@bb.utils.contains('PACKAGECONFIG', 'sanitize', '-fsanitize=address,undefined -fno-omit-frame-pointer', '', d)}"
