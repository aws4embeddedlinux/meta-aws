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
    file://0001-boost-support-any.patch \
    file://0002-remove-cxx-standard.patch \
    file://0003-boost-include-format.patch \
    file://0004-cmake-version.patch \
    file://run-ptest \
    "
SRCREV = "feb59e268c8f4f1c7450f3a510963e84cc397ac7"

UPSTREAM_CHECK_COMMITS = "1"

inherit cmake ptest pkgconfig

PACKAGECONFIG ??= "\
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'with-tests', '', d)} \
    "

PACKAGECONFIG[with-tests] = "-DBUILD_TESTS=ON,-DBUILD_TESTS=OFF,"

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

# Use -std=c++20 for fixing
# error: #warning "<ciso646> is deprecated in C++17, use <version> to detect implementation-specific macros" [-Werror=cpp]
CXXFLAGS += "-std=c++20 -Wno-error=attributes -Wno-error=deprecated"
