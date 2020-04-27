SUMMARY = "Automatically gets credentials for Amazon ECR on docker push/docker pull"
DESCRIPTION = "The Amazon ECR Docker Credential Helper is a credential helper for the Docker daemon that makes it easier to use Amazon Elastic Container Registry."

LICENSE = "Apache-2.0 & MIT & ISC"
LIC_FILES_CHKSUM = " \
    file://src/github.com/awslabs/amazon-ecr-credential-helper/LICENSE;md5=e60493c23a6358c3e44f125a289c0692 \
    file://src/github.com/awslabs/amazon-ecr-credential-helper/THIRD-PARTY-LICENSES;md5=4d94bac1eb7c152647e9cec7a46888d3 \
    file://src/github.com/awslabs/amazon-ecr-credential-helper/ecr-login/vendor/github.com/golang/mock/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://src/github.com/awslabs/amazon-ecr-credential-helper/ecr-login/vendor/github.com/pmezard/go-difflib/LICENSE;md5=e9a2ebb8de779a07500ddecca806145e \
    file://src/github.com/awslabs/amazon-ecr-credential-helper/ecr-login/vendor/github.com/pkg/errors/LICENSE;md5=6fe682a02df52c6653f33bd0f7126b5a \
    file://src/github.com/awslabs/amazon-ecr-credential-helper/ecr-login/vendor/github.com/jmespath/go-jmespath/LICENSE;md5=9abfa8353fce3f2cb28364e1e9016852 \
    file://src/github.com/awslabs/amazon-ecr-credential-helper/ecr-login/vendor/github.com/mitchellh/go-homedir/LICENSE;md5=3f7765c3d4f58e1f84c4313cecf0f5bd \
    file://src/github.com/awslabs/amazon-ecr-credential-helper/ecr-login/vendor/github.com/stretchr/testify/LICENSE;md5=d4c9e9b2abd3afaebed1524a9a77b937 \
    file://src/github.com/awslabs/amazon-ecr-credential-helper/ecr-login/vendor/github.com/sirupsen/logrus/LICENSE;md5=8dadfef729c08ec4e631c4f6fc5d43a0 \
    file://src/github.com/awslabs/amazon-ecr-credential-helper/ecr-login/vendor/github.com/docker/docker-credential-helpers/LICENSE;md5=5f28a5a15a6bde800864f444aee95768 \
    file://src/github.com/awslabs/amazon-ecr-credential-helper/ecr-login/vendor/github.com/konsorten/go-windows-terminal-sequences/LICENSE;md5=0fa4821e00ed8fa049781716357f27ed \
    file://src/github.com/awslabs/amazon-ecr-credential-helper/ecr-login/vendor/github.com/davecgh/go-spew/LICENSE;md5=c06795ed54b2a35ebeeb543cd3a73e56 \
    file://src/github.com/awslabs/amazon-ecr-credential-helper/ecr-login/vendor/github.com/aws/aws-sdk-go/LICENSE.txt;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://src/github.com/awslabs/amazon-ecr-credential-helper/ecr-login/vendor/golang.org/x/sys/LICENSE;md5=5d4950ecb7b26d2c5e4e7b4e0dd74707 \
"

SRC_URI = "git://github.com/awslabs/amazon-ecr-credential-helper.git;protocol=https"

PV = "0.4.0"
SRCREV = "1a5791b236421b509fbc30502211b1de51ca8e30"

GO_IMPORT = "github.com/awslabs/amazon-ecr-credential-helper"

inherit go

GO_INSTALL = " \
    ${GO_IMPORT}/ecr-login/cli/docker-credential-ecr-login \
    ${GO_IMPORT}/ecr-login/api \
    ${GO_IMPORT}/ecr-login/cache \
"

RDEPENDS_${PN} = "bash"
