# meta-aws

The **meta-aws** project provides *recipes* for building in AWS edge software capabilities to [Embedded Linux](https://elinux.org) built with [OpenEmbedded](https://www.openembedded.org) and [Yocto Project](https://www.yoctoproject.org/) build frameworks.

This layer depends on the additional mandatory layers:

- meta-yocto-bsp
- meta-openembedded/meta-oe
- meta-openembedded/meta-filesystems
- meta-openembedded/meta-python

Please make sure you have them in your Yocto Project.

# Getting Started

The following tutorial is useful to start building your own Yocto project. The development machine should be running Ubuntu 16.04 and above.

More information on how to get started can be found in the original Yocto documentation: https://www.yoctoproject.org/docs/2.4/yocto-project-qs/yocto-project-qs.html#yp-resources

## Install System Dependencies

    sudo apt-get update && sudo apt-get upgrade
    sudo apt-get install gawk wget git-core diffstat unzip texinfo gcc-multilib build-essential chrpath socat libsdl1.2-dev xterm lzop u-boot-tools git build-essential curl libusb-1.0-0-dev python-pip minicom libncurses5-dev
    sudo pip install --upgrade pip && sudo pip install pyserial

Additional packages might be needed depending on the intended image build

## Get sources

Clone sources:

    git clone --branch fido git://git.yoctoproject.org/poky.git ~/yocto/poky
    git clone --branch fido git://git.openembedded.org/meta-openembedded ~/yocto/meta-openembedded

## Configure build

Setup environment:

    cd ~/yocto
    source poky/oe-init-build-env

Set machine in the configuration file `~/yocto/build/conf/local.conf`:

    MACHINE ??= <target_machine>

Or simply export the variable globally:

    export MACHINE = <target_machine>

# Adding the Layer and Specific Recipes

Adding the meta-aws layer to the project can be done via the command

    bitbake-layers add-layer meta-aws

or simply editing the `bblayers.conf` file `~/yocto/build/conf` directory

## Adding Recipes to Your Image

Once the setup is done the recipes provided in the `meta-aws` will be available and can be added to the target image by adding:

    IMAGE_INSTALL += "<name-of-the-bb-recipe>"

after which the image can be built with the command:

    bitbake <image-name>

For more information please refer to https://www.yoctoproject.org/docs/1.8/dev-manual/dev-manual.html


© 2019, Amazon Web Services, Inc. or its affiliates. All rights reserved.
