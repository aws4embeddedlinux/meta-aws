# Auto-Generated by cargo-bitbake 0.3.16-alpha.0
#
inherit cargo

# If this is git based prefer versioned ones if they exist
# DEFAULT_PREFERENCE = "-1"

# how to get jailer could be as easy as but default to a git checkout:
# SRC_URI += "crate://crates.io/jailer/0.24.3"
SRC_URI += "git://github.com/firecracker-microvm/firecracker.git;protocol=https;nobranch=1"
SRCREV = "9f447fe65c1c549ef7b0abe863428fa33ffe5f79"
S = "${WORKDIR}/git"
CARGO_SRC_DIR = "src/jailer"


# please note if you have entries that do not begin with crate://
# you must change them to how that package can be fetched
SRC_URI += " \
    crate://crates.io/aho-corasick/0.7.15 \
    crate://crates.io/atty/0.2.14 \
    crate://crates.io/autocfg/1.0.1 \
    crate://crates.io/bincode/1.3.1 \
    crate://crates.io/bitflags/1.2.1 \
    crate://crates.io/bstr/0.2.14 \
    crate://crates.io/bumpalo/3.4.0 \
    crate://crates.io/byteorder/1.3.4 \
    crate://crates.io/cast/0.2.3 \
    crate://crates.io/cfg-if/0.1.10 \
    crate://crates.io/cfg-if/1.0.0 \
    crate://crates.io/clap/2.33.3 \
    crate://crates.io/const_fn/0.4.3 \
    crate://crates.io/crc64/1.0.0 \
    crate://crates.io/criterion-plot/0.4.3 \
    crate://crates.io/criterion/0.3.3 \
    crate://crates.io/crossbeam-channel/0.5.0 \
    crate://crates.io/crossbeam-deque/0.8.0 \
    crate://crates.io/crossbeam-epoch/0.9.0 \
    crate://crates.io/crossbeam-utils/0.8.0 \
    crate://crates.io/csv-core/0.1.10 \
    crate://crates.io/csv/1.1.4 \
    crate://crates.io/device_tree/1.1.0 \
    crate://crates.io/either/1.6.1 \
    crate://crates.io/errno-dragonfly/0.1.1 \
    crate://crates.io/errno/0.2.7 \
    crate://crates.io/gcc/0.3.55 \
    crate://crates.io/half/1.6.0 \
    crate://crates.io/hermit-abi/0.1.17 \
    crate://crates.io/itertools/0.9.0 \
    crate://crates.io/itoa/0.4.6 \
    crate://crates.io/js-sys/0.3.45 \
    crate://crates.io/kernel32-sys/0.2.2 \
    crate://crates.io/kvm-ioctls/0.6.0 \
    crate://crates.io/lazy_static/1.4.0 \
    crate://crates.io/libc/0.2.80 \
    crate://crates.io/log/0.4.11 \
    crate://crates.io/memchr/2.3.4 \
    crate://crates.io/memoffset/0.5.6 \
    crate://crates.io/num-traits/0.2.14 \
    crate://crates.io/num_cpus/1.13.0 \
    crate://crates.io/oorandom/11.1.3 \
    crate://crates.io/plotters/0.2.15 \
    crate://crates.io/proc-macro2/1.0.24 \
    crate://crates.io/quote/1.0.7 \
    crate://crates.io/rayon-core/1.9.0 \
    crate://crates.io/rayon/1.5.0 \
    crate://crates.io/regex-automata/0.1.9 \
    crate://crates.io/regex-syntax/0.6.21 \
    crate://crates.io/regex/1.4.2 \
    crate://crates.io/rustc_version/0.2.3 \
    crate://crates.io/ryu/1.0.5 \
    crate://crates.io/same-file/1.0.6 \
    crate://crates.io/scopeguard/1.1.0 \
    crate://crates.io/semver-parser/0.7.0 \
    crate://crates.io/semver/0.9.0 \
    crate://crates.io/serde/1.0.117 \
    crate://crates.io/serde_cbor/0.11.1 \
    crate://crates.io/serde_derive/1.0.117 \
    crate://crates.io/serde_json/1.0.59 \
    crate://crates.io/syn/1.0.50 \
    crate://crates.io/sysconf/0.3.4 \
    crate://crates.io/textwrap/0.11.0 \
    crate://crates.io/thread_local/1.0.1 \
    crate://crates.io/timerfd/1.1.1 \
    crate://crates.io/tinytemplate/1.1.0 \
    crate://crates.io/unicode-width/0.1.8 \
    crate://crates.io/unicode-xid/0.2.1 \
    crate://crates.io/versionize/0.1.4 \
    crate://crates.io/versionize_derive/0.1.3 \
    crate://crates.io/vm-memory/0.4.0 \
    crate://crates.io/vmm-sys-util/0.7.0 \
    crate://crates.io/walkdir/2.3.1 \
    crate://crates.io/wasm-bindgen-backend/0.2.68 \
    crate://crates.io/wasm-bindgen-macro-support/0.2.68 \
    crate://crates.io/wasm-bindgen-macro/0.2.68 \
    crate://crates.io/wasm-bindgen-shared/0.2.68 \
    crate://crates.io/wasm-bindgen/0.2.68 \
    crate://crates.io/web-sys/0.3.45 \
    crate://crates.io/winapi-build/0.1.1 \
    crate://crates.io/winapi-i686-pc-windows-gnu/0.4.0 \
    crate://crates.io/winapi-util/0.1.5 \
    crate://crates.io/winapi-x86_64-pc-windows-gnu/0.4.0 \
    crate://crates.io/winapi/0.2.8 \
    crate://crates.io/winapi/0.3.9 \
    git://github.com/firecracker-microvm/kvm-bindings;protocol=https;nobranch=1;name=kvm-bindings;destsuffix=kvm-bindings \
"

SRCREV_FORMAT .= "_kvm-bindings"
SRCREV_kvm-bindings = "640ea4fd7c9fa3bb6317ce73a68f5792c9f1feef"
EXTRA_OECARGO_PATHS += "${WORKDIR}/kvm-bindings"

LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
"

SUMMARY = "Firecracker enables you to deploy workloads in lightweight virtual machines, called microVMs, which provide enhanced security and workload isolation over traditional VMs, while enabling the speed and resource efficiency of containers."
HOMEPAGE = "https://firecracker-microvm.github.io/"
LICENSE = "Apache-2.0"

# includes this file if it exists but does not fail
# this is useful for anything you may want to override from
# what cargo-bitbake generates.
include jailer-${PV}.inc
include jailer.inc
