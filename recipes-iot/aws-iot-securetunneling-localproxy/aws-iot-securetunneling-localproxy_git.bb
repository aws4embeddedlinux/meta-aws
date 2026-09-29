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
    file://run-ptest \
    "
SRCREV = "5e599cabfc4195dd39a8acab5ad843fbbd4faf70"

UPSTREAM_CHECK_COMMITS = "1"

inherit cmake ptest pkgconfig

PACKAGECONFIG ??= "\
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'with-tests', '', d)} \
    "

PACKAGECONFIG[with-tests] = "-DBUILD_TESTS=ON,-DBUILD_TESTS=OFF,"

EXTRA_OECMAKE += "-DLINK_STATIC_OPENSSL=OFF"
EXTRA_OECMAKE += "-DBOOST_ROOT=${STAGING_DIR_HOST}${prefix}"
EXTRA_OECMAKE += "-DBoost_NO_SYSTEM_PATHS=ON"

# Upstream defaults to system mode when cross-compiling, which is what we want.
# Override CXX standard: upstream sets C++14 but we need C++20 for GCC 16 compat.
# Use shared protobuf: upstream forces static in system mode via lp_static_lib_path.
do_configure:prepend() {
    # Remove CMAKE_CXX_STANDARD 14 so our CXXFLAGS -std=c++20 takes effect
    sed -i '/^set(CMAKE_CXX_STANDARD 14)/d' ${S}/CMakeLists.txt
    sed -i '/^set(CMAKE_CXX_STANDARD_REQUIRED ON)/d' ${S}/CMakeLists.txt

    # Use shared protobuf-lite instead of static: replace the static-lib
    # rewrite with a direct assignment of the shared library path
    sed -i 's|lp_static_lib_path(Protobuf_LITE_STATIC_LIBRARY "${Protobuf_LITE_LIBRARY}")|set(Protobuf_LITE_STATIC_LIBRARY "${Protobuf_LITE_LIBRARY}")|' ${S}/cmake/LocalproxyProtobuf.cmake

    # OE's Boost 1.92 (built with b2) installs BoostConfig.cmake but not
    # per-component configs. Bypass it with Boost_NO_BOOST_CMAKE. Also
    # disable static libs (OE has shared only), and hint the library dir
    # so FindBoost can locate the shared libraries in the sysroot.
    # Remove 'system' from COMPONENTS — Boost.System is header-only since
    # Boost 1.69 and OE doesn't build a libboost_system.so stub.
    sed -i '/find_package(/i set(Boost_NO_BOOST_CMAKE ON)' ${S}/cmake/LocalproxyBoost.cmake
    sed -i 's/set(Boost_USE_STATIC_LIBS ON)/set(Boost_USE_STATIC_LIBS OFF)/' ${S}/cmake/LocalproxyBoost.cmake
    sed -i '/^ *system$/d' ${S}/cmake/LocalproxyBoost.cmake
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
