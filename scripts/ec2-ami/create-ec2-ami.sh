#!/usr/bin/env bash
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: MIT-0
set -e
[ "$DEBUG" == 'true' ] && set -x

ARGC=$#
if [ $ARGC -lt 3 ]; then
    echo "ERROR: Please inform import bucket name as first argument and AMI disk size in GB as second, IMAGE_NAME as third and MACHINE_NAME as last."
    echo "E.g.:"
    echo "$0 my-test-bucket 4 core-image-minimal aws-ec2-arm64"
    exit 1
fi
IMPORT_BUCKET_NAME=$1
AMI_DISK_SIZE_GB=$2
IMAGE_NAME=$3
MACHINE_NAME=$4

# validation steps
# AMI_DISK_SIZE_GB
re='^[0-9]+$'
if ! [[ $AMI_DISK_SIZE_GB =~ $re ]] ; then
    echo "AMI_DISK_SIZE needs to be a number only without unit. E.g. '4'." >&2
    exit 1
fi

IMG_DIR=$(bitbake-getvar --value -q  DEPLOY_DIR_IMAGE)

TESTDATA_JSON="${IMG_DIR}/${IMAGE_NAME}-${MACHINE_NAME}.rootfs.testdata.json"

DISTRO=$(jq -r '.DISTRO' $TESTDATA_JSON)
DISTRO_CODENAME=$(jq -r '.DISTRO_CODENAME' $TESTDATA_JSON)
DISTRO_NAME=$(jq -r '.DISTRO_NAME' $TESTDATA_JSON)
DISTRO_VERSION=$(jq -r '.DISTRO_VERSION' $TESTDATA_JSON)
BUILDNAME=$(jq -r '.BUILDNAME' $TESTDATA_JSON)
TARGET_ARCH=$(jq -r '.TARGET_ARCH' $TESTDATA_JSON)
IMAGE_NAME=$(jq -r '.IMAGE_NAME' $TESTDATA_JSON)
IMAGE_ROOTFS_SIZE=$(jq -r '.IMAGE_ROOTFS_SIZE' $TESTDATA_JSON)

echo IMG_DIR=$IMG_DIR
echo DISTRO=$DISTRO
echo DISTRO_CODENAME=$DISTRO_CODENAME
echo DISTRO_NAME=$DISTRO_NAME
echo DISTRO_VERSION=$DISTRO_VERSION
echo BUILDNAME=$BUILDNAME
echo TARGET_ARCH=$TARGET_ARCH
echo IMAGE_ROOTFS_SIZE=$IMAGE_ROOTFS_SIZE
echo AMI_DISK_SIZE_GB=$AMI_DISK_SIZE_GB

echo "Pushing image ${IMAGE_NAME}.wic.vhd to s3://${IMPORT_BUCKET_NAME}"
aws s3 cp ${IMG_DIR}/${IMAGE_NAME}.wic.vhd s3://${IMPORT_BUCKET_NAME}

cat <<EOF > image-import.json
{
    "Description": "ewaol docker image",
    "Format": "vhd",
    "UserBucket": {
        "S3Bucket": "${IMPORT_BUCKET_NAME}",
        "S3Key": "${IMAGE_NAME}.wic.vhd"
    }
}
EOF
echo "Importing image file into snapshot "
IMPORT_TASK_ID=$(aws ec2 import-snapshot --disk-container "file://image-import.json" | jq -r '.ImportTaskId')

IMPORT_STATUS=$(aws ec2 describe-import-snapshot-tasks --import-task-ids $IMPORT_TASK_ID | jq -r '.ImportSnapshotTasks[].SnapshotTaskDetail.Status')
x=0
rm image-import.json
while [ "$IMPORT_STATUS" = "active" ] && [ $x -lt 120 ]
do
  IMPORT_STATUS=$(aws ec2 describe-import-snapshot-tasks --import-task-ids $IMPORT_TASK_ID | jq -r '.ImportSnapshotTasks[].SnapshotTaskDetail.Status')
  IMPORT_STATUS_MSG=$(aws ec2 describe-import-snapshot-tasks --import-task-ids $IMPORT_TASK_ID | jq -r '.ImportSnapshotTasks[].SnapshotTaskDetail.StatusMessage')
  echo "Import Status: ${IMPORT_STATUS} / ${IMPORT_STATUS_MSG}"
  x=$(( $x + 1 ))
  sleep 15
done
if [ $x -eq 120 ]; then
    echo "ERROR: Import task taking too long, exiting..."; exit 1;
elif [ "$IMPORT_STATUS" = "completed" ]; then
    echo "Import completed Successfully"
else
    echo "Import Failed, exiting"; exit 2;
fi

SNAPSHOT_ID=$(aws ec2 describe-import-snapshot-tasks --import-task-ids $IMPORT_TASK_ID | jq -r '.ImportSnapshotTasks[].SnapshotTaskDetail.SnapshotId')

aws ec2 wait snapshot-completed --snapshot-ids $SNAPSHOT_ID

if [[ "$TARGET_ARCH" == "x86_64" ]]; then
    ARCHITECTURE="x86_64"
elif [[ "$TARGET_ARCH" == "aarch64" ]]; then
    ARCHITECTURE="arm64"
else
    echo "Architecture not supported"
    exit 1
fi
DESCRIPTION=$(echo "DISTRO=$DISTRO;DISTRO_CODENAME=$DISTRO_CODENAME;DISTRO_NAME=$DISTRO_NAME;DISTRO_VERSION=$DISTRO_VERSION;BUILDNAME=$BUILDNAME;TARGET_ARCH=$ARCHITECTURE;IMAGE_NAME=$IMAGE_NAME" | cut -c -255)

cat <<EOF > register-ami.json
{
    "Architecture": "$ARCHITECTURE",
    "BlockDeviceMappings": [
        {
            "DeviceName": "/dev/sda1",
            "Ebs": {
                "DeleteOnTermination": true,
                "SnapshotId": "$SNAPSHOT_ID",
                "VolumeSize": ${AMI_DISK_SIZE_GB},
                "VolumeType": "gp2"
            }
        }
    ],
    "Description": "$DESCRIPTION",
    "RootDeviceName": "/dev/sda1",
    "BootMode": "uefi",
    "VirtualizationType": "hvm",
    "EnaSupport": true
}
EOF

AMI_NAME=$(echo "${IMAGE_NAME}-${DISTRO}-${DISTRO_CODENAME}-${DISTRO_VERSION}-${BUILDNAME}-${ARCHITECTURE}" | cut -c -128 | sed -e s/+/-/g)
IMAGE_ID=$(aws ec2 describe-images --filters Name=name,Values=${AMI_NAME} | jq -r '.Images[].ImageId')
if [ "$IMAGE_ID" != "" ]; then
    echo "Deregistering existing image $IMAGE_ID"
    aws ec2 deregister-image --image-id ${IMAGE_ID} 2>&1 > /dev/null
fi
echo "Registering AMI with Snapshot $SNAPSHOT_ID"
AMI_ID=$(aws ec2 register-image --name ${AMI_NAME} --cli-input-json="file://register-ami.json" | jq -r '.ImageId')
echo "AMI name: $AMI_NAME"
echo "AMI ID: $AMI_ID"
rm register-ami.json
