SUMMARY = "AWS Iot Secure Tunneling local proxy reference C++ implementation"
DESCRIPTION = "When devices are deployed behind restricted firewalls at remote sites, you need a way to gain access to those device for troubleshooting, configuration updates, and other operational tasks. Secure tunneling helps customers establish bidirectional communication to remote devices over a secure connection that is managed by AWS IoT. Secure tunneling does not require updates to your existing inbound firewall rule, so you can keep the same security level provided by firewall rules at a remote site. "
HOMEPAGE = "https://docs.aws.amazon.com/iot/latest/developerguide/secure-tunneling.html"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS += "\
    boost \
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
    file://0004-cmake-version.patch \
    file://0005-fix-boost-system-header-only.patch \
    file://run-ptest \
    "
SRCREV = "e59b955aef2bf68ca9691933999c658e43fb8adc"

S = "${WORKDIR}/git"

UPSTREAM_CHECK_COMMITS = "1"

inherit cmake ptest pkgconfig

# NOTE: Catch2 v3 tests disabled - meta-oe only provides Catch2 v2.13.10
# CMakeLists.txt requires Catch2 v3 which is not available in scarthgap
PACKAGECONFIG ??= ""

PACKAGECONFIG[with-tests] = "-DBUILD_TESTS=ON,-DBUILD_TESTS=OFF,"

EXTRA_OECMAKE += "-DLINK_STATIC_OPENSSL=OFF"

do_configure:prepend() {
    sed -i "s/Protobuf_LITE_STATIC_LIBRARY/Protobuf_LITE_LIBRARY/g" ${S}/CMakeLists.txt
    sed -i "/string(REPLACE.*CMAKE_SHARED_LIBRARY_SUFFIX.*CMAKE_STATIC_LIBRARY_SUFFIX/{ N; /Protobuf_LITE_LIBRARY/d; }" ${S}/CMakeLists.txt
    sed -i "s/set_property.*PROTOBUF_USE_STATIC_LIBS.*/#&/g" ${S}/CMakeLists.txt
    sed -i "s/find_package(Protobuf/set(Protobuf_USE_STATIC_LIBS OFF)\nfind_package(Protobuf/g" ${S}/CMakeLists.txt
}

do_install () {
  install -d ${D}${bindir}
  install -m 0755 ${B}/bin/localproxy ${D}${bindir}/localproxy
}

FILES:${PN} += "${bindir}/localproxy"

do_install_ptest() {
  :
}

# fix DSO missing from command line
LDFLAGS += "-Wl,--copy-dt-needed-entries"

# Use -std=c++20 for fixing
# error: #warning "<ciso646> is deprecated in C++17, use <version> to detect implementation-specific macros" [-Werror=cpp]
CXXFLAGS += "-std=c++20 -Wno-error=attributes -Wno-error=deprecated"
