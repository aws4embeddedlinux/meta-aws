# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: MIT-0

# nooelint: oelint.func.specific
do_install:append:aws-ec2() {
    # Hardening settings to enforce key-based ssh and disable root logins.
    sed -i -e 's:#PermitRootLogin prohibit-password:PermitRootLogin no:' ${D}${sysconfdir}/ssh/sshd_config
    sed -i -e 's:#PermitEmptyPasswords.*:PermitEmptyPasswords no:' ${D}${sysconfdir}/ssh/sshd_config
    sed -i -e 's:#PasswordAuthentication yes:PasswordAuthentication no:' ${D}${sysconfdir}/ssh/sshd_config
}

