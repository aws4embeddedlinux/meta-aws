SUMMARY     = "AWS IoT Greengrass Nucleus - Binary Distribution - demo mode"
DESCRIPTION = ""
LICENSE     = "Apache-2"

S                          = "${WORKDIR}"
GG_BASENAME                = "greengrass/v2"
GG_ROOT                    = "${D}/${GG_BASENAME}"
LIC_FILES_CHKSUM           = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"
SRC_URI                    = "https://raw.githubusercontent.com/aws-greengrass/aws-greengrass-nucleus/main/LICENSE;name=license; \
                              file://greengrassv2-init.yaml \
                              file://demo.pkey.pem          \
                              file://demo.cert.pem          \
                              file://demo.root.pem          \
                              "
SRC_URI[license.md5sum]    = "34400b68072d710fecd0a2940a0d1658"
SRC_URI[license.sha256sum] = "09e8a9bcec8067104652c168685ab0931e7868f9c8284b66f5ae6edae5f1130b"

RDEPENDS_${PN} += "greengrass-bin"

do_configure[noexec] = "1"
do_compile[noexec]   = "1"

do_install() {
    install -d ${GG_ROOT}/auth
    install -d ${GG_ROOT}/config
    install -m 0440 ${WORKDIR}/demo.pkey.pem ${GG_ROOT}/auth
    install -m 0440 ${WORKDIR}/demo.cert.pem ${GG_ROOT}/auth
    install -m 0440 ${WORKDIR}/demo.root.pem ${GG_ROOT}/auth

    install -m 0640 ${WORKDIR}/greengrassv2-init.yaml ${GG_ROOT}/config/config.yaml

    sed -i -e "s,##private_key##,/${GG_BASENAME}/auth/demo.pkey.pem,g" ${GG_ROOT}/config/config.yaml
    sed -i -e "s,##certificate_path##,/${GG_BASENAME}/auth/demo.cert.pem,g" ${GG_ROOT}/config/config.yaml
    sed -i -e "s,##root_ca##,/${GG_BASENAME}/auth/demo.root.pem,g" ${GG_ROOT}/config/config.yaml
    sed -i -e "s,##thing_name##,${GGV2_THING_NAME},g" ${GG_ROOT}/config/config.yaml
    sed -i -e "s,##aws_region##,${GGV2_REGION},g" ${GG_ROOT}/config/config.yaml
    sed -i -e "s,##role_alias##,${GGV2_TES_RALIAS},g" ${GG_ROOT}/config/config.yaml
    sed -i -e "s,##posixUser##,ggc_user:ggc_group,g" ${GG_ROOT}/config/config.yaml
    sed -i -e "s,##iot_cred_endpoint##,${GGV2_CRED_EP},g" ${GG_ROOT}/config/config.yaml
    sed -i -e "s,##iot_data_endpoint##,${GGV2_DATA_EP},g" ${GG_ROOT}/config/config.yaml
}

FILES = ""
FILES_${PN} = "/${GG_BASENAME}"

INSANE_SKIP_${PN} += "already-stripped ldflags file-rdeps"
