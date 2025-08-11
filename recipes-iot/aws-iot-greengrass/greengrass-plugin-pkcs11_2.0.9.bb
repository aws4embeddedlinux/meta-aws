# SPDX-FileCopyrightText: Copyright (c) 2025, Linaro Limited.
#
# SPDX-License-Identifier: MIT

SUMMARY = "AWS Greengrass Crypto PKCS11 Provider"
DESCRIPTION = "Install Java based AWS Greengrass crypto pkcs11 provider plugin"
HOMEPAGE = "https://github.com/aws-greengrass/aws-greengrass-pkcs11-provider"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${WORKDIR}/LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

PLUGIN_SRC_NAME = "aws.greengrass.crypto.Pkcs11Provider-${PV}.jar"
PLUGIN_NAME = "aws.greengrass.crypto.Pkcs11Provider.jar"

inherit greengrass-plugin

# PKCS11 Plugin version: 2.0.9
SRC_URI = "https://d2s8p88vqu9w66.cloudfront.net/releases/Pkcs11Provider/aws.greengrass.crypto.Pkcs11Provider-${PV}.jar;unpack=false; \
           https://raw.githubusercontent.com/aws-greengrass/aws-greengrass-pkcs11-provider/v${PV}/LICENSE;name=license; \
           file://config.yaml.template;"

SRC_URI[sha256sum] = "00516cbdaa39fd3a9436eba3efae5388a7a82b3fedc60a5af675200737f5737c"
SRC_URI[license.sha256sum] = "09e8a9bcec8067104652c168685ab0931e7868f9c8284b66f5ae6edae5f1130b"
