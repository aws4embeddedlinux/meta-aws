# nooelint: oelint.file.underscores
require aws-iot-device-sdk-cpp-v2-samples.inc

S = "${WORKDIR}/git/samples/mqtt5/mqtt5_pubsub"

DEBUG_PREFIX_MAP += "-ffile-prefix-map=${UNPACKDIR}/${BP}=${TARGET_DBGSRC_DIR}"

do_install() {
 install -d ${D}${bindir}
 install ${B}/mqtt5_pubsub ${D}${bindir}
}

EXTRA_OECMAKE:append = " -DCMAKE_BUILD_TYPE=RelWithDebInfo"
