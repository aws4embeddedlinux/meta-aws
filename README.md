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
$ git clone git://git.yoctoproject.org/poky ~/poky-zeus
$ cd ~/poky-zeus
$ git fetch --tags
$ git checkout tags/yocto-3.0.1
```

Next, check out the `openembedded` distribution for the **zeus** release.

```bash
$ git clone git://git.openembedded.org/meta-openembedded \
      ~/poky-zeus/meta-openembedded

$ cd ~/poky-zeus/meta-openembedded

$ git fetch --tags
$ git checkout zeus
```

Next, checkout `meta-aws` to the **zeus** branch.

```bash
$ git clone https://github.com/aws/meta-aws ~/poky-zeus/meta-aws
$ pushd ~/poky-zeus/meta-aws
$ git fetch --tags
$ git checkout zeus
```

## Configure build

Initialize the build environment. This will create a build directory named **zeus**.

```bash
$ cd ~/poky-zeus
$ source poky/oe-init-build-env zeus
```

Next, add layers to the build.

```bash
$ cd ~/poky-zeus/zeus
$ bitbake-layers add-layer $BASEDIR/meta-openembedded/meta-oe
$ bitbake-layers add-layer $BASEDIR/meta-openembedded/meta-python
$ bitbake-layers add-layer $BASEDIR/meta-openembedded/meta-networking
$ bitbake-layers add-layer $BASEDIR/meta-aws
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
$ MACHINE=qemux86-64 bitbake core-image-minimal
```

You can then test with QEMU.  For example, if you want to run the image with ext4 and 2GB memory:

```bash
runqemu qemux86-64 core-image-minimal ext4 qemuparams="-m 2048"
```

For more information please refer to https://www.yoctoproject.org/docs/1.8/dev-manual/dev-manual.html

© 2019, Amazon Web Services, Inc. or its affiliates. All rights reserved.
