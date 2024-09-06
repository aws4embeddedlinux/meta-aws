SUMMARY = "AWS C++ SDK"
DESCRIPTION = "AWS C++ SDK and ptest"
HOMEPAGE = "https://github.com/aws/aws-sdk-cpp"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

DEPENDS += "\
    aws-crt-cpp \
    curl \
"

AWS_SDK_PACKAGES = ""

PACKAGES_DYNAMIC = "^${PN}-.*"

SRC_URI = "\
    git://github.com/aws/aws-sdk-cpp.git;protocol=https;branch=main \
    file://run-ptest \
    file://ptest_result.py \
    "

SRCREV = "a72c30acc317b24d282640f0a40ef137acbeedec"

S = "${WORKDIR}/git"

inherit cmake ptest pkgconfig

PACKAGECONFIG ??= "\
    ${@bb.utils.filter('DISTRO_FEATURES', 'pulseaudio', d)} \
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'with-tests', '', d)}"

PACKAGECONFIG[pulseaudio] = "-DPULSEAUDIO=TRUE, -DPULSEAUDIO=FALSE, pulseaudio"

# CMAKE_CROSSCOMPILING=OFF will enable build of unit tests
PACKAGECONFIG[with-tests] = "-DENABLE_TESTING=ON -DAUTORUN_UNIT_TESTS=OFF -DCMAKE_CROSSCOMPILING=OFF,-DENABLE_TESTING=OFF -DAUTORUN_UNIT_TESTS=OFF, googletest"

python populate_packages:prepend () {
    packages = []
    def hook(f, pkg, file_regex, output_pattern, modulename):
        packages.append(pkg)

    # Put the libraries into separate packages
    do_split_packages(d, d.expand('${libdir}'), r'^lib(.*)\.so$', '%s', 'library for %s', extra_depends='', prepend=True, hook=hook)

    d.setVar("AWS_SDK_PACKAGES", " ".join(packages))
}

# enable PACKAGECONFIG = "static" to build static instead of shared libs
PACKAGECONFIG[static] = "-DBUILD_SHARED_LIBS=OFF,-DBUILD_SHARED_LIBS=ON"

# Notify that libraries are not versioned
FILES_SOLIBSDEV = ""

# -Werror will cause deprecation warnings to fail the build e.g. OpenSSL cause one, so disable these warnings
OECMAKE_CXX_FLAGS += "-Wno-deprecated-declarations"

# note: variable tracking size limit exceeded with '-fvar-tracking-assignments', retrying without
OECMAKE_CXX_FLAGS += "-fno-var-tracking"

# -Wno-maybe-uninitialized is related to this: https://github.com/aws/aws-sdk-cpp/issues/2234
OECMAKE_CXX_FLAGS += "${@bb.utils.contains('PTEST_ENABLED', '1', '-Wno-maybe-uninitialized', '', d)}"

OECMAKE_CXX_FLAGS += "-Wno-psabi"

EXTRA_OECMAKE += "\
     -DBUILD_DEPS=OFF \
     -DCMAKE_MODULE_PATH=${STAGING_LIBDIR}/cmake \
"

RDEPENDS:${PN}-ptest += "\
    bash \
    python3 \
"
# "aws-sdk-cpp" is a meta package which pulls in all aws-sdk-cpp libraries
ALLOW_EMPTY:${PN} = "1"
RRECOMMENDS:${PN} += "${AWS_SDK_PACKAGES}"
RRECOMMENDS:${PN}:class-native = ""

do_install_ptest () {
    install -d ${D}${PTEST_PATH}/tests
    find ${B}/generated/tests -executable -type f -exec install -m 0755 "{}" ${D}${PTEST_PATH}/tests/ \;
    install -m 0755 ${WORKDIR}/ptest_result.py ${D}${PTEST_PATH}/
}

# this is related to this issue
# https://github.com/aws/aws-sdk-cpp/issues/2242
# nooelint: oelint.vars.insaneskip:INSANE_SKIP
INSANE_SKIP:${PN}-src:append:class-target:arm = " buildpaths"
