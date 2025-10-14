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

python fix_spdx_unknown() {
    import re
    license_file = d.getVar('THISDIR') + '/amazon-cloudwatch-agent-licenses.inc'
    try:
        with open(license_file, 'r') as f:
            content = f.read()
        content = re.sub(r'(apache/thrift.*|google/cadvisor.*|docker/.*|opencontainers/.*|gophercloud/.*|openshift/.*|mrunalp/.*|vishvananda/.*|spf13/cobra.*|magiconair/.*|ionos-cloud/.*|xdg-go/.*|sigs\.k8s\.io/.*)spdx=Unknown', r'\1spdx=Apache-2.0', content)
        content = re.sub(r'(.*singleflight.*|cyphar/.*|fsnotify/.*|golang/snappy.*|google/go-.*|gorilla/mux.*|grpc-ecosystem/.*|miekg/.*|pierrec/.*|pmezard/.*|prometheus.*gddo.*|klauspost.*snapref.*|shirou/gopsutil@v3.*|spf13/pflag.*|gonum\.org.*|gopkg\.in/.*|k8s\.io.*third_party.*)spdx=Unknown', r'\1spdx=BSD-3-Clause', content)
        content = re.sub(r'(deckarep/.*|digitalocean/.*|go-zookeeper/.*|iancoleman/.*|imdario/.*|pelletier/.*|scaleway/.*|gosnmp/.*)spdx=Unknown', r'\1spdx=MIT', content)
        content = re.sub(r'(hashicorp/(?!cronexpr).*)spdx=Unknown', r'\1spdx=MPL-2.0', content)
        content = re.sub(r'(hashicorp/cronexpr.*)spdx=Unknown', r'\1spdx=GPL-3.0-only', content)
        content = re.sub(r'(rcrowley/.*)spdx=Unknown', r'\1spdx=BSD-2-Clause', content)
        content = re.sub(r'LICENSE \+= "& Apache-2\.0 & BSD-2-Clause & BSD-3-Clause & ISC & MIT & MPL-2\.0 & Unknown & Zlib"', 'LICENSE += "& Apache-2.0 & BSD-2-Clause & BSD-3-Clause & GPL-3.0-only & ISC & MIT & MPL-2.0 & Zlib"', content)
        with open(license_file, 'w') as f:
            f.write(content)
    except: pass
}

do_update_modules[postfuncs] += "fix_spdx_unknown"

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
