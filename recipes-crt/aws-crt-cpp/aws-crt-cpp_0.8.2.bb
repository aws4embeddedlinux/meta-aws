inherit cmake
require aws-crt-cpp.inc

LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

BRANCH ?= "master"

SRC_URI = "git://github.com/awslabs/aws-crt-cpp.git;branch=${BRANCH};name=aws-crt-cpp"
SRCREV = "e84c3402ee0a1d00d5d66a0a8e803f33a64fccb4"
