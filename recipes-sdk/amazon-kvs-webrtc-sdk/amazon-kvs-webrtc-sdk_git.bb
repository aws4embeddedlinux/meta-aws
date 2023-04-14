SUMMARY = "Amazon Kinesis Video Streams WebRTC"
DESCRIPTION = "Pure C WebRTC Client for Amazon Kinesis Video Streams"
HOMEPAGE = "https://github.com/awslabs/amazon-kinesis-video-streams-webrtc-sdk-c"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

DEPENDS += "\
    amazon-kvs-producer-pic \
    amazon-kvs-producer-sdk-c \
    curl \
    gstreamer1.0 \
    gstreamer1.0-plugins-base \
    libsrtp \
    libwebsockets \
    openssl \
    usrsctp \
"

PROVIDES += "aws/amazon-kvs-webrtc-sdk"

BRANCH = "master"
SRC_URI = "\
    git://github.com/awslabs/amazon-kinesis-video-streams-webrtc-sdk-c.git;protocol=https;branch=${BRANCH} \
"

SRCREV = "b820e2ea3d28c670da2fbc789face742cb949ef1"

UPSTREAM_CHECK_COMMITS = "1"

S = "${WORKDIR}/git"

inherit cmake pkgconfig

FILES:${PN} += "${libdir}"

CFLAGS:append = " -Wl,-Bsymbolic"

EXTRA_OECMAKE += "\
    -DBUILD_DEPENDENCIES=OFF \
    -DCODE_COVERAGE=OFF \
    -DBUILD_SAMPLE=OFF \
    -DADDRESS_SANITIZER=OFF \
    -DMEMORY_SANITIZER=OFF \
    -DTHREAD_SANITIZER=OFF \
    -DUNDEFINED_BEHAVIOR_SANITIZER=OFF \
    -DDEBUG_HEAP=OFF \
    -DCOMPILER_WARNINGS=OFF \
    -DALIGNED_MEMORY_MODEL=OFF \
    -DCMAKE_BUILD_TYPE=Release \
    -DCMAKE_INSTALL_PREFIX=$D/usr \
"

FILES_SOLIBSDEV = ""

BBCLASSEXTEND = "native nativesdk"
