# meta-aws

The **meta-aws** project provides *recipes* for building in AWS edge software capabilities to [Embedded Linux](https://elinux.org) built with [OpenEmbedded](https://www.openembedded.org) and [Yocto Project](https://www.yoctoproject.org/) build frameworks.

Please check out [our sister project meta-aws-demos](https://github.com/aws-samples/meta-aws-demos)!  Over time, we will continuously be adding MACHINE specific demonstrations for AWS software on Embedded Linux built by the Yocto Project build framework with the meta-aws Metadata Layer.

# Getting Started

The following tutorial is useful to start building your own Yocto project. The development machine should be running Ubuntu 16.04 and above.

More information on how to get started can be found in the original Yocto documentation: https://www.yoctoproject.org/docs/2.4/yocto-project-qs/yocto-project-qs.html#yp-resources

## Install System Dependencies

```bash
$ sudo apt-get update && sudo apt-get upgrade
$ sudo apt-get install gawk wget git-core diffstat unzip texinfo gcc-multilib \
       build-essential chrpath socat cpio python python3 python3-pip python3-pexpect \
       xz-utils debianutils iputils-ping python3-git python3-jinja2 libegl1-mesa \
       libsdl1.2-dev pylint3 xterm
```

Additional packages might be needed depending on the intended image build

## Get sources

In this section, you will checkout the Poky reference implementation, meta-aws, and dependent source trees.  When you build for specific `MACHINE` targets, you will need to checkout additional sources.

In this case, we will be checking out sources for the **zeus** release.

```bash
git clone -b zeus git://git.yoctoproject.org/poky ~/poky-zeus
cd ~/poky-zeus
```

Next, check out the `openembedded` distribution for the **zeus**
release.  **NOTE** This will be updated to dunfell soon.

```bash
git clone -b zeus git://git.openembedded.org/meta-openembedded
git clone -b zeus git://git.yoctoproject.org/meta-virtualization
git clone -b zeus git://git.yoctoproject.org/meta-java
git clone -b zeus git://github.com/aws/meta-aws
```

## Configure build

Initialize the build environment. This will create a build directory named **zeus**.

```bash
source oe-init-build-env
```

Next, modify conf/bblayers.conf to resemble the following.  The reason
for manual editing is the bitbake-layers command gets broken by a
couple of the layers.

```text
# POKY_BBLAYERS_CONF_VERSION is increased each time build/conf/bblayers.conf
# changes incompatibly
POKY_BBLAYERS_CONF_VERSION = "2"

BBPATH = "${TOPDIR}"
BBFILES ?= ""

BBLAYERS ?= " \
  /src/poky-zeus/meta \
  /src/poky-zeus/meta-poky \
  /src/poky-zeus/meta-yocto-bsp \
  /src/poky-zeus/meta-openembedded/meta-oe \
  /src/poky-zeus/meta-openembedded/meta-python \
  /src/poky-zeus/meta-openembedded/meta-filesystems \
  /src/poky-zeus/meta-openembedded/meta-networking \
  /src/poky-zeus/meta-virtualization \
  /src/poky-zeus/meta-aws \
  "
```

Now you are ready to build.

## Adding Recipes to Your Image

Once the setup is done the recipes provided in the `meta-aws` will be available and can be added to the target image by adding the following to the end of the `local.conf`.  Ensure that a leading space is there.

```cfg
IMAGE_INSTALL_append = " <name-of-the-bb-recipe>"
```

For example:

```cfg
IMAGE_INSTALL_append = " greengrass"
```

after which the image can be built with the command:

```bash
bitbake <image-name>
```

for example

```bash
MACHINE=qemux86-64 bitbake core-image-minimal
```

If you want to add support for IoT Greengrass Connectors and/or Stream
Manager, you will need to add Docker and JDK respectively to the build.

You can then test with QEMU.  For example, if you want to run the image with ext4 and 2GB memory:

```bash
runqemu qemux86-64 core-image-minimal ext4 qemuparams="-m 2048"
```

For more information please refer to https://www.yoctoproject.org/docs/1.8/dev-manual/dev-manual.html

© 2019, Amazon Web Services, Inc. or its affiliates. All rights reserved.
