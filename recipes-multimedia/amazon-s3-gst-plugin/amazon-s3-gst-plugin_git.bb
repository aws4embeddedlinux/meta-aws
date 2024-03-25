SUMMARY = "amazon s3 gst plugin"
DESCRIPTION = "A collection of Amazon S3 GStreamer elements."
HOMEPAGE = "https://github.com/amzn/amazon-s3-gst-plugin"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1803fa9c2c3ce8cb06b4861d75310742"

DEPENDS = "\
    aws-crt-cpp \
    aws-sdk-cpp \
    gstreamer1.0 \
"

# nooelint: oelint.file.patchsignedoff:Patch
SRC_URI = "git://github.com/amzn/amazon-s3-gst-plugin.git;protocol=https;branch=master \
           file://vaLog.patch \
           file://run-ptest \
           "
SRCREV = "9515e85871047eeaed6a1612b88b72955373b310"

# this project do not use version tags, use latest commit
UPSTREAM_CHECK_COMMITS = "1"

S = "${WORKDIR}/git"

CXXFLAGS:append = " -Wno-missing-field-initializers"

inherit meson pkgconfig ptest

FILES:${PN} += "\
    ${libdir}/gstreamer-1.0/* \
    ${libdir}/libgstawscredentials-1.0.so \
"
FILES_SOLIBSDEV = ""
