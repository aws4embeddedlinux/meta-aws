SUMMARY = "AWS Greengrass Core SDK Python with Example"
DESCRIPTION = "A simple hello world application"
LICENSE = "Apache-2.0"

BRANCH = "master"
SRCREV = "${AUTOREV}"
SRC_URI = "git://github.com/aws/aws-greengrass-core-sdk-python.git;protocol=https;branch=${BRANCH}"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d9a734997394df8920646c5a08be0ea7"

S = "${WORKDIR}/git"

# Needed for install script
DEPENDS = "python3 python3-setuptools"
RDEPENDS_${PN} = "python3"

# Copy sdk into rootfs and run python3 install script
do_install() {
    install -d ${D}/greengrass/aws-greengrass-core-sdk-python
    cp -r ${S}/* ${D}/greengrass/aws-greengrass-core-sdk-python
    python3 ${D}/greengrass/aws-greengrass-core-sdk-python/setup.py install
}

# Associate generated files with package
FILES_${PN} += "/greengrass/aws-greengrass-core-sdk-python/*"
