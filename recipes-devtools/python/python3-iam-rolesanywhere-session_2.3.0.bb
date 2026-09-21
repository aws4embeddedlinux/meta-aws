SUMMARY = "Boto3 session creator for IAM Roles Anywhere"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

DEPENDS = "python3-setuptools-scm-native"
SRC_URI += "file://0001-fix-remove-license-classifier.patch"
SRC_URI[sha256sum] = "718a7cb43c191f2801467a53405b829ce878ac58b3cb8868caa5782ecc3f20e5"

inherit pypi python_setuptools_build_meta

PYPI_PACKAGE = "iam_rolesanywhere_session"
RDEPENDS:${PN} = "python3-boto3 python3-botocore python3-cryptography"

BBCLASSEXTEND = "native"
