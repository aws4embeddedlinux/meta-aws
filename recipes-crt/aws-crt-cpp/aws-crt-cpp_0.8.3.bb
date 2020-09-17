inherit cmake
require aws-crt-cpp.inc

LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

BRANCH ?= "master"

SRC_URI = "git://github.com/awslabs/aws-crt-cpp.git;branch=${BRANCH};name=aws-crt-cpp"
SRCREV = "506422e110c58d6dd3cb8c9f8818dc22311ac8e1"
