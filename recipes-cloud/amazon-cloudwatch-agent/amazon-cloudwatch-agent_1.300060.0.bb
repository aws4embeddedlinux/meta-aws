SUMMARY = "Amazon CloudWatch Agent"
DESCRIPTION = "CloudWatch Agent enables you to collect and export host-level metrics and logs on instances running Linux or Windows server."
HOMEPAGE = "https://github.com/aws/amazon-cloudwatch-agent/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=4dde6168ca1ce801034ffe20cabf2b37"

COMPATIBLE_MACHINE = "(^$)"
COMPATIBLE_MACHINE:aarch64 = "(.*)"
COMPATIBLE_MACHINE:x86-64 = "(.*)"

# nooelint: oelint.vars.specific
COMPATIBLE_HOST:arm = "null"

inherit go-mod go-mod-update-modules ptest

RECIPE_UPGRADE_EXTRA_TASKS += "do_update_modules"

INSANE_SKIP:${PN} += "license-exists"

# Disable license and SPDX tasks due to Go module license issues
do_populate_lic[noexec] = "1"
do_create_spdx[noexec] = "1"
do_create_package_spdx[noexec] = "1"

GO_IMPORT = "github.com/aws/amazon-cloudwatch-agent"

SRC_URI = "\
    git://${GO_IMPORT}.git;branch=main;protocol=https;destsuffix=${GO_SRCURI_DESTSUFFIX} \
    file://run-ptest \
    "

SRCREV = "01b3fbdbe2d2234a44b1219d4f71d11335b771f2"

# Include the generated files (these will be created by do_update_modules)
require ${BPN}-go-mods.inc
require ${BPN}-licenses.inc

do_compile:prepend() {
    export BUILD_SPACE="${B}"
    export CGO_LDFLAGS="-w"
    export BUILDTAGS="static_build"
    export GO_BUILD_FLAGS="-trimpath"
    export GOARCH="${TARGET_GOARCH}"
    export GOPATH="${UNPACKDIR}/git/:${S}/src/import/.gopath:${S}/src/import/vendor:${STAGING_DIR_TARGET}/${prefix}/local/go:${UNPACKDIR}/git/"
}

# nooelint: oelint.task.dash
go_do_compile:x86-64() {
    oe_runmake -C ${S}/src/github.com/aws/amazon-cloudwatch-agent/ build-for-docker-amd64
}

go_do_compile:aarch64() {
    oe_runmake -C ${S}/src/github.com/aws/amazon-cloudwatch-agent/ build-for-docker-arm64
}

go_do_install:aarch64() {
    install -d ${D}${bindir}/
    cp ${S}/src/github.com/aws/amazon-cloudwatch-agent/build/bin/linux_arm64/amazon-cloudwatch-agent ${D}${bindir}/
    cp ${S}/src/github.com/aws/amazon-cloudwatch-agent/build/bin/linux_arm64/start-amazon-cloudwatch-agent  ${D}${bindir}/
}

# nooelint: oelint.task.dash
go_do_install:x86-64() {
    install -d ${D}${bindir}/
    cp ${S}/src/github.com/aws/amazon-cloudwatch-agent/build/bin/linux_amd64/amazon-cloudwatch-agent ${D}${bindir}/
    cp ${S}/src/github.com/aws/amazon-cloudwatch-agent/build/bin/linux_amd64/start-amazon-cloudwatch-agent ${D}${bindir}/
}

# nooelint: oelint.vars.insaneskip
INSANE_SKIP:${PN} += "textrel already-stripped"
