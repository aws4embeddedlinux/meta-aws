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

# THIS IS DISABLED IF exernalsrc is enabled
SRC_URI = "\
    git://github.com/aws-greengrass/aws-greengrass-lite.git;protocol=https;branch=main;name=ggl \
    git://github.com/FreeRTOS/coreMQTT.git;protocol=https;branch=main;name=mqtt;destsuffix=${S}/thirdparty/core_mqtt \
    git://github.com/FreeRTOS/backoffAlgorithm.git;protocol=https;branch=main;name=backoff;destsuffix=${S}/thirdparty/backoff_algorithm \
    git://github.com/aws/SigV4-for-AWS-IoT-embedded-sdk.git;protocol=https;branch=main;name=sigv4;destsuffix=${S}/thirdparty/aws_sigv4 \
    git://github.com/aws-greengrass/aws-greengrass-sdk-lite.git;protocol=https;branch=main;name=sdk;destsuffix=${S}/thirdparty/ggl_sdk \
    file://001-disable_strip.patch \
    ${@bb.utils.contains('PACKAGECONFIG','localdeployment','file://002-fix-deployment-copy-path.patch','',d)} \
    ${@bb.utils.contains('PACKAGECONFIG','localdeployment','file://003-ggl-cli-multi-component.patch','',d)} \
    ${@bb.utils.contains('PACKAGECONFIG','fleetprovisioning','file://004-fix-fleet-provisioning-circular-dependency.patch','',d)} \
    file://greengrass-lite.yaml \
    file://run-ptest \
    ${@bb.utils.contains('PACKAGECONFIG','localdeployment','file://ggl.local-deployment.service','',d)} \
    ${@bb.utils.contains('PACKAGECONFIG','localdeployment','file://ggl-deploy-image-components','',d)} \
    ${@bb.utils.contains('PACKAGECONFIG','fleetprovisioning','file://ggl.gg_pre-fleetprovisioning.service','',d)} \
    ${@bb.utils.contains('PACKAGECONFIG','fleetprovisioning','file://ggl.gg_fleetprovisioning.service','',d)} \
"

# Both patches enabled: fix-deployment-copy-path.patch and fix-deployment-queue-processing.patch
# Improved deployment script: ggl-deploy-image-components with atomic deployment and component verification
# Comprehensive solution addressing both root cause and edge cases

SRCREV_ggl = "ed2b01efd60fc7e44b0f175985a67f4ce72ab323"

# must match fc_deps.json
SRCREV_mqtt = "f1827d8b46703f1c5ff05d21b34692d3122c9a04"
SRCREV_backoff = "f2f3bb2d8310f7cb48baa3ee64b635a5d66f838b"
SRCREV_sigv4 = "f0409ced6c2c9430f0e972019b7e8f20bbf58f4e"
SRCREV_sdk = "0d239f96101608441dd6434f98a9e7f6623556c7"

EXTRA_OECMAKE:append = " \
    -DFETCHCONTENT_SOURCE_DIR_CORE_MQTT=${S}/thirdparty/core_mqtt \
    -DFETCHCONTENT_SOURCE_DIR_BACKOFF_ALGORITHM=${S}/thirdparty/backoff_algorithm \
    -DFETCHCONTENT_SOURCE_DIR_AWS_SIGV4=${S}/thirdparty/aws_sigv4 \
    -DFETCHCONTENT_SOURCE_DIR_GGL_SDK=${S}/thirdparty/ggl_sdk \
    "

SRCREV_FORMAT .= "_ggl_core_mqtt_backoff_aws_sigv4_ggl_sdk"

do_configure:prepend() {
    # verify that all dependencies have correct version
    grep -q ${SRCREV_mqtt} ${S}/fc_deps.json || bbfatal "ERROR: dependency version mismatch, please update 'SRCREV_mqtt'!"
    grep -q ${SRCREV_backoff} ${S}/fc_deps.json || bbfatal "ERROR: dependency version mismatch, please update 'SRCREV_backoff'!"
    grep -q ${SRCREV_sigv4} ${S}/fc_deps.json || bbfatal "ERROR: dependency version mismatch, please update 'SRCREV_sigv4'!"
    grep -q ${SRCREV_sdk} ${S}/fc_deps.json || bbfatal "ERROR: dependency version mismatch, please update 'SRCREV_sdk'!"
}

S = "${WORKDIR}/git"

# Fleet provisioning configuration - overwrite in your local config, e.g. IOT_DATA_ENDPOINT:pn-greengrass-lite = "xxx"
IOT_DATA_ENDPOINT ?= ""
IOT_CRED_ENDPOINT ?= ""
FLEET_PROVISIONING_TEMPLATE ?= ""
CLAIM_CERT_PATH  ?= ""
CLAIM_KEY_PATH ?= ""
ROOT_CA_PATH ?= ""
IOT_ROLE_ALIAS ?= ""
AWS_REGION ?= ""

FILES:${PN}:append = " \
    ${systemd_unitdir}/system/greengrass-lite.service \
    ${@bb.utils.contains('PACKAGECONFIG','localdeployment','${systemd_unitdir}/system/ggl.local-deployment.service','',d)} \
    ${@bb.utils.contains('PACKAGECONFIG','localdeployment','${bindir}/ggl-deploy-image-components','',d)} \
    ${@bb.utils.contains('PACKAGECONFIG','fleetprovisioning','${systemd_unitdir}/system/ggl.gg_fleetprovisioning.service','',d)} \
    ${@bb.utils.contains('PACKAGECONFIG','fleetprovisioning','${systemd_unitdir}/system/ggl.gg_pre-fleetprovisioning.service','',d)} \
    /usr/components/* \
    /usr/share/greengrass-image-components/* \
    ${sysconfdir}/sudoers.d/${BPN} \
    /usr/lib/* \
    ${gg_workingdir} \
    ${sysconfdir}/greengrass/certs/* \
    "

# Runtime dependencies
RDEPENDS:${PN} += "bash"

REQUIRED_DISTRO_FEATURES = "systemd"

# enable fleetprovisioning for testing by default to get test coverage
PACKAGECONFIG ?= "\
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'with-tests', '', d)} \
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'fleetprovisioning', '', d)} \
    "

# this is to make the PACKAGECONFIG QA check happy
PACKAGECONFIG[fleetprovisioning] = ""
PACKAGECONFIG[localdeployment] = ""

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
    ${@bb.utils.contains('PACKAGECONFIG','localdeployment','ggl.local-deployment.service','',d)} \
    ${@bb.utils.contains('PACKAGECONFIG','fleetprovisioning','ggl.gg_fleetprovisioning.service ','',d)} \
    ${@bb.utils.contains('PACKAGECONFIG','fleetprovisioning','ggl.gg_pre-fleetprovisioning.service ','',d)} \
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

    install -m 0644 ${WORKDIR}/greengrass-lite.yaml ${D}/${sysconfdir}/greengrass/config.d
    sed -i -e 's,@GG_WORKING_DIR@,${gg_workingdir},g' \
            -e 's,@GG_USER@,${gg_user},g' \
            -e 's,@GG_GROUP@,${gg_group},g' \
            ${D}/${sysconfdir}/greengrass/config.d/greengrass-lite.yaml

    install -d ${D}/${gg_workingdir}
    chown ${gg_user}:${gg_group} ${D}/${gg_workingdir}

    # Local deployment service and script are installed conditionally via PACKAGECONFIG
    if ${@bb.utils.contains('PACKAGECONFIG','localdeployment','true','false',d)}; then
        install -m 0644 ${WORKDIR}/ggl.local-deployment.service ${D}${systemd_unitdir}/system/
        install -m 0755 ${WORKDIR}/ggl-deploy-image-components ${D}${bindir}/
    fi

    if ${@bb.utils.contains('PACKAGECONFIG','fleetprovisioning','true','false',d)}; then
        # Create ggcredentials directory for fleet provisioning
        install -m 0644 ${WORKDIR}/ggl.gg_pre-fleetprovisioning.service ${D}${systemd_unitdir}/system/
        install -m 0644 ${WORKDIR}/ggl.gg_fleetprovisioning.service ${D}${systemd_unitdir}/system/

        # Replace variables in the config file using a temporary file to ensure proper expansion
        cat > ${D}/${sysconfdir}/greengrass/config.d/fleetprovisioning.yaml << EOF
---
system:
  thingName: ""
  privateKeyPath: ""
  certificateFilePath: ""
services:
  aws.greengrass.NucleusLite:
    componentType: "NUCLEUS"
    configuration:
      awsRegion: "${AWS_REGION}"
      iotCredEndpoint: ""
      iotDataEndpoint: ""
      iotRoleAlias: "${IOT_ROLE_ALIAS}"
      runWithDefault:
        posixUser: "${gg_user}:${gg_group}"
      greengrassDataPlanePort: "8443"
  aws.greengrass.fleet_provisioning:
    configuration:
      iotDataEndpoint: "${IOT_DATA_ENDPOINT}"
      iotCredEndpoint: "${IOT_CRED_ENDPOINT}"
      claimCertPath: "/etc/greengrass/certs/claim.cert.pem"
      claimKeyPath: "/etc/greengrass/certs/claim.key.pem"
      rootCaPath: "/etc/greengrass/certs/AmazonRootCA1.pem"
      templateName: "${FLEET_PROVISIONING_TEMPLATE}"
      templateParams: '{"SerialNumber": "<unique>"}'
EOF
        # Create certificates directory
        install -d ${D}/${sysconfdir}/greengrass/certs

        # Install certificates only if CLAIM_CERT_PATH is set
        if [ "${CLAIM_CERT_PATH}" != "" ]; then
                # Install claim certificates from specified path
                install -m 0644 ${CLAIM_CERT_PATH} ${D}/${sysconfdir}/greengrass/certs/claim.cert.pem
                install -m 0600 ${CLAIM_KEY_PATH} ${D}/${sysconfdir}/greengrass/certs/claim.key.pem
                install -m 0644 ${ROOT_CA_PATH} ${D}/${sysconfdir}/greengrass/certs/AmazonRootCA1.pem

                # Ensure correct ownership
                chown -R ${gg_user}:${gg_group} ${D}/${sysconfdir}/greengrass/certs
        else
            bbwarn "CLAIM_CERT_PATH is not set. Fleet provisioning certificates will not be installed."
            bbwarn "You will need to provide the certificates manually at /etc/greengrass/certs/"
        fi
    fi

}

# watch for changed fleetprovisioning files to rebuild if they are changed
SSTATE_SCAN_FILES:append = " ${@' ${CLAIM_CERT_PATH} ${CLAIM_KEY_PATH} ${ROOT_CA_PATH}' if (bb.utils.contains('PACKAGECONFIG', 'fleetprovisioning', True, False, d)) else ''}"

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "-r ${gg_group}; -r ${ggc_group}"
USERADD_PARAM:${PN} = "-r -M -N -g  ${gg_group} -s /bin/false ${gg_user}; -r -M -N -g  ${ggc_group} -s /bin/false ${ggc_user}"
