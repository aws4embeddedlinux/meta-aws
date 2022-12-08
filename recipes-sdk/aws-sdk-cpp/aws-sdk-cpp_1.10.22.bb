SUMMARY = "AWS C++ SDK"
DESCRIPTION = "AWS C++ SDK and ptest"
HOMEPAGE = "https://github.com/aws/aws-sdk-cpp"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

DEPENDS += "\
    aws-c-auth \
    aws-crt-cpp \
    curl \
"

AWS_SDK_PACKAGES = ""

SRC_URI = "\
    git://github.com/aws/aws-sdk-cpp.git;protocol=https;branch=main \
    file://0002-build-fix-building-without-external-dependencies.patch \
    file://run-ptest"

SRCREV = "6eb7b90bc0d59d072473969d513aa707d1ab02d8"

S = "${WORKDIR}/git"

inherit cmake ptest pkgconfig

PACKAGECONFIG ??= "\
    ${@bb.utils.filter('DISTRO_FEATURES', 'pulseaudio', d)} \
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'with-tests', '', d)}"

PACKAGECONFIG[pulseaudio] = "-DPULSEAUDIO=TRUE, -DPULSEAUDIO=FALSE, pulseaudio"

PACKAGECONFIG[with-tests] = "-DENABLE_TESTING=ON -DAUTORUN_UNIT_TESTS=ON,-DENABLE_TESTING=OFF -DAUTORUN_UNIT_TESTS=OFF, googletest"

python populate_packages:prepend () {
    packages = []
    def hook(f, pkg, file_regex, output_pattern, modulename):
        packages.append(pkg)

    # Put the libraries into separate packages
    do_split_packages(d, d.expand('${libdir}'), r'^lib(.*)\.so$', '%s', 'library for %s', extra_depends='', prepend=True, hook=hook)

    d.setVar("AWS_SDK_PACKAGES", " ".join(packages))
}

# enable PACKAGECONFIG = "static" to build static instead of shared libs
PACKAGECONFIG[static] = "-DBUILD_SHARED_LIBS=OFF,-DBUILD_SHARED_LIBS=ON,,"

# Notify that libraries are not versioned
FILES_SOLIBSDEV = ""

# -Werror will cause deprecation warnings to fail the build e.g. OpenSSL cause one, so disable these warnings
OECMAKE_CXX_FLAGS += "-Wno-deprecated-declarations"

# -Wno-maybe-uninitialized is related to this: https://github.com/aws/aws-sdk-cpp/issues/2234
OECMAKE_CXX_FLAGS += "${@bb.utils.contains('PTEST_ENABLED', '1', '-Wno-maybe-uninitialized', '', d)}"

EXTRA_OECMAKE += "\
     -DBUILD_DEPS=OFF \
"

RDEPENDS:${PN}-ptest += "\
    bash \
    python3 \
"
# "aws-sdk-cpp" is a metapackage which pulls in all aws-sdk-cpp libraries
ALLOW_EMPTY:${PN} = "1"
RRECOMMENDS:${PN} += "${AWS_SDK_PACKAGES}"
RRECOMMENDS:${PN}:class-native = ""

do_install_ptest () {
    install -d ${D}${PTEST_PATH}/tests/build
    cd ${B}/ && find . -name "*integration-tests*" -executable -type f -exec install -D "{}" ${D}${PTEST_PATH}/tests/build/ \;
    install -m 0755 ${S}/scripts/run_integration_tests.py ${D}${PTEST_PATH}/tests/
    chmod 755 ${D}${PTEST_PATH}/tests/build/*
}

# to save compile time you can specify libs you only want to build
# (we can't have spaces in -DBUILD_ONLY, hence the strange formatting)
# EXTRA_OECMAKE += "-DBUILD_ONLY='\
# access-management;\
# cloudfront;\
# cognito-identity;\
# cognito-idp;\
# iam;\
# iot;\
# kinesis;\
# lambda;\
# polly;\
# s3;\
# sts;'"
