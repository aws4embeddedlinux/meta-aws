DESCRIPTION = "Push metrics and logs to CloudWatch from any system that can run Python"
HOMEPAGE = "https://github.com/awslabs/amazon-cloudwatch-publisher"
LICENSE = "Apache-2.0"

RDEPENDS_${PN} += " \
	python3-boto3 \
	python3-psutil \
	python3-requests \
	python3-timeloop \
"

inherit systemd

SYSTEMD_SERVICE_${PN} = "amazon-cloudwatch-publisher.service"
SYSTEMD_AUTO_ENABLE = "enable"

FILES_${PN} += " \
	${systemd_system_unitdir}/amazon-cloudwatch-publisher.service
"

SRC_URI += " \
	git://github.com/awslabs/amazon-cloudwatch-publisher;destsuffix=amazon-cloudwatch-publisher;protocol=http;rev=8714318477f2d627e5eea5a7b8c354dabaf6a4e1 \
"

do_install() {
	install -m 644 ${WORKDIR}/amazon-cloudwatch-publisher/amazon-cloudwatch-publisher.service ${D}${systemd_system_unitdir}/
}
