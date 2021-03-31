# meta-aws

The **meta-aws** project provides *recipes* for building in AWS edge software capabilities to [Embedded Linux](https://elinux.org) built with [OpenEmbedded](https://www.openembedded.org) and [Yocto Project](https://www.yoctoproject.org/) build frameworks.

Please check out [our sister project meta-aws-demos](https://github.com/aws-samples/meta-aws-demos)!  Over time, we will continuously be adding MACHINE specific demonstrations for AWS software on Embedded Linux built by the Yocto Project build framework with the meta-aws Metadata Layer.

These are the currently supported services, software, and SDKs you can use to build AWS solutions with many types of devices when building your distribution with the Yocto Project.

<center>Service, Software, or SDK</center> | <center>Details</center>
---|---
<center>![Image of Amazon CloudWatch Icon](images/Arch_Amazon-CloudWatch_64.png)<br/>Amazon CloudWatch Publisher</center>|Installs and configures the [Amazon CloudWatch Publisher](https://github.com/awslabs/amazon-cloudwatch-publisher).<br/>Amazon CloudWatch provides a wealth of tools for monitoring resources and applications in real-time. However, out-of-the-box support is limited to AWS-native resources (e.g. EC2 instances) or systems compatible with the CloudWatch Agent.
<center>![Image of Javaman](images/corretto.png)<br/>Amazon Corretto</center>|Amazon Corretto is a no-cost, multiplatform, production-ready distribution of the Open Java Development Kit (OpenJDK). Corretto comes with long-term support that will include performance enhancements and security fixes. Amazon runs Corretto internally on thousands of production services and Corretto is certified as compatible with the Java SE standard.
<center>![Image of AWS IoT Device Client Icon](images/Arch_AWS-Tools-and-SDKs_64.png)</br>AWS IoT Device Client</center>|The AWS IoT Device Client is free, open-source, modular software written in C++ that you can compile and install on your Embedded Linux based IoT devices to access AWS IoT Core, AWS IoT Device Management, and AWS IoT Device Defender features by default.
<center>![Image of AWS IoT Greengrass Icon](images/Arch_AWS-IoT-Greengrass_64.png)<br/>AWS IoT Greengrass</center>|AWS IoT Greengrass is an Internet of Things (IoT) open source edge runtime and cloud service that helps you build, deploy, and manage device software. Customers use AWS IoT Greengrass for their IoT applications on millions of devices in homes, factories, vehicles, and businesses. You can program your devices to act locally on the data they generate, execute predictions based on machine learning models, filter and aggregate device data, and only transmit necessary information to the cloud.<br/>Recipe README
<center>![Image of SDK Icon](images/Arch_AWS-Tools-and-SDKs_64.png)</br>AWS SDK for Python</center>|The AWS SDK for Python provides the python libraries you can use to interact with AWS Cloud. Botocore and Boto3 are available.






**IMPORTANT NOTE**: Automotive Grade Linux: The AGL distribution uses a specific static ID process. When adding AWS IoT Greengrass, you will need to define users in the passwd and group files manually. Please see https://github.com/aws/meta-aws/issues/75 for more information.

© 2019-2021, Amazon Web Services, Inc. or its affiliates. All rights reserved.
