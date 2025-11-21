# gg-sdk is provided by aws-greengrass-component-sdk package as a local crate
# We need to vendor its build dependencies

SRC_URI += "\
    crate://crates.io/cc/1.2.44 \
    crate://crates.io/find-msvc-tools/0.1.4 \
    crate://crates.io/shlex/1.3.0 \
"

SRC_URI[cc-1.2.44.sha256sum] = "37521ac7aabe3d13122dc382493e20c9416f299d2ccd5b3a5340a2570cdeb0f3"
SRC_URI[find-msvc-tools-0.1.4.sha256sum] = "52051878f80a721bb68ebfbc930e07b65ba72f2da88968ea5c06fd6ca3d3a127"
SRC_URI[shlex-1.3.0.sha256sum] = "0fda2ff0d084019ba4d7c6f371c95d8fd75ce3524c3cb8fb653a3023f6323e64"
