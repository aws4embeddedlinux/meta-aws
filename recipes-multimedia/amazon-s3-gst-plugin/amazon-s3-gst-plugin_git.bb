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
SRCREV = "89c2ed54eb057a557bb26d4ef3332734794e23c2"

# this project do not use version tags, use latest commit
UPSTREAM_CHECK_COMMITS = "1"

S = "${WORKDIR}/git"

CXXFLAGS:append = " -Wno-missing-field-initializers"

EXTRA_OECMAKE:append = " -DCMAKE_BUILD_TYPE=RelWithDebInfo"

inherit meson pkgconfig ptest

FILES:${PN} += "\
    ${libdir}/gstreamer-1.0/* \
    ${libdir}/libgstawscredentials-1.0.so \
"
FILES_SOLIBSDEV = ""

PACKAGECONFIG:append:x86-64 = " ${@bb.utils.contains('PTEST_ENABLED', '1', 'sanitize', '', d)}"
# -fsanitize=address does cause this
# nooelint: oelint.vars.insaneskip:INSANE_SKIP
# INSANE_SKIP += "${@bb.utils.contains('PACKAGECONFIG', 'sanitize', 'buildpaths', '', d)}"

PACKAGECONFIG[sanitize] = ",, gcc-sanitizers"
OECMAKE_CXX_FLAGS += "${@bb.utils.contains('PACKAGECONFIG', 'sanitize', '-fsanitize=address,undefined -fno-omit-frame-pointer', '', d)}"