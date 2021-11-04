DESCRIPTION = "Push metrics and logs to CloudWatch from any system that can run Python"
HOMEPAGE = "https://github.com/awslabs/${PN}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

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
	${systemd_system_unitdir}/${PN}.service \
	/opt/aws/${PN}/${PN} \
	/opt/aws/${PN}/etc/.gitkeep \
	/opt/aws/${PN}/logs/.gitkeep \
"

SRC_URI += " \
	git://github.com/awslabs/${PN};protocol=https;protocol=http;rev=8714318477f2d627e5eea5a7b8c354dabaf6a4e1 \
"

do_install() {
	install -d ${D}${systemd_system_unitdir}
	install -d ${D}/opt/aws/${PN}/etc
	touch ${D}/opt/aws/${PN}/etc/.gitkeep
	install -d ${D}/opt/aws/${PN}/logs
	touch ${D}/opt/aws/${PN}/logs/.gitkeep
	install -m 744 ${WORKDIR}/git/${PN} ${D}/opt/aws/${PN}/

	# TODO: user and group should be cwpublisher/wheel
	cat <<-EOF > ${D}${systemd_system_unitdir}/${PN}.service
[Unit]
Description=Push metrics and logs to CloudWatch from any system that can run Python
DefaultDependencies=no
Requires=network.target
After=network.target network-online.target

[Service]
Type=simple
User=root
Group=root
ExecStart=/opt/aws/${PN}/${PN}
Restart=on-failure
WorkingDirectory=/opt/aws/${PN}

[Install]
WantedBy=multi-user.target
EOF
}
