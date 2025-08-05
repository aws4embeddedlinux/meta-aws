SUMMARY = "AWS IoT Greengrass Hello World Python Component"
DESCRIPTION = "A simple hello world Python component for AWS IoT Greengrass v2 with MQTT publishing"
HOMEPAGE = "https://github.com/aws4embeddedlinux/meta-aws"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

COMPONENT_NAME = "com.example.HelloWorldPython"
COMPONENT_VERSION = "1.0.0"
COMPONENT_ARTIFACTS = "hello_world.py"

RDEPENDS:${PN} += "aws-iot-device-sdk-python-v2"

inherit greengrass-component

SRC_URI = " \
    file://hello_world.py \
    file://component-recipe.yaml \
    "
