inherit cmake
require aws-iot-device-sdk-cpp-v2.inc

LIC_FILES_CHKSUM = "file://LICENSE;md5=f91e61641e7a96835dea6926a65f4702"

BRANCH ?= "main"

SRC_URI = "git://github.com/aws/aws-iot-device-sdk-cpp-v2.git;branch=${BRANCH};name=aws-iot-device-sdk-cpp-v2"
SRCREV = "4b16a0236be89d77375c43c9ffb8be5b929e6227"
