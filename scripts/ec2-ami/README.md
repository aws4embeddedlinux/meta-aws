# The EC2 AMI generation feature
The AWS EC2 AMI feature was taken from [meta-aws-ewaol](https://github.com/aws4embeddedlinux/meta-aws-ewaol). And turned into a more generic solution that will allow you by simple adding those lines to your local.conf to build an AWS EC2 AMI image that can be uploaded with a simple included script to your AWS account.

```
INHERIT += "aws-ec2-image"
MACHINE = "aws-ec2-arm64"
# or MACHINE = "aws-ec2-x86-64"
```

## Build instructions

### Pre-requisites

1. An AWS account and the necessary IAM rights to create EC2 instances, EBS snapshots, EBS volumes, S3 buckets, and IAM roles and policies.
1. A VPC with appropriate subnets and routing required to access the build server.
1. The resources created by deploying the [VMImport CloudFormation Template](vmimport-cfn.yml) (roles, policies, S3 bucket for images).
    1. Take note of the outputs of the stack deployment as they'll be needed in the following steps

### Build Dependencies
To be able to execute the ```create-ec2-ami.sh``` script AWS CLI v2 is necessary.
1. Install AWS CLI v2

    ```bash
    curl "https://awscli.amazonaws.com/awscli-exe-linux-aarch64.zip" -o "/tmp/awscliv2.zip"
    unzip /tmp/awscliv2.zip -d /tmp
    sudo /tmp/aws/install
    ```

### Building 

1. Build your image as usual. For example:

    ```bash
    bitbake core-image-minimal
    ```

### Creating AMI from image file

From meta-aws scripts/ec2-ami directory, run the bash script. Replace <S3_BUCKET_IMPORT_IMAGES> with the bucket name created by the CloudFormation Stack in the pre-requisites section and choose the appropriate size for the future root disk of AMI to have by entering a number (e.g. 16) in place of <AMI_DISK_SIZE_IN_GB>, use the created <IMAGE_NAME> and <MACHINE_NAME> :

```bash
meta-aws/scripts/ec2-ami/create-ec2-ami.sh <S3_BUCKET_IMPORT_IMAGES> <AMI_DISK_SIZE_IN_GB> <IMAGE_NAME> <MACHINE_NAME>
```

e.g.
```bash
meta-aws/scripts/ec2-ami/create-ec2-ami.sh amitest-bucket 16 core-image-minimal aws-ec2-arm64
```
## Launch the EC2 Image as usual using your newly created AMI

1. In the Web Console, Navigate to EC2->Images->AMIs
1. Select the desired AMI and click 'Launch instance from Image'
1. Follow the wizard as usual
1. Access the image with the previously provided ssh key with user **user** or the user specified in [the cloud init configuration](../../dynamic-layers/virtualization-layer/recipes-extended/cloud-init/files/cloud.cfg).

## Limitations

The image does not yet support online expansion of partitions/filesystems via cloud-init.
Follow the below workaround to expand root partition and filesystem (this can be used as user data script):

```bash
#!/bin/sh

# disabling swap
swapoff -a
sed -i '/.*swap.*/d' /etc/fstab
# trick to fix GPT
printf "fix\n" | parted ---pretend-input-tty /dev/nvme0n1 print
# remove partition 3 (swap)
parted -s /dev/nvme0n1 rm 3
# resize partition 2 to use 100% of available free space
parted -s /dev/nvme0n1 resizepart 2 100%
# resizing ext4 filesystem
resize2fs /dev/nvme0n1p2
```

## Future Enchancements

* Enable support for expanding filesystem on boot with cloud-init which depends on growpart. This needs cloud-utils which is not in openembedded recipes yet.
