SUMMARY = "Amazon Kinesis Video Streams DCEP (Data Channel Establishment Protocol)"
DESCRIPTION = "A library for establishing data channels in Amazon Kinesis Video Streams WebRTC"
HOMEPAGE = "https://github.com/awslabs/amazon-kinesis-video-streams-dcep"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

SRCREV = "89bc7c2433e8d981c42bd24a119c74eb5b5ce5a5"
SRC_URI = "git://github.com/moninom1/amazon-kinesis-video-streams-dcep.git;protocol=https;branch=docker \
           file://run-ptest \
           file://endianness_test.c \
          "

inherit cmake ptest

DEPENDS = "openssl"

EXTRA_OECMAKE = ""

# Ensure main package exists even if empty
ALLOW_EMPTY:${PN} = "1"

FILES:${PN}-dev = "${includedir}/*"
FILES:${PN}-staticdev = "${libdir}/*.a"

# Skip QA check for buildpaths in ptest package
INSANE_SKIP:${PN}-ptest += "buildpaths"
INSANE_SKIP:${PN}-dbg += "buildpaths"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}
    install -m 0755 ${UNPACKDIR}/run-ptest ${D}${PTEST_PATH}/
    
    # Compile and install the endianness test
    ${CC} ${CFLAGS} ${LDFLAGS} -o ${D}${PTEST_PATH}/endianness_test ${UNPACKDIR}/endianness_test.c
}

RDEPENDS:${PN}-ptest += "bash"
