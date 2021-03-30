DESCRIPTION = "Push metrics and logs to CloudWatch from any system that can run Python"
HOMEPAGE = "https://github.com/awslabs/amazon-cloudwatch-publisher"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

RDEPENDS_${PN} += " \
	python3-boto3 \
	python3-psutil \
	python3-requests \
	python3-timeloop \
"

inherit systemd

SYSTEMD_SERVICE_${PN} = "${BPN}.service"
SYSTEMD_AUTO_ENABLE = "enable"

FILES_${PN} += " \
	${systemd_system_unitdir}/${BPN}.service \
	/opt/aws/${BPN}/${BPN} \
	/opt/aws/${BPN}/etc/.gitkeep \
	/opt/aws/${BPN}/logs/.gitkeep \
"

SRC_URI += "git://github.com/awslabs/${BPN};protocol=https;tag=1.2.2"

do_install() {
	install -d ${D}${systemd_system_unitdir}
	install -d ${D}/opt/aws/${BPN}/etc
	touch ${D}/opt/aws/${BPN}/etc/.gitkeep
	install -d ${D}/opt/aws/${BPN}/logs
	touch ${D}/opt/aws/${BPN}/logs/.gitkeep
	install -m 744 ${WORKDIR}/git/${BPN} ${D}/opt/aws/${BPN}/

	# TODO: user and group should be cwpublisher/wheel
	cat <<-EOF > ${D}${systemd_system_unitdir}/${BPN}.service
[Unit]
Description=Push metrics and logs to CloudWatch from any system that can run Python
DefaultDependencies=no
Requires=network.target
After=network.target network-online.target

[Service]
Type=simple
User=root
Group=root
ExecStart=/opt/aws/${BPN}/${BPN}
Restart=on-failure
WorkingDirectory=/opt/aws/${BPN}

[Install]
WantedBy=multi-user.target
EOF
}
