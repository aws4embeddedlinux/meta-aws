# Recipe created by recipetool
# This is the basis of a recipe and may need further editing in order to be fully functional.
# (Feel free to remove these comments when editing.)

# WARNING: the following LICENSE and LIC_FILES_CHKSUM values are best guesses - it is
# your responsibility to verify that the values are complete and correct.
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=000b2cc208c380dab61c7176d8ad5cfc"

SRC_URI = "gitsm://github.com/aws/SigV4-for-AWS-IoT-embedded-sdk.git;protocol=https;branch=main"

# Modify these as desired
PV = "1.0+git"
SRCREV = "892bcbb2d4b95daf2b7306ba3210e74b25bfae16"

inherit cmake

# Specify any options you want to pass to cmake using EXTRA_OECMAKE:
EXTRA_OECMAKE = ""

