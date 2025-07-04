SUMMARY = "AWS IoT Greengrass lite"
DESCRIPTION = "AWS IoT Greengrass runtime for constrained devices"
HOMEPAGE = "https://github.com/aws-greengrass/aws-greengrass-lite"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

DEPENDS += "\
    curl \
    libevent \
    libyaml \
    openssl \
    sdbus-c++-libsystemd \
    sqlite3 \
    util-linux-libuuid \
    uriparser \
    libzip \
    "

DEPENDS:append:libc-musl = " argp-standalone"
LDFLAGS:append:libc-musl = " -largp"

### enable CLANG instead of GCC
#TOOLCHAIN = "clang"

###
# Use this for development to specify a local folder as source dir (cloned repo)
# inherit externalsrc
# EXTERNALSRC = "${TOPDIR}/../../aws-greengrass-lite"
# EXTERNALSRC_BUILD = "${EXTERNALSRC}/build/${DEVICE}_${IMAGE}"
###

#THIS IS DISABLED IF exernalsrc is enabled
SRC_URI = "\
    git://github.com/aws-greengrass/aws-greengrass-lite.git;protocol=https;branch=main;name=ggl \
    git://github.com/FreeRTOS/coreMQTT.git;protocol=https;branch=main;name=mqtt;destsuffix=${S}/thirdparty/core_mqtt \
    git://github.com/FreeRTOS/backoffAlgorithm.git;protocol=https;branch=main;name=backoff;destsuffix=${S}/thirdparty/backoff_algorithm \
    git://github.com/aws/SigV4-for-AWS-IoT-embedded-sdk.git;protocol=https;branch=main;name=sigv4;destsuffix=${S}/thirdparty/aws_sigv4 \
    file://001-disable_strip.patch \
    file://greengrass-lite.yaml \
    file://run-ptest \
"

SRCREV_ggl = "25ca698f878ee481a8a2ade0d54499992f339122"

# must match fc_deps.json
SRCREV_mqtt = "f1827d8b46703f1c5ff05d21b34692d3122c9a04"
SRCREV_backoff = "f2f3bb2d8310f7cb48baa3ee64b635a5d66f838b"
SRCREV_sigv4 = "f0409ced6c2c9430f0e972019b7e8f20bbf58f4e"

EXTRA_OECMAKE:append = " \
    -DFETCHCONTENT_SOURCE_DIR_CORE_MQTT=${S}/thirdparty/core_mqtt \
    -DFETCHCONTENT_SOURCE_DIR_BACKOFF_ALGORITHM=${S}/thirdparty/backoff_algorithm \
    -DFETCHCONTENT_SOURCE_DIR_AWS_SIGV4=${S}/thirdparty/aws_sigv4 \
    "

SRCREV_FORMAT .= "_ggl_core_mqtt_backoff_aws_sigv4"

FILES:${PN}:append = " \
    ${systemd_unitdir}/system/greengrass-lite.service \
    /usr/components/* \
    ${sysconfdir}/sudoers.d/${BPN} \
    /usr/lib/* \
    ${gg_workingdir} \
    "

REQUIRED_DISTRO_FEATURES = "systemd"

PACKAGECONFIG ?= "\
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'with-tests', '', d)} \
    "

PACKAGECONFIG[with-tests] = "-DBUILD_TESTING=ON -DBUILD_EXAMPLES=ON,-DBUILD_TESTING=OFF,"

# default is stripped, we wanna do this by yocto
EXTRA_OECMAKE:append = " -DCMAKE_BUILD_TYPE=RelWithDebInfo"
# EXTRA_OECMAKE:append = " -DCMAKE_BUILD_TYPE=MinSizeRel"

# add DEBUG logs
EXTRA_OECMAKE:append = " -DGGL_LOG_LEVEL=DEBUG"
# EXTRA_OECMAKE:append = " -DGGL_LOG_LEVEL=TRACE"

# No warnings should be in commited code, not enabled yet
# CFLAGS:append = " -Werror"

SYSTEMD_SERVICE:${PN} = "\
    ggl.aws_iot_mqtt.socket \
    ggl.aws_iot_tes.socket \
    ggl.aws.greengrass.TokenExchangeService.service \
    ggl.core.gg-fleet-statusd.service \
    ggl.core.ggconfigd.service \
    ggl.core.ggdeploymentd.service \
    ggl.core.gghealthd.service \
    ggl.core.ggipcd.service \
    ggl.core.ggpubsubd.service \
    ggl.core.iotcored.service \
    ggl.core.tesd.service \
    ggl.gg_config.socket \
    ggl.gg_deployment.socket \
    ggl.gg_fleet_status.socket \
    ggl.gg_health.socket \
    ggl.gg_pubsub.socket \
    ggl.gg-ipc.socket.socket \
    ggl.ipc_component.socket \
    greengrass-lite.target \
"

inherit systemd cmake pkgconfig useradd features_check ptest

gg_workingdir ?= "${localstatedir}/lib/greengrass"

# https://github.com/aws-greengrass/aws-greengrass-lite/blob/main/docs/INSTALL.md#usergroup
# user and group for greengrass itself
gg_user = "ggcore"
gg_group = "ggcore"

# default user and group for greengrass components
ggc_user = "gg_component"
ggc_group = "gg_component"

# set user and group for greengrass-lite itself
EXTRA_OECMAKE:append = " -DGGL_SYSTEMD_SYSTEM_USER=${gg_user}"
EXTRA_OECMAKE:append = " -DGGL_SYSTEMD_SYSTEM_GROUP=${gg_group}"
EXTRA_OECMAKE:append = " -DGGL_SYSTEMD_SYSTEM_DIR=${systemd_system_unitdir}"

do_install:append() {

    install -d ${D}/${sysconfdir}/greengrass
    install -d -m 0755 ${D}/${sysconfdir}/greengrass/config.d

    install -m 0644 ${UNPACKDIR}/greengrass-lite.yaml ${D}/${sysconfdir}/greengrass/config.d
    sed -i -e 's,@GG_WORKING_DIR@,${gg_workingdir},g' \
            -e 's,@GG_USER@,${gg_user},g' \
            -e 's,@GG_GROUP@,${gg_group},g' \
            ${D}/${sysconfdir}/greengrass/config.d/greengrass-lite.yaml

    install -d ${D}/${gg_workingdir}
    chown ${gg_user}:${gg_group} ${D}/${gg_workingdir}
}

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "-r ${gg_group}; -r ${ggc_group}"
USERADD_PARAM:${PN} = "-r -M -N -g  ${gg_group} -s /bin/false ${gg_user}; -r -M -N -g  ${ggc_group} -s /bin/false ${ggc_user}"
