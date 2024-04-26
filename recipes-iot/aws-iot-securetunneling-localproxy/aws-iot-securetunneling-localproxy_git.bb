SUMMARY = "AWS Iot Secure Tunneling local proxy reference C++ implementation"
DESCRIPTION = "When devices are deployed behind restricted firewalls at remote sites, you need a way to gain access to those device for troubleshooting, configuration updates, and other operational tasks. Secure tunneling helps customers establish bidirectional communication to remote devices over a secure connection that is managed by AWS IoT. Secure tunneling does not require updates to your existing inbound firewall rule, so you can keep the same security level provided by firewall rules at a remote site. "
HOMEPAGE = "https://docs.aws.amazon.com/iot/latest/developerguide/secure-tunneling.html"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS += "\
    boost \
    catch2 \
    openssl \
    protobuf \
    protobuf-native \
    zlib \
    "

BRANCH ?= "main"

# nooelint: oelint.file.patchsignedoff
SRC_URI = "\
  git://git@github.com/aws-samples/aws-iot-securetunneling-localproxy.git;branch=${BRANCH};protocol=https \
  file://boost-support-any.patch \
  file://gcc13.patch \
  file://gcc13_2.patch \
  file://remove-cxx-standard.patch \
  file://boost-include-format.patch \
  file://run-ptest \
  "
SRCREV = "9eace7470fbbee00473074f6dc763afdc9e11a4c"

UPSTREAM_CHECK_COMMITS = "1"

S = "${WORKDIR}/git"

inherit cmake ptest

do_configure:prepend() {
    sed -i "s/Protobuf_LITE_STATIC_LIBRARY/Protobuf_LITE_LIBRARY/g" ${S}/CMakeLists.txt
    sed -i "s/string.*Protobuf_LITE_LIBRARY.*/#&/g" ${S}/CMakeLists.txt
    sed -i "s/set_property.*PROTOBUF_USE_STATIC_LIBS.*/#&/g" ${S}/CMakeLists.txt
}

do_install () {
  install -d ${D}${bindir}
  install -m 0755 ${B}/bin/localproxy ${D}${bindir}/localproxy
}

FILES:${PN} += "${bindir}/localproxy"
FILES:${PN}-ptest += "${bindir}/localproxytest"

do_install_ptest() {
  install -d ${D}${bindir}
  install -m 0755 ${B}/bin/localproxytest ${D}${bindir}/localproxytest
}

# fix DSO missing from command line
LDFLAGS += "-Wl,--copy-dt-needed-entries"
