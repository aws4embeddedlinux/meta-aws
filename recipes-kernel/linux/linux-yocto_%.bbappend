FILESEXTRAPATHS:prepend:aws-ec2 := "${THISDIR}/files:"

COMPATIBLE_MACHINE:aws-ec2 = "aws-ec2"

SRC_URI:append:aws-ec2 = " \
    file://aws-ec2.cfg \
    "
