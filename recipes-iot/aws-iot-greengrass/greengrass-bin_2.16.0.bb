SUMMARY = "AWS IoT Greengrass Nucleus - Binary Distribution"
DESCRIPTION = "The Greengrass nucleus component provides functionality for device side orchestration of deployments and lifecycle management for execution of Greengrass components and applications."
HOMEPAGE = "https://github.com/aws-greengrass/aws-greengrass-nucleus"
LICENSE = "Apache-2.0"

CONFLICTS = "greengrass"

require greengrass-nucleus.inc

LIC_FILES_CHKSUM = "file://${UNPACKDIR}/greengrass-bin/LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

SRC_URI = "\
    https://d2s8p88vqu9w66.cloudfront.net/releases/greengrass-${PV}.zip;subdir=greengrass-bin \
    file://001-service-time-wait.patch \
    file://002-fix-service-exec-and-docker.patch \
    file://greengrassv2-init.yaml \
    file://run-ptest \
    file://config.yaml.template \
    "

SRC_URI[sha256sum] = "f07742c76eca868617127b5c6c9028e41c45c2b4ec25dd0db6f3b40ef7638b4e"

S = "${UNPACKDIR}/greengrass-bin"

COMPATIBLE_MACHINE:riscv64 = "null"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

# Nucleus artifacts from the prebuilt distribution (see do_install in greengrass-nucleus.inc)
GG_NUCLEUS_BINDIR = "${S}/bin"
GG_NUCLEUS_JAR = "${S}/lib/Greengrass.jar"
GG_INIT_YAML = "${S}/../greengrassv2-init.yaml"
