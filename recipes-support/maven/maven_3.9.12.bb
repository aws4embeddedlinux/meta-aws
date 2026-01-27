SUMMARY = "Apache Maven - Software project management and comprehension tool"
DESCRIPTION = "Apache Maven is a software project management and comprehension tool. Based on the concept of a Project Object Model (POM), Maven can manage a project's build, reporting and documentation from a central piece of information."
HOMEPAGE = "http://maven.apache.org"
SECTION = "devel"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=d8588a64ef8c5eb9c3545dd9114be451"

SRC_URI = "\
    https://archive.apache.org/dist/maven/maven-3/${PV}/binaries/apache-maven-${PV}-bin.tar.gz \
    file://settings.xml \
"

SRC_URI[sha256sum] = "fa2c9948729296c23afd18fd01a90f62cdda09a46191b54a8bc3764c2eee812e"
SRC_URI[sha1sum] = "3bdcdd002f5453b9d5bb417ee0d4d22a545c34b2"
SRC_URI[sha384sum] = "06eb477f2176b2ee41bfbcaab959e0b486ef2e39503a7ccddb194387709c6305bd22a83b0a1524e4187588004b6cb733"
SRC_URI[sha512sum] = "0a1be79f02466533fc1a80abbef8796e4f737c46c6574ede5658b110899942a94db634477dfd3745501c80aef9aac0d4f841d38574373f7e2d24cce89d694f70"

PR = "r0"

PACKAGES = "${PN}"

COMPATIBLE_MACHINE:riscv64 = "null"

S = "${UNPACKDIR}/apache-maven-${PV}"

do_configure() {
    # Install custom settings
    cp ${UNPACKDIR}/settings.xml ${S}/conf/settings.xml
}

python do_install () {
    bb.build.exec_func("shell_do_install", d)
    oe.path.make_relative_symlink(d.expand("${D}${bindir}/mvn"))
}

shell_do_install() {
    install -d ${D}${libdir}/${BPN}/bin
    install -m 0755 ${S}/bin/mvn ${D}${libdir}/${BPN}/bin/
    install -m 0755 ${S}/bin/mvnDebug ${D}${libdir}/${BPN}/bin/
    install -m 0755 ${S}/bin/mvnyjp ${D}${libdir}/${BPN}/bin/
    install -m 0644 ${S}/bin/m2.conf ${D}${libdir}/${BPN}/bin/

    install -d ${D}${libdir}/${BPN}/boot
    install -m 0644 ${S}/boot/* ${D}${libdir}/${BPN}/boot/

    install -d ${D}${libdir}/${BPN}/conf
    install -d ${D}${libdir}/${BPN}/conf/logging
    install -m 0644 ${S}/conf/*.xml ${D}${libdir}/${BPN}/conf/
    install -m 0644 ${S}/conf/logging/* ${D}${libdir}/${BPN}/conf/logging/

    install -d ${D}${libdir}/${BPN}/lib
    install -m 0644 ${S}/lib/*.jar ${D}${libdir}/${BPN}/lib/
    install -m 0644 ${S}/lib/*.license ${D}${libdir}/${BPN}/lib/

    install -d ${D}${bindir}
    ln -sf ${D}${libdir}/${BPN}/bin/mvn ${D}${bindir}/mvn
}

FILES:${PN} = "\
    ${libdir} \
    ${bindir} \
"

BBCLASSEXTEND = "native nativesdk"

inherit ptest

RDEPENDS:${PN}-ptest += "java-11"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}
    echo "#!/bin/sh" > ${D}${PTEST_PATH}/run-ptest
    echo "mvn --version > /dev/null 2>&1 && echo 'PASS: mvn version' || echo 'FAIL: mvn version'" >> ${D}${PTEST_PATH}/run-ptest
    chmod +x ${D}${PTEST_PATH}/run-ptest
}
