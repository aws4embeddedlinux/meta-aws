# AWS IoT Greengrass

## AWS IoT Greengrass Lite Runtime

AWS IoT Greengrass Lite is an optimized runtime option designed for constrained devices. Written in C for improved performance and reduced footprint.

### Component Compatibility

> [!NOTE]
> At launch, Greengrass Lite Runtime supports a subset of Greengrass v2 components, with plans for eventual
> feature parity in subsequent releases.
> See more details [here](https://github.com/aws-greengrass/aws-greengrass-lite).

## Greengrass Lite on prplOS

### Integration Features

AWS IoT Greengrass has been integrated with prplWare and RDK Telco stacks using prpl foundation's open source Life Cycle Manager (LCM) containers. This integration provides:

* Simplified application development and deployment within LCM containers
* High-level software abstraction for CPE devices
* Cloud-to-edge integration capabilities
* Flexibility to choose between prplWare and RDK stacks
* Compliance with TR-369 and TR-181 standards

### Key Benefits for Telco Providers

* Quick development and testing of new services
* Remote application deployment
* IoT device lifecycle management
* Managed Wi-Fi capabilities
* Network security management
* Mass telemetry support
* AI/ML capabilities at the edge
* Advanced analytics

### Implementation Support

* Supports Customer Premises Equipment (CPE)
* Compatible with TR369 compliant prplWare
* Works with RDKTelco software stacks
* Operates through prpl foundation's LCM containers
* Maintains native stack communications

> [!NOTE]
> The integration is still a pending PR. See more details about how to get started with Greengrass lite on Prpl OS [here](https://github.com/aws-samples/aws-iot-gg-prplos-workshop/pull/1):

## AWS IoT Greengrass V2

These images are provided for AWS IoT Greengrass V2:
* `greengrass-bin`: This installs Greengrass v2 without a configuration file. This can be used if you plan to add logic to configure Greengrass when the device runs for the first time
* `greengrass-bin-demo`: This installs Greengrass v2 and configures it to run using the provided certificates and configs. Use this if you want the image to be specific to a device, or to get started quickly.

If you want to use a version of greengrass that is not the latest, you can provide `PREFERRED_VERSION_greengrass-bin` and `PREFERRED_VERSION_greengrass-bin-demo` (if used) to use latest.

### Add systemd

Greengrass v2 runs more elegantly using systemd.  Add this to your
local.conf or distribution to add support.

```bash
DISTRO_FEATURES += "systemd"
DISTRO_FEATURES_BACKFILL_CONSIDERED = "sysvinit"
VIRTUAL-RUNTIME_init_manager = "systemd"
VIRTUAL-RUNTIME_initscripts = ""
```

### Configure for demo mode (greengrass-bin-demo)

1. Put this in local.conf and configure

```bash
IMAGE_INSTALL:append = " greengrass-bin-demo"

GGV2_DATA_EP     = ""
GGV2_CRED_EP     = ""
GGV2_REGION      = ""
GGV2_PKEY        = ""
GGV2_CERT        = ""
GGV2_CA          = ""
GGV2_THING_NAME  = ""
GGV2_TES_RALIAS  = ""
```

Add certificate, key, and CA to the files/ directory. Overwrite the
files named:

- demo.pkey.pem : private key in PEM format
- demo.cert.pem : certificate in PEM format
- demo.root.pem : root CA certificate in PEM format

A sample configuration looks like the following.

```bash
GGV2_DATA_EP     = "audqth88umq6e-ats.iot.us-east-1.amazonaws.com"
GGV2_CRED_EP     = "c2uxv58888oq7k.credentials.iot.us-east-1.amazonaws.com"
GGV2_REGION      = "us-west-2"
GGV2_THING_NAME  = "workstation"
GGV2_TES_RALIAS  = "GreengrassV2TokenExchangeRoleAlias"
```

In order to get the information above you can follow the instructions provided here: https://docs.aws.amazon.com/greengrass/v2/developerguide/manual-installation.html

## Using Greengrass Fleet Provisioning

When enabling Fleet provisioning `PACKAGECONFIG:pn-greengrass-bin = "fleetprovisioning"`
it is important to provide claim certificates and place them in the `files` dir:
```
meta-aws
└── recipes-iot
    └── aws-iot-greengrass
        └── files
            └── claim.cert.pem
            └── claim.pkey.pem
            └── claim.root.pem
```
Additionally make sure to provide the configuration through the variables from the local.conf similar to the ones in `greengrass-bin-demo`

```
GGV2_DATA_EP     = ""
GGV2_CRED_EP     = ""
GGV2_REGION      = ""
GGV2_PKEY        = ""
GGV2_CERT        = ""
GGV2_CA          = ""
GGV2_THING_NAME  = ""
GGV2_TES_RALIAS  = ""
```
with the addition of:
```
GGV2_THING_GROUP = ""
in which the devices will be part of after automatic provisioning and
GGV2_FLEET_PROVISIONING_TEMPLATE_NAME = ""

```