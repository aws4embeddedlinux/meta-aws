# nooelint: oelint.file.underscores
require aws-iot-device-sdk-cpp-v2-samples.inc

S = "${UNPACKDIR}/${PN}-${PV}/samples/mqtt5/mqtt5_pubsub"

do_install() {
 install -d ${D}${bindir}
 install ${B}/mqtt5_pubsub ${D}${bindir}
}

# nooelint: oelint.vars.insaneskip:INSANE_SKIP
INSANE_SKIP:${PN}-dbg += "buildpaths"

EXTRA_OECMAKE:append = " -DCMAKE_BUILD_TYPE=RelWithDebInfo"
