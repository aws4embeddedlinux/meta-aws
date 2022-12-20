# -*- mode: Conf; -*-
SUMMARY = "A collection of Amazon S3 GStreamer elements."
HOMEPAGE = "https://github.com/amzn/amazon-s3-gst-plugin"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1803fa9c2c3ce8cb06b4861d75310742"

SRC_URI = "git://github.com/amzn/amazon-s3-gst-plugin.git;protocol=https;branch=master"
SRCREV = "06c235948a673b240be32fe5f8241feb675d6cc8"

# this project do not use version tags, use latest commit
UPSTREAM_CHECK_COMMITS = "1"

S = "${WORKDIR}/git"

inherit meson pkgconfig

DEPENDS = " \
    gstreamer1.0 \
    aws-sdk-cpp \
"

FILES:${PN} += " \
    ${libdir}/gstreamer-1.0/* \
    ${libdir}/libgstawscredentials-1.0.so \
"
FILES_SOLIBSDEV = ""
