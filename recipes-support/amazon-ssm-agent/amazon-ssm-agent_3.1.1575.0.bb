LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = " \
    file://src/${GO_IMPORT}/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    "

GO_IMPORT = "github.com/aws/amazon-ssm-agent"
SRC_URI = "git://${GO_IMPORT};branch=mainline;protocol=https"

SRCREV = "c4414a04a161ed90e141050fb1a8cc7f43835e70"

S = "${WORKDIR}/git"

inherit go systemd

SYSTEMD_AUTO_ENABLE = "enable"
SYSTEMD_SERVICE:${PN} = "amazon-ssm-agent.service"

do_compile () {
    cd ${S}/src/${GO_IMPORT}

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
    install -d ${D}/etc/amazon/ssm
    install -m 644 ${S}/src/${GO_IMPORT}/seelog_unix.xml ${D}/etc/amazon/ssm/seelog.xml
    install -d ${D}${systemd_unitdir}/system/
    install -m 644 ${S}/src/${GO_IMPORT}/packaging/linux/amazon-ssm-agent.service ${D}${systemd_unitdir}/system/amazon-ssm-agent.service
}

