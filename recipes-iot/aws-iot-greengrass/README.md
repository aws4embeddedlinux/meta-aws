# AWS IoT Greengrass

All new designs should be using AWS IoT Greengrass V2.

See the AWS IoT Greengrass V1 section if you still require V1.

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


## AWS IoT Greengrass V1

### Enabling the V1 version.

AWS IoT Greengrass v2 will be used by default. to use AWS IoT
Greengrass v1, you must override the version number. Configure
local.conf or your distribution file with the preferred version.

```text
PREFERRED_VERSION_greengrass = "1.11.0"
```

### Kernel Dependencies

Greengrass v1 requires specific kernel features which must be opt-in
according the Yocto Project best practices and achieving layer
compatibility.

In your local.conf or distribution configuration, set the following
variable:

```text
GG_KERNEL_MOD = 1
```
