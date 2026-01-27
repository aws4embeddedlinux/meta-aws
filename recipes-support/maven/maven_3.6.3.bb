SUMMARY = "Apache Maven - Software project management and comprehension tool"
DESCRIPTION = "Apache Maven is a software project management and comprehension tool. Based on the concept of a Project Object Model (POM), Maven can manage a project's build, reporting and documentation from a central piece of information."
HOMEPAGE = "http://maven.apache.org"
SECTION = "devel"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=47b7ce43312b5e1e13a8edf5c31245ad"

SRC_URI = "\
    https://archive.apache.org/dist/maven/maven-3/${PV}/binaries/apache-maven-${PV}-bin.tar.gz \
    file://settings.xml \
"

SRC_URI[sha256sum] = "26ad91d751b3a9a53087aefa743f4e16a17741d3915b219cf74112bf87a438c5"
SRC_URI[sha1sum] = "cc836dc7e64b113472df31996caaedf132969009"
SRC_URI[sha384sum] = "b5a8b74eefcfee6e5b450526143fd562802d37bcedef1fbecc61eb30835329cb52c78d2550a4be14e8dfef04a1c450bb"
SRC_URI[sha512sum] = "c35a1803a6e70a126e80b2b3ae33eed961f83ed74d18fcd16909b2d44d7dada3203f1ffe726c17ef8dcca2dcaa9fca676987befeadc9b9f759967a8cb77181c0"

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

do_install_ptest() {
    install -d ${D}${PTEST_PATH}
    echo "#!/bin/sh" > ${D}${PTEST_PATH}/run-ptest
    echo "mvn --version > /dev/null 2>&1 && echo 'PASS: mvn version' || echo 'FAIL: mvn version'" >> ${D}${PTEST_PATH}/run-ptest
    chmod +x ${D}${PTEST_PATH}/run-ptest
}
