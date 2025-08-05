SUMMARY = "AWS IoT Greengrass Hello World bash Component"
DESCRIPTION = "A simple hello world bash component for AWS IoT Greengrass v2"
HOMEPAGE = "https://github.com/aws4embeddedlinux/meta-aws"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

COMPONENT_NAME = "com.example.BashHelloWorld"
COMPONENT_VERSION = "1.0.0"

inherit greengrass-component

SRC_URI = " \
    file://config.yaml.template \
    "
