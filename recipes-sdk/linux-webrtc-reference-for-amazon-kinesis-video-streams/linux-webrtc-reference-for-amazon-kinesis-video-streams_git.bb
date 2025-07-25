SUMMARY = "linux-webrtc-reference-for-amazon-kinesis-video-streams"
DESCRIPTION = "Pure C WebRTC Client for Amazon Kinesis Video Streams - with minimal dependencies - This repository is currently in development and not recommended for production use."
HOMEPAGE = "https://github.com/awslabs/linux-webrtc-reference-for-amazon-kinesis-video-streams"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

DEPENDS += "\
  libwebsockets \
  "

# default is stripped, we wanna do this by yocto
EXTRA_OECMAKE:append = " -DCMAKE_BUILD_TYPE=RelWithDebInfo"

# set log level
OECMAKE_C_FLAGS:append = " -DLIBRARY_LOG_LEVEL=LOG_INFO"

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

# nooelint: oelint.vars.specific
CFLAGS:append:arm = " -Wno-error=format="

SRC_URI += "\
     gitsm://github.com/awslabs/linux-webrtc-reference-for-amazon-kinesis-video-streams.git;protocol=https;branch=main \
     file://run-ptest \
"

SRCREV = "bf916b16ce0fef99e3023a1162cc8ce6bb722ea4"

UPSTREAM_CHECK_COMMITS = "1"

S = "${WORKDIR}/git"

PACKAGECONFIG:append:x86-64 = " ${@bb.utils.contains('PTEST_ENABLED', '1', 'sanitize', '', d)}"

PACKAGECONFIG[sanitize] = ",, gcc-sanitizers"

# h264 gst plugin require setting: LICENSE_FLAGS_ACCEPTED += "commercial"
# and enable opus codec
# PACKAGECONFIG:append:pn-gstreamer1.0-plugins-base = " opus"
# and enable x264 codec
# PACKAGECONFIG:append:pn-gstreamer1.0-plugins-ugly = " x264"

PACKAGECONFIG:append = " ${@bb.utils.contains("LICENSE_FLAGS_ACCEPTED", "commercial", "gstreamer", "", d)}"

PACKAGECONFIG[gstreamer] = ",,\
  gstreamer1.0 \
  gstreamer1.0-plugins-base \
  gstreamer1.0-plugins-good \
  gstreamer1.0-plugins-bad \
  gstreamer1.0-plugins-ugly \
  ,\
  gstreamer1.0 \
  gstreamer1.0-plugins-base \
  gstreamer1.0-plugins-good \
  gstreamer1.0-plugins-bad \
  gstreamer1.0-plugins-ugly \
  gstreamer1.0-plugins-base-apps \
  "

RDEPENDS:${PN} += "ca-certificates"

# TODO: fix ptest
# amazon-kvs-webrtc-sdk

RDEPENDS:${PN}-ptest += "\
    coreutils \
    util-linux \
"

# overwrite these variables to use your own AWS credentials in your local.conf
AWS_REGION ?= "eu-central-1"
AWS_KVS_CHANNEL_NAME ?= "test-channel"
AWS_ACCESS_KEY_ID ?= ""
AWS_SECRET_ACCESS_KEY ?= ""
AWS_SESSION_TOKEN ?= ""
AWS_CA_CERT_PATH ?= "/etc/cert.pem"

# this can be used for key based authentication
do_configure:append() {
  cp ${S}/examples/demo_config/demo_config_template.h ${S}/examples/demo_config/demo_config.h
  sed -i '/#define AWS_REGION "us-west-2"/d' ${S}/examples/demo_config/demo_config.h
  sed -i '/#define AWS_KVS_CHANNEL_NAME ""/d' ${S}/examples/demo_config/demo_config.h
  sed -i '/#define AWS_CA_CERT_PATH "cert\/cert.pem"/d' ${S}/examples/demo_config/demo_config.h
  sed -i '/^#if defined( AWS_ACCESS_KEY_ID ) && defined( AWS_IOT_THING_ROLE_ALIAS )/i\
#define AWS_REGION "${AWS_REGION}"\
#define AWS_KVS_CHANNEL_NAME "${AWS_KVS_CHANNEL_NAME}"\
#define AWS_ACCESS_KEY_ID "${AWS_ACCESS_KEY_ID}"\
#define AWS_SECRET_ACCESS_KEY "${AWS_SECRET_ACCESS_KEY}"\
#define AWS_SESSION_TOKEN "${AWS_SESSION_TOKEN}"\
#define AWS_CA_CERT_PATH "${AWS_CA_CERT_PATH}"\
  ' ${S}/examples/demo_config/demo_config.h
}

# this can be used for iot cert based authentication
#do_configure:append() {
#  cp ${S}/examples/demo_config/demo_config_template.h ${S}/examples/demo_config/demo_config.h
#  sed -i '/^#if defined( AWS_ACCESS_KEY_ID ) && defined( AWS_IOT_THING_ROLE_ALIAS )/i\
#  #define AWS_CREDENTIALS_ENDPOINT ""\
#  #define AWS_IOT_THING_NAME ""\
#  #define AWS_IOT_THING_ROLE_ALIAS ""\
#  #define AWS_IOT_THING_CERT_PATH "certificate.pem"\
#  #define AWS_IOT_THING_PRIVATE_KEY_PATH "private.key"\
#  ' ${S}/examples/demo_config/demo_config.h
#}
#

inherit cmake pkgconfig ptest

do_install() {
  install -d ${D}${bindir}
  install -m 0755 ${B}/WebRTCLinuxApplicationMaster ${D}${bindir}
  ${@bb.utils.contains("PACKAGECONFIG", "gstreamer", "install -m 0755 ${B}/WebRTCLinuxApplicationGstMaster ${D}${bindir}", "", d)}

  install -d ${D}${bindir}/examples/app_media_source/samples/h264SampleFrames
  cp -r ${S}/examples/app_media_source/samples/h264SampleFrames/* ${D}${bindir}/examples/app_media_source/samples/h264SampleFrames/

  install -d ${D}${bindir}/examples/app_media_source/samples/opusSampleFrames
  cp -r ${S}/examples/app_media_source/samples/opusSampleFrames/* ${D}${bindir}/examples/app_media_source/samples/opusSampleFrames/

  install -d ${D}${sysconfdir}
  install -m 0664 ${S}/cert/cert.pem ${D}${sysconfdir}/
}

do_install_ptest:append() {
  install -d ${D}${sysconfdir}
  echo  "${AWS_ACCESS_KEY_ID}" > ${D}${sysconfdir}/AWS_ACCESS_KEY_ID
  echo  "${AWS_SECRET_ACCESS_KEY}" > ${D}${sysconfdir}/AWS_SECRET_ACCESS_KEY
  echo  "${AWS_KVS_CHANNEL_NAME}" > ${D}${sysconfdir}/CHANNEL_NAME
  echo  "${AWS_REGION}" > ${D}${sysconfdir}/AWS_REGION

  # cert based authentication
  if [ -f ${S}/cert/private.key ]; then
    install -m 0664 ${S}/cert/private.key ${D}${sysconfdir}/
  fi
  if [ -f ${S}/cert/certificate.pem ]; then
    install -m 0664 ${S}/cert/certificate.pem ${D}${sysconfdir}/
  fi
}

#TODO: add suport for cert based authentication
# do_install_ptest:append() {
#   install -d ${D}${sysconfdir}
#   install ${S}/tests/iot-credentials/THING_NAME ${D}${sysconfdir}/
#   install ${S}/tests/iot-credentials/CHANNEL_NAME ${D}${sysconfdir}/
#   install ${S}/tests/iot-credentials/ROLE_ALIAS ${D}${sysconfdir}/
#   install ${S}/tests/iot-credentials/CREDENTIALS_ENDPOINT ${D}${sysconfdir}/
#   install ${S}/tests/iot-credentials/AWS_REGION ${D}${sysconfdir}/
# }

OECMAKE_CXX_FLAGS += "${@bb.utils.contains('PACKAGECONFIG', 'sanitize', '-fsanitize=address,undefined -fno-omit-frame-pointer', '', d)}"

# nooelint: oelint.vars.insaneskip:INSANE_SKIP
INSANE_SKIP:${PN} += "buildpaths"
INSANE_SKIP:${PN}-dbg += "buildpaths"
