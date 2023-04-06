SUMMARY = "AWS Greengrass Core Python SDK"
DESCRIPTION =  "The AWS IoT Greengrass Core SDK is meant to be used by AWS Lambda functions \
                running on an AWS IoT Greengrass Core. It will enable Lambda functions to \
                invoke other Lambda functions deployed to the Greengrass Core, publish messages \
                to the Greengrass Core and work with the local Shadow service."

HOMEPAGE = "https://github.com/aws/aws-greengrass-core-sdk-python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d9a734997394df8920646c5a08be0ea7"

SRC_URI = "git://github.com/aws/aws-greengrass-core-sdk-python.git;protocol=https;tag=v${PV}"

S = "${WORKDIR}/git"

RDEPENDS_${PN} = "python3"

# Copy examples into rootfs
do_install_append() {
    install -d ${D}${datadir}/${BPN}/
    cp --preserve=mode,timestamps -r ${S}/greengrasssdk/ ${D}${datadir}/${BPN}
    cp --preserve=mode,timestamps -r ${S}/examples/ ${D}${datadir}/${BPN}
    cp --preserve=mode,timestamps -r ${S}/LICENSE ${D}${datadir}/${BPN}
}

# Associate generated files with package
FILES_${PN} += "${datadir}/${BPN}/*"
