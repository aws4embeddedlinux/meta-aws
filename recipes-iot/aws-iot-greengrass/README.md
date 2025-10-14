# AWS IoT Greengrass

## AWS IoT Greengrass Lite Runtime

AWS IoT Greengrass Lite is an optimized runtime option designed for constrained devices. Written in C for improved performance and reduced footprint.

### Component Compatibility

> [!NOTE]
> At launch, Greengrass Lite Runtime supports a subset of Greengrass v2 components, with plans for eventual
> feature parity in subsequent releases.
> See more details [here](https://github.com/aws-greengrass/aws-greengrass-lite).

### Image-Provided Component Deployment

AWS IoT Greengrass Lite now supports **automatic deployment of components built into the device image**, enabling embedded systems to have components ready immediately upon boot without requiring cloud connectivity.

#### Key Features

* **Zero-Copy Deployment**: Components are placed directly in the Greengrass packages directory during image build, eliminating file copying overhead during deployment
* **Automatic Service Management**: Components are automatically deployed after fleet provisioning completes and the service disables itself to prevent re-runs
* **Dual-Mode Support**: Configurable between zero-copy (performance optimized) and traditional (compatibility) deployment modes
* **PACKAGECONFIG Integration**: Optional feature that can be enabled/disabled via `localdeployment` PACKAGECONFIG option

#### How It Works

1. **Build Time**: Components using `greengrass_lite_component.bbclass` are installed directly into `/var/lib/greengrass/packages/`
2. **Boot Time**: The `ggl.local-deployment.service` automatically deploys image-provided components after fleet provisioning
3. **Runtime**: Components start immediately and are managed by Greengrass Lite like any other component

#### Usage

To enable image-provided component deployment:

```bitbake
# In local.conf - enable the feature
PACKAGECONFIG:append:pn-greengrass-lite = " localdeployment"
```

To create an image-provided component:

```bitbake
# In your component recipe
inherit greengrass_lite_component

COMPONENT_NAME = "com.example.MyComponent"
COMPONENT_VERSION = "1.0.0"
COMPONENT_ARTIFACTS = "my_script.py"
GREENGRASS_VARIANT = "lite"

# Optional: Use traditional mode instead of zero-copy
# GREENGRASS_LITE_ZERO_COPY = "0"
```

To use traditional deployment mode (for updates/separate partitions):

```bitbake
# In local.conf or component recipe
GREENGRASS_LITE_ZERO_COPY:pn-your-component = "0"
```

#### Benefits for Embedded Systems

* **Faster Boot**: Components available immediately without cloud dependency
* **Offline Operation**: Components work even without internet connectivity
* **Reduced Storage**: Zero-copy mode eliminates duplicate file storage
* **Better Performance**: No file copying overhead during deployment
* **Predictable Behavior**: Components always deploy regardless of network conditions

#### Component Recipe Requirements

Image-provided components must follow Greengrass Lite recipe format with case-sensitive lifecycle phases:

```yaml
---
RecipeFormatVersion: '2020-01-25'
ComponentName: 'com.example.MyComponent'
ComponentVersion: '1.0.0'
ComponentType: 'aws.greengrass.generic'
Manifests:
  - Platform:
      os: 'linux'
      runtime: "*"
    Lifecycle:
      run: |  # Note: lowercase 'run'
        python3 {artifacts:path}/my_script.py
    Artifacts:
      - URI: "my_script.py"
        Unarchive: "NONE"
```

> [!IMPORTANT]
> Lifecycle phases in Greengrass Lite are case-sensitive. Use lowercase (`run:`, `startup:`, `bootstrap:`) not uppercase (`Run:`, `Startup:`, `Bootstrap:`).

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

### Add systemd

Greengrass v2 runs more elegantly using systemd.  Add this to your
local.conf or distribution to add support.

```bash
DISTRO_FEATURES += "systemd"
DISTRO_FEATURES_BACKFILL_CONSIDERED = "sysvinit"
VIRTUAL-RUNTIME_init_manager = "systemd"
VIRTUAL-RUNTIME_initscripts = ""
```

## Using Greengrass Fleet Provisioning

When enabling Fleet provisioning `PACKAGECONFIG:pn-greengrass-bin = "fleetprovisioning"`, you need to provide claim certificates and configuration. You can do this in two ways:

### Option 1: Using External Certificate Paths (Recommended)

Set the certificate paths and configuration in your local.conf, similar to how greengrass-lite works:

```bash
# Fleet provisioning configuration
IOT_DATA_ENDPOINT:pn-greengrass-plugin-fleetprovisioning = "your-iot-data-endpoint.iot.region.amazonaws.com"
IOT_CRED_ENDPOINT:pn-greengrass-plugin-fleetprovisioning = "your-credentials-endpoint.credentials.iot.region.amazonaws.com"
FLEET_PROVISIONING_TEMPLATE:pn-greengrass-plugin-fleetprovisioning = "YourFleetProvisioningTemplate"
IOT_ROLE_ALIAS:pn-greengrass-plugin-fleetprovisioning = "YourTokenExchangeRoleAlias"
AWS_REGION:pn-greengrass-plugin-fleetprovisioning = "us-west-2"

# Fleet provisioning certificate paths
CLAIM_CERT_PATH:pn-greengrass-plugin-fleetprovisioning = "/path/to/your/claim.cert.pem"
CLAIM_KEY_PATH:pn-greengrass-plugin-fleetprovisioning = "/path/to/your/claim.key.pem"
ROOT_CA_PATH:pn-greengrass-plugin-fleetprovisioning = "/path/to/your/AmazonRootCA1.pem"
```

### Option 2: Manual Installation

If you don't set the certificate paths, the recipe will create the certificate directory structure and warn you to manually provide the certificates at runtime in the target device at:
```
/greengrass/v2/claim-certs/claim.cert.pem
/greengrass/v2/claim-certs/claim.pkey.pem
/greengrass/v2/claim-certs/claim.root.pem
```

### Additional Configuration

Make sure to provide the configuration through the variables in local.conf:

```bash
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
```bash
GGV2_FLEET_PROVISIONING_TEMPLATE_NAME = ""
```