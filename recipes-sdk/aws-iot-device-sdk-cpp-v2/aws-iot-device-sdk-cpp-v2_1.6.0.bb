inherit cmake
require aws-iot-device-sdk-cpp-v2.inc

LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

BRANCH ?= "main"

SRC_URI = "git://github.com/aws/aws-iot-device-sdk-cpp-v2.git;branch=${BRANCH};name=aws-iot-device-sdk-cpp-v2"
SRCREV = "65b8344ddb6173d312dff4e759a0dfbc05a97e4c"
