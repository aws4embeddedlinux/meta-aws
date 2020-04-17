README.txt

You may set your preferred version in <project>/conf/local.conf

#PREFERRED_VERSION_aws-iot-device-sdk-embedded-c = "3.0.1"
PREFERRED_VERSION_aws-iot-device-sdk-embedded-c = "4-beta"

Default will be "4-beta" if not set.

Version 3.0.1:

	Builds a single statically linked binary in /usr/bin/subscribe_publish_sample
	To run the application you will need to add you certs and private key
	(Usually generated in AWS Console)

	# /usr/bin/subscribe_publish_sample \
		-h <endpoint url> \
		-p <port:8883> \
		-c <path/to/certs> 
	
	Certs are expected to be named:
		rootCA     <path/to/certs>/rootCA.crt
		clientCRT  <path/to/certs>/cert.pem
		clientKey  <path/to/certs>/privkey.pem


Version 4-beta:

	Builds the shared (*.so) libs and installs them in /usr/lib/
	and also installs the demo apps in /usr/bin/

	# /usr/bin/iot_demo_mqtt \
		-h <endpoint-url> \
		-p <port:8883> \
		-r <path/to/certs/rootCA.crt> \
		-c <path/to/certs/cert.pem> \
		-k <path/to/certs/privkey.pem 
