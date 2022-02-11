# AWS IoT FleetWise Edge Agent

This recipe builds [the AWS IoT FleetWise Edge Agent](https://github.com/aws/aws-iot-fleetwise-edge)
and installs it as a `systemd` service named `fwe@0`.

Refer to the [AWS IoT FleetWise Edge Agent Developer Guide](https://github.com/aws/aws-iot-fleetwise-edge/blob/main/docs/dev-guide/edge-agent-dev-guide.md)
for more information about configuring the template configuration file installed at
`/etc/aws-iot-fleetwise/config-0.json`, provisioning AWS IoT Core credentials, registering the
device as a IoT FleetWise Vehicle, and deploying IoT FleetWise Campaigns from the cloud.
