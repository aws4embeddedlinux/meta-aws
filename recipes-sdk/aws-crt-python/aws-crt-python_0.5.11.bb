inherit setuptools3
require aws-crt-python.inc

LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

BRANCH ?= "master"

SRC_URI = "git://github.com/awslabs/aws-crt-python.git;branch=${BRANCH};name=aws-crt-python"
SRCREV = "2d19abb7fc360416202f9c590971c91c84dc2c72"
