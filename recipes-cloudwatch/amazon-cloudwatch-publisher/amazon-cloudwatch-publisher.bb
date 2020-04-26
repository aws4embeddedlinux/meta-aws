DESCRIPTION = "Push metrics and logs to CloudWatch from any system that can run Python"
HOMEPAGE = "https://github.com/awslabs/${PN}"
LICENSE = "Apache-2.0"

RDEPENDS_${PN} += " \
	python3-boto3 \
	python3-psutil \
	python3-requests \
	python3-timeloop \
"

inherit systemd

SYSTEMD_SERVICE_${PN} = "${PN}.service"
SYSTEMD_AUTO_ENABLE = "enable"

FILES_${PN} += " \
	${systemd_system_unitdir}/${PN}.service
	/opt/aws/${PN}/${PN}
"

SRC_URI += " \
	git://github.com/awslabs/${PN};destsuffix=${PN};protocol=http;rev=8714318477f2d627e5eea5a7b8c354dabaf6a4e1 \
"

do_install() {
	install -d ${D}/opt/aws/${PN}/etc
	install -d ${D}/opt/aws/${PN}/logs
	install -m 644 ${WORKDIR}/${PN}/${PN}.service ${D}${systemd_system_unitdir}/
	install -m 644 ${WORKDIR}/${PN}/${PN} ${D}/opt/aws/${PN}/

	cat <<-EOF > ${D}${systemd_system_unitdir}/${PN}.service
[Unit]
Description=Push metrics and logs to CloudWatch from any system that can run Python
DefaultDependencies=no
Requires=network.target
After=network.target network-online.target

[Service]
Type=simple
# TODO: user and group should be cwpublisher/wheel
User=root
Group=root
ExecStart=${PN}
Restart=on-failure
WorkingDirectory=/opt/aws/${PN}

[Install]
WantedBy=multi-user.target
EOF
}
