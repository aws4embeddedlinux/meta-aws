inherit cmake
require aws-iot-device-sdk-cpp-v2.inc

LIC_FILES_CHKSUM = "file://LICENSE;md5=f91e61641e7a96835dea6926a65f4702"

BRANCH ?= "main"

SRC_URI = "git://github.com/aws/aws-iot-device-sdk-cpp-v2.git;branch=${BRANCH};name=aws-iot-device-sdk-cpp-v2"
SRCREV = "b7a5dd3eb7d89d799c5928d9c041696a7850a3a9"
