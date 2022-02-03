# -*- mode: Conf; -*-
SUMMARY = "JSMN"
DESCRIPTION = "jsmn (pronounced like 'jasmine') is a minimalistic JSON parser in C. It can be easily integrated into resource-limited or embedded projects."
HOMEPAGE = "https://github.com/zserge/jsmn"
LICENSE = "MIT-0"
PROVIDES += "jsmn"

BRANCH ?= "master"
SDIR ?= "amazon-kvs-producer-sdk-c"

LIC_FILES_CHKSUM = "file://${SDIR}/LICENSE;md5=5adc94605a1f7a797a9a834adbe335e3"

SRC_URI = "git://github.com/zserge/jsmn.git;protocol=https;branch=${BRANCH};destsuffix=${S}/${SDIR}"
SRCREV = "fdcef3ebf886fa210d14956d3c068a653e76a24e"

S = "${WORKDIR}/git"

FILES:${PN}     = ""
FILES:${PN}-dev = "jsmn.h"
FILES:${PN}-dbg = ""

# Notify that libraries are not versioned
FILES_SOLIBSDEV = ""

BBCLASSEXTEND = "native nativesdk"
