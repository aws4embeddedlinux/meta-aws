# the meta-aws project
<table border="0" rules="none">
<tr border="0">
<td width="150" height="150"><img alt="Yocto Project Platinum Member"
src="images/lf_yp_plat.png"></td>
<td width="150" height="150"><img alt="Yocto Project Layer Compatible"
src="images/LF_17_02_Yocto-Badge-Update_Compatible_Final_Blank.png"></td>
<td width="150" height="150"><img alt="Automotive Grade Linux Platinum Member"
src="images/agl-member-plat.png"></td>
</tr>
</table>

The **meta-aws project** provides *recipes* for building in AWS edge software capabilities to [Embedded Linux](https://elinux.org) built with [OpenEmbedded](https://www.openembedded.org) and [Yocto Project](https://www.yoctoproject.org/) build frameworks.

Please check out [our sister project meta-aws-demos](https://github.com/aws-samples/meta-aws-demos)!  Over time, we will continuously be adding MACHINE specific demonstrations for AWS software on Embedded Linux built by the Yocto Project build framework with the meta-aws Metadata Layer.

### Supported Yocto Project Releases

We are supporting customers building solutions on AWS with meta-aws for the following Yocto Project releases.  Let us know if you need AWS device software for a specific Yocto Project release and we will work with you through Github Issues to resolve the challenge you might be facing.  We also encourage [**contributions**](CONTRIBUTING.md) by the community.

<table>
<tr><th>Release branch <a href="https://wiki.yoctoproject.org/wiki/Releases" target="none" title="What is this?">(?)</a></th><th>Layer integrity check <a href="https://www.yoctoproject.org/docs/2.5/dev-manual/dev-manual.html#making-sure-your-layer-is-compatible-with-yocto-project" target="none" title="What is this?">(?)</a></th></tr>
<tr><td>master</td><td><img src="https://codebuild.us-west-2.amazonaws.com/badges?uuid=eyJlbmNyeXB0ZWREYXRhIjoibjRaeDZVUzQrdTRtaDhmb0xRK0FCdndZRU00TytjSEJ6bGR2RzFLZFdOaTIwU0sxOFk5RUZ1Yk04VGxvWVlIVXIvai9aSUIyL1RkaXlSalAxS0IxWGdZPSIsIml2UGFyYW1ldGVyU3BlYyI6IkVWQnd5bXA4Si9zaW04OU4iLCJtYXRlcmlhbFNldFNlcmlhbCI6MX0%3D&branch=master"/></td></tr>
<tr><td>kirkstone</td><td><img src="https://codebuild.us-west-2.amazonaws.com/badges?uuid=eyJlbmNyeXB0ZWREYXRhIjoiUTVKN3F4ZGFZNUpkeFovZW5CTTVwQktlUThuRHZZTzNONWJ0M01ocUJWOVpwL2Y3NURVK2ovVVNoMjhMbFhpSTlkcFhiS0dPRzErWnhza2xqbkV3ZXpBPSIsIml2UGFyYW1ldGVyU3BlYyI6IkZBSTRWRHZXM082ZndXT1EiLCJtYXRlcmlhbFNldFNlcmlhbCI6MX0%3D&branch=kirkstone"/></td></tr>
<tr><td>honister</td><td><img src="https://codebuild.us-west-2.amazonaws.com/badges?uuid=eyJlbmNyeXB0ZWREYXRhIjoieE0zYzlVdG10WWdYT0RwUGhQRW80dVE5OVNWd1RjQ2pna0xoQm1kc09odjQ0ZjRSQmpveWVrdksrREdDVzcrQW10eVE5SFVpRmNHM2FnbnZMdmU0ZzVzPSIsIml2UGFyYW1ldGVyU3BlYyI6IlYrcmhWdkxqakNHYkUxUnMiLCJtYXRlcmlhbFNldFNlcmlhbCI6MX0%3D&branch=honister"/></td></tr>
<tr><td>hardknott</td><td><img src="https://codebuild.us-west-2.amazonaws.com/badges?uuid=eyJlbmNyeXB0ZWREYXRhIjoiaFQ3SEZZOTZGamdQWkt5RUREZS9XTjVxY1IvMlliUXRZcFVXODQxMjJDSW5tK0xWbzRneDdxQ2toZS93ek8zOXNCcHc1UFV2QVdjTXloRE5JMUU2Q0owPSIsIml2UGFyYW1ldGVyU3BlYyI6InRRay9YRTNyTTZGWUFWTTciLCJtYXRlcmlhbFNldFNlcmlhbCI6MX0%3D&branch=hardknott"/></td></tr>
<tr><td>gatesgarth </td><td><img src="https://codebuild.us-west-2.amazonaws.com/badges?uuid=eyJlbmNyeXB0ZWREYXRhIjoieTNEbG5BdFRvdXNJU20zUUprZU1EM2Q5c0RnNW9hRFVRQ2ZJeUFJSnN4UVQxdFhJNS9yMnhwaHB0dlhZSXZzTVUwMHRJWHZyU1NNRTc5WlJVbVBWZ1ZVPSIsIml2UGFyYW1ldGVyU3BlYyI6InJVSDhaZHhHN29ERjlqMEIiLCJtYXRlcmlhbFNldFNlcmlhbCI6MX0%3D&branch=gatesgarth"/></td></tr>
<tr><td>dunfell</td><td><img src="https://codebuild.us-west-2.amazonaws.com/badges?uuid=eyJlbmNyeXB0ZWREYXRhIjoiTk9VRSs1aHBiMXpMTzJLbVlBTVFvUXU5blNQb0hzd2o5TEJZWUVwajBnbXpacmROMlM4VzE4aWU4dDIvWG9nTHN5TXY1UWxNeW1LL1lrTkVYcWJmcE1BPSIsIml2UGFyYW1ldGVyU3BlYyI6IlM5emZaLzVaTDVxL09DYnQiLCJtYXRlcmlhbFNldFNlcmlhbCI6MX0%3D&branch=dunfell"/></td></tr>
<tr><td>zeus (breakfix only)</td><td><img src="https://codebuild.us-east-1.amazonaws.com/badges?uuid=eyJlbmNyeXB0ZWREYXRhIjoiYXZDSnIwdXVNSFRjMW13MmxVOVZYZVJhVktKTkVScG1UQURqTDdpMHlrYXFKS2x0VHdXV1ZzeUVxR0Q1cVU4RTBtUkkzWnVOMjRPdVZhKzRhSTBqQkI4PSIsIml2UGFyYW1ldGVyU3BlYyI6IlNwZXFGbXJ3U0ZwM1dLQVciLCJtYXRlcmlhbFNldFNlcmlhbCI6MX0%3D&branch=zeus"/></td></tr>
<tr><td>thud (breakfix only)</td><td><img src="https://codebuild.us-east-1.amazonaws.com/badges?uuid=eyJlbmNyeXB0ZWREYXRhIjoiK0IwWDFuaTZFZFd4NVY3N2UwdGdGcWtsMGtDc2dRVEgzMHdxV2V6TU5ZRmpOU01MaUtreVowSlJid204VHEwelIzdGoxQnJ4RnViNWI0bjQ3cEgxN1k4PSIsIml2UGFyYW1ldGVyU3BlYyI6IjA3ZndZRHFjeVlKMGUvQ2IiLCJtYXRlcmlhbFNldFNlcmlhbCI6MX0%3D&branch=thud"/></td></tr>
<tr><td>warrior (breakfix only)</td><td><img src="https://codebuild.us-west-2.amazonaws.com/badges?uuid=eyJlbmNyeXB0ZWREYXRhIjoiR0kyTTJZek11SHkwTGJ2QVlsWE1uTjUyNTJYdjhmaEtKVUcvZXNWUzFBSXFBUHJuN0hoc2k0SVRMRWhWOWRBcWovTEQ3RUF6aWdpbmhtZEN5RUR4VDRBPSIsIml2UGFyYW1ldGVyU3BlYyI6Inoyd1VHbU5Qd0FGTXhBYTIiLCJtYXRlcmlhbFNldFNlcmlhbCI6MX0%3D&branch=warrior"/></td></tr>
<tr><td>sumo (breakfix only)</td><td><img src="https://codebuild.us-west-2.amazonaws.com/badges?uuid=eyJlbmNyeXB0ZWREYXRhIjoiOHhleEREbml5SDJnanZQL0FIb1lKZDlmT1JFZ3dROGdwekhiVEhYWGxSVzQwSGFKM09DaWplcnNwNFVRMTVsd2hTZytwdE9KNXNMRUdlNDZ6ck0xaDBzPSIsIml2UGFyYW1ldGVyU3BlYyI6InVmQnp4a2h4d1pEU0xDeUYiLCJtYXRlcmlhbFNldFNlcmlhbCI6MX0%3D&branch=sumo"/></td></tr>
</table>

All prior releases will be handled on a case by case basis.  Again, please let us know if you're in a crunch on earlier releases and we'll help you the best we can!

### Dependencies

**meta-aws** supports a wide variety of device software.  This layer defines a minimum dependency set that covers many of the recipes.  Sometimes, the recipe will require additional layers either to support optional features or programming languages not supported by OpenEmbedded. When those requirements surface, they are documented in recipe specific README files.

Base dependencies:

* core
* openembedded-layer (meta-oe)
* networking-layer (meta-networking)
* meta-python

### Supported recipes for services, software, and SDKs

These are the currently supported services, software, and SDKs you can use to build AWS solutions with many types of devices when building your distribution with the Yocto Project.


|Service, Software, or SDK |Details |
|:------------------------:|:-------|
|<center>![Image of Amazon CloudWatch Icon](images/Arch_Amazon-CloudWatch_64.png)<br/>Amazon CloudWatch Publisher</center>|Installs and configures the [Amazon CloudWatch Publisher](https://github.com/awslabs/amazon-cloudwatch-publisher).<br/>Amazon CloudWatch provides a wealth of tools for monitoring resources and applications in real-time. However, out-of-the-box support is limited to AWS-native resources (e.g. EC2 instances) or systems compatible with the CloudWatch Agent.|
|![Image of AWS Command Line Icon](images/Arch_AWS-Command-Line-Interface_64.png)<br/>AWS Command Line Interface v1|The AWS Command Line Interface (CLI) is a unified tool to manage your AWS services. With just one tool to download and configure, you can control multiple AWS services from the command line and automate them through scripts.|
|<center>![Image of Javaman](images/corretto.png)<br/>Amazon Corretto</center>|Amazon Corretto is a no-cost, multiplatform, production-ready distribution of the Open Java Development Kit (OpenJDK). Corretto comes with long-term support that will include performance enhancements and security fixes. Amazon runs Corretto internally on thousands of production services and Corretto is certified as compatible with the Java SE standard.|
|<center>![Image of AWS IoT Device Client Icon](images/Arch_AWS-Tools-and-SDKs_64.png)</br>AWS IoT Device Client</center>|The AWS IoT Device Client is free, open-source, modular software written in C++ that you can compile and install on your Embedded Linux based IoT devices to access AWS IoT Core, AWS IoT Device Management, and AWS IoT Device Defender features by default.|
|<center>![Image of AWS IoT Greengrass Icon](images/Arch_AWS-IoT-Greengrass_64.png)<br/>AWS IoT Greengrass<br/>v1.0</center>|AWS IoT Greengrass is an Internet of Things (IoT) open source edge runtime and cloud service that helps you build, deploy, and manage device software. Customers use AWS IoT Greengrass for their IoT applications on millions of devices in homes, factories, vehicles, and businesses. You can program your devices to act locally on the data they generate, execute predictions based on machine learning models, filter and aggregate device data, and only transmit necessary information to the cloud.<br/><a href="recipes-iot/aws-iot-greengrass/README.md#aws-iot-greengrass-v1">README</a>|
|<center>![Image of AWS IoT Greengrass Icon](images/Arch_AWS-IoT-Greengrass_64.png)<br/>AWS IoT Greengrass<br/>v2.0</center>|AWS IoT Greengrass is an Internet of Things (IoT) open source edge runtime and cloud service that helps you build, deploy, and manage device software. Customers use AWS IoT Greengrass for their IoT applications on millions of devices in homes, factories, vehicles, and businesses. You can program your devices to act locally on the data they generate, execute predictions based on machine learning models, filter and aggregate device data, and only transmit necessary information to the cloud.<br/><a href="recipes-iot/aws-iot-greengrass/README.md#aws-iot-greengrass-v2">README</a>|
|<center>![Image of SDK Icon](images/Arch_AWS-Tools-and-SDKs_64.png)</br>AWS SDK for Python</center>|The AWS SDK for Python provides the python libraries you can use to interact with AWS Cloud. Botocore and Boto3 are available.|
|<center>![Image of AWS IoT Device SDK Icon](images/Arch_AWS-Tools-and-SDKs_64.png)</br>AWS IoT Device SDK for C++ v2|The AWS IoT C++ Device SDK allows developers to build connected applications using AWS and the AWS IoT APIs. Specifically, this SDK was designed for devices that are not resource constrained and require advanced features such as message queuing, multi-threading support, and the latest language features.|
|<center>![Image of AWS IoT Device SDK Icon](images/Arch_AWS-Tools-and-SDKs_64.png)</br>AWS IoT Device SDK for Python v2|The AWS IoT Device SDK for Python makes it possible for developers to write Python scripts to use their devices to access the AWS IoT platform through MQTT or MQTT over the WebSocket protocol. By connecting their devices to AWS IoT, users can securely work with the message broker, rules, and shadows provided by AWS IoT and with other AWS services like AWS Lambda, Kinesis, and Amazon S3, and more.|
|<center>![Image of AWS Firecracker Icon](images/Arch_AWS-Firecracker.png)</br>AWS Firecracker|AWS Firecracker Firecracker enables you to deploy workloads in lightweight virtual machines, called microVMs, which provide enhanced security and workload isolation over traditional VMs, while enabling the speed and resource efficiency of containers.|

**IMPORTANT NOTES**: 

* Automotive Grade Linux: The AGL distribution uses a specific static ID process. When adding AWS IoT Greengrass, you will need to define users in the passwd and group files manually. Please see https://github.com/aws/meta-aws/issues/75 for more information.

© 2019-2022, Amazon Web Services, Inc. or its affiliates. All rights reserved.
