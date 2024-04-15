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
           file://run-ptest \
           "
SRCREV = "b0887cf881b915a07e78b5e9ff87835b51c8e26c"

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
