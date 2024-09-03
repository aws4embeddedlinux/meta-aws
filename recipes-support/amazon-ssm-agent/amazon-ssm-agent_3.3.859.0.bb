SUMMARY = "Amazon SSM Agent"
DESCRIPTION = "An agent to enable remote management of your EC2 instances, on-premises servers, or virtual machines (VMs)."
HOMEPAGE = "https://github.com/aws/amazon-ssm-agent"
CVE_PRODUCT = "amazon_ssm_agent"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "\
    file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    "

SRC_URI = "\
    git://github.com/aws/amazon-ssm-agent.git;protocol=https;branch=mainline \
    file://run-ptest \
    "

SRCREV = "194cf1f1a8fcdda97c6e00bfb0edce1dc8bbbd5a"

S = "${WORKDIR}/git"

GO_IMPORT = ""

inherit go systemd ptest

SYSTEMD_AUTO_ENABLE = "enable"
SYSTEMD_SERVICE:${PN} = "amazon-ssm-agent.service"

# src folder will break devtool upgrade
# nooelint: oelint.task.nopythonprefix
python go_do_unpack() {
    src_uri = (d.getVar('SRC_URI') or "").split()
    if len(src_uri) == 0:
        return

    fetcher = bb.fetch2.Fetch(src_uri, d)
    for url in fetcher.urls:
        if fetcher.ud[url].type == 'git':
            if fetcher.ud[url].parm.get('destsuffix') is None:
                s_dirname = os.path.basename(d.getVar('S'))
#                fetcher.ud[url].parm['destsuffix'] = os.path.join(s_dirname, 'src', d.getVar('GO_IMPORT')) + '/'
                fetcher.ud[url].parm['destsuffix'] = os.path.join(s_dirname, '', d.getVar('GO_IMPORT')) + '/'
    fetcher.unpack(d.getVar('WORKDIR'))
}

# src folder will break devtool upgrade
go_do_configure() {
#      ln -snf ${S}/src ${B}/
    ln -snf ${S} ${B}/

    # Manually set the correct version.
    cat <<END > ${S}/agent/version/version.go
package version

const Version = "${PV}"
END

}

do_compile () {
    cd ${S}

    ${GO} build -trimpath -o ${B}/amazon-ssm-agent -v \
        core/agent.go core/agent_unix.go core/agent_parser.go

    ${GO} build -trimpath -o ${B}/ssm-agent-worker -v \
        agent/agent.go agent/agent_unix.go agent/agent_parser.go

    ${GO} build -trimpath -o ${B}/updater -v \
        agent/update/updater/updater.go agent/update/updater/updater_unix.go

    ${GO} build -trimpath -o ${B}/ssm-cli -v \
        agent/cli-main/cli-main.go

    ${GO} build -trimpath -o ${B}/ssm-document-worker -v \
        agent/framework/processor/executer/outofproc/worker/main.go

    ${GO} build -trimpath -o ${B}/ssm-session-logger -v \
        agent/session/logging/main.go

    ${GO} build -trimpath -o ${B}/ssm-session-worker -v \
        agent/framework/processor/executer/outofproc/sessionworker/main.go

    ${GO} build -trimpath -o ${B}/ssm-setup-cli -v \
        agent/setupcli/setupcli.go

}

do_install () {
    install -d ${D}${bindir}/
    install -m 755 amazon-ssm-agent ${D}${bindir}/
    install -m 755 ssm-agent-worker ${D}${bindir}/
    install -m 755 updater ${D}${bindir}/
    install -m 755 ssm-cli ${D}${bindir}/
    install -m 755 ssm-document-worker ${D}${bindir}/
    install -m 755 ssm-session-logger ${D}${bindir}/
    install -m 755 ssm-session-worker ${D}${bindir}/
    install -m 755 ssm-setup-cli ${D}${bindir}/

    # TODO(glimsdal): This is hard coded in the SSM source. We should probably
    # patch the file or override the variable at link time.
    install -d ${D}${sysconfdir}/amazon/ssm
    install -m 644 ${S}/seelog_unix.xml ${D}${sysconfdir}/amazon/ssm/seelog.xml
    install -d ${D}${systemd_unitdir}/system/
    install -m 644 ${S}/packaging/linux/amazon-ssm-agent.service ${D}${systemd_unitdir}/system/amazon-ssm-agent.service
}

# nooelint: oelint.vars.insaneskip:INSANE_SKIP
INSANE_SKIP:${PN} += "textrel"
