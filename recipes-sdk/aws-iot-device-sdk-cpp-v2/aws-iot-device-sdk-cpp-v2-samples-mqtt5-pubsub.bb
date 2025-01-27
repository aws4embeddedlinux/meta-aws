# nooelint: oelint.file.underscores
require aws-iot-device-sdk-cpp-v2-samples.inc

S = "${WORKDIR}/git/samples/mqtt5/mqtt5_pubsub"

do_install() {
 install -d ${D}${bindir}
 install ${B}/mqtt5_pubsub ${D}${bindir}
}

# nooelint: oelint.vars.insaneskip:INSANE_SKIP
INSANE_SKIP:${PN}-dbg += "buildpaths"

EXTRA_OECMAKE:append = " -DCMAKE_BUILD_TYPE=RelWithDebInfo"

PACKAGECONFIG:append:x86-64 = " ${@bb.utils.contains('PTEST_ENABLED', '1', 'sanitize', '', d)}"
# -fsanitize=address does cause this
# nooelint: oelint.vars.insaneskip:INSANE_SKIP
# INSANE_SKIP += "${@bb.utils.contains('PACKAGECONFIG', 'sanitize', 'buildpaths', '', d)}"

PACKAGECONFIG[sanitize] = ",, gcc-sanitizers"
OECMAKE_CXX_FLAGS += "${@bb.utils.contains('PACKAGECONFIG', 'sanitize', '-fsanitize=address,undefined -fno-omit-frame-pointer', '', d)}"
