SUMMARY = "AWS IoT Device Client"
DESCRIPTION = "The AWS IoT Device Client is free, open-source, modular software written in C++ that you can compile and install on your Embedded Linux based IoT devices to access AWS IoT Core, AWS IoT Device Management, and AWS IoT Device Defender features by default."
HOMEPAGE = "https://github.com/awslabs/aws-iot-device-client"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3eb31626add6ada64ff9ac772bd3c653"

DEPENDS = "\
    aws-iot-device-sdk-cpp-v2 \
    openssl \
    "
# disabled googletest because of: https://github.com/awslabs/aws-iot-device-client/issues/404
# use a older and build by this package version, will be downloaded in the do_configure step
do_configure[network] = "1"

PROVIDES = "aws/aws-iot-device-client"

BRANCH ?= "main"

# nooelint: oelint.file.patchsignedoff:Patch
SRC_URI = "\
    git://github.com/awslabs/aws-iot-device-client.git;protocol=https;branch=${BRANCH} \
    file://run-ptest \
    file://ptest_result.py \
    "

SRCREV = "cf761074367fa50b80b1e8a5c1efb4599ce2a85e"

S = "${WORKDIR}/git"

inherit cmake systemd ptest
do_compile_ptest()  {
    cmake_runcmake_build --target test-aws-iot-device-client
}

do_install() {
  install -d ${D}${base_sbindir}
  install -d ${D}${sysconfdir}
  install -d -m 0700 ${D}${sysconfdir}/aws-iot-device-client
  install -d ${D}${systemd_unitdir}/system

  install -m 0755 ${WORKDIR}/build/aws-iot-device-client \
                  ${D}${base_sbindir}/aws-iot-device-client
  install -m 0644 ${S}/setup/aws-iot-device-client.service \
                  ${D}${systemd_system_unitdir}/aws-iot-device-client.service
}

EXTRA_OECMAKE += "\
    -DBUILD_SDK=OFF \
    -DBUILD_TEST_DEPS=ON \
    -DCMAKE_BUILD_TYPE=RelWithDebInfo \
    -DCMAKE_VERBOSE_MAKEFILE=ON \
    -DCMAKE_CXX_FLAGS_RELEASE=-s \
"

CXXFLAGS += "-Wno-ignored-attributes"

PACKAGECONFIG ??= " dsn dsc ds st fp dd pubsub samples jobs"

# enable PACKAGECONFIG = "static" to build static instead of shared
PACKAGECONFIG[static] = "-DBUILD_SHARED_LIBS=OFF,-DBUILD_SHARED_LIBS=ON,,"

PACKAGECONFIG[samples] = "-DEXCLUDE_SAMPLES=OFF,-DEXCLUDE_SAMPLES=ON,,"
PACKAGECONFIG[pubsub] = "-DEXCLUDE_PUBSUB=OFF,-DEXCLUDE_PUBSUB=ON,,"
PACKAGECONFIG[jobs] = "-DEXCLUDE_JOBS=OFF,-DEXCLUDE_JOBS=ON,,"
PACKAGECONFIG[dd] = "-DEXCLUDE_DD=OFF,-DEXCLUDE_DD=ON,,"
PACKAGECONFIG[st] = "-DEXCLUDE_ST=OFF,-DEXCLUDE_DD=ON,,"
PACKAGECONFIG[fp] = "-DEXCLUDE_FP=OFF,-DEXCLUDE_FP=ON,,"
PACKAGECONFIG[ds] = "-DEXCLUDE_SHADOW=OFF,-DEXCLUDE_SHADOW=ON,,"
PACKAGECONFIG[dsc] = "-DEXCLUDE_CONFIG_SHADOW=OFF,-DEXCLUDE_CONFIG_SHADOW=ON,,"
PACKAGECONFIG[dsn] = "-DEXCLUDE_SAMPLE_SHADOW=OFF,-DEXCLUDE_SAMPLE_SHADOW=ON,,"

FILES:${PN} += "${base_sbindir}/sbin/aws-iot-device-client"
FILES:${PN} += "${systemd_system_unitdir}/aws-iot-device-client.service"
FILES:${PN} += "${sysconfdir}/aws-iot-device-client.json"

RDEPENDS:${PN} = "\
    aws-iot-device-sdk-cpp-v2 \
    openssl \
    "

SYSTEMD_AUTO_ENABLE = "enable"
SYSTEMD_SERVICE:${PN} = "aws-iot-device-client.service"

RDEPENDS:${PN}-ptest += "\
    bash \
    python3 \
    "

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests

    cp -r ${B}/test/test-aws-iot-device-client ${D}${PTEST_PATH}/

    install -m 0755 ${WORKDIR}/ptest_result.py ${D}${PTEST_PATH}/

    install -d ${D}/${libdir}
    install -m 755 ${B}/lib/libgtest.so.1.11.0 ${D}/${libdir}
    install -m 755 ${B}/lib/libgmock.so.1.11.0 ${D}/${libdir}
    install -m 755 ${B}/lib/libgmock_main.so.1.11.0 ${D}/${libdir}
}

# nooelint: oelint.vars.insaneskip:INSANE_SKIP
INSANE_SKIP:${PN}-ptest += "buildpaths"
