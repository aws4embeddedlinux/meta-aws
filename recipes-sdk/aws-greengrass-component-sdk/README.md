# aws-greengrass-component-sdk

Yocto/OE recipe for the [AWS Greengrass Component SDK](https://github.com/aws-greengrass/aws-greengrass-component-sdk) — a lightweight library for building AWS IoT Greengrass components in C, C++, and Rust.

## What this builds

- **C static library** (`libgg-sdk.a`) and headers
- **C++ static library** (`libgg-sdk++.a`) and headers
- **Rust rlib** (`libgg_sdk.rlib`) for Rust components
- **Sample binaries** demonstrating every supported IPC operation
- **Component recipe JSONs** for Greengrass deployment reference

## Configuration

Add to your `local.conf` or distro config:

### Default (SDK + samples)

No configuration needed. The recipe builds with `rust` and `samples` enabled by default.

### Disable samples (SDK only)

```bitbake
PACKAGECONFIG:pn-aws-greengrass-component-sdk = "rust"
```

### Disable Rust support

```bitbake
PACKAGECONFIG:pn-aws-greengrass-component-sdk = "samples"
```

### Minimal (C/C++ SDK only, no samples, no Rust)

```bitbake
PACKAGECONFIG:pn-aws-greengrass-component-sdk = ""
```

## Packages

| Package | Contents |
|---------|----------|
| `aws-greengrass-component-sdk` | Meta package (SDK is static-only, use `-dev` or `-staticdev`) |
| `aws-greengrass-component-sdk-dev` | Headers (`gg/*.h`, `gg/*.hpp`) and Rust rlib |
| `aws-greengrass-component-sdk-staticdev` | Static libraries (`libgg-sdk.a`, `libgg-sdk++.a`) |
| `aws-greengrass-component-sdk-samples` | C/C++/Rust sample binaries and component recipe JSONs |
| `aws-greengrass-component-sdk-doc` | Documentation |
| `aws-greengrass-component-sdk-ptest` | Runtime tests for sample binaries |

## Adding samples to your image

```bitbake
IMAGE_INSTALL:append = " aws-greengrass-component-sdk-samples"
```

The sample binaries are installed to `/usr/bin/` and the component recipe
JSONs to `/usr/share/greengrass/component-recipes/`.

## Writing your own Greengrass component

Greengrass components are **self-contained static binaries** that communicate
with the Greengrass nucleus via IPC. The SDK is a build-time dependency only.

### C/C++ component recipe

```bitbake
DEPENDS = "aws-greengrass-component-sdk"
inherit cmake

# Your CMakeLists.txt links against gg-sdk (C) or gg-sdk++ (C++)
# target_link_libraries(my-component PRIVATE gg-sdk)
```

### Rust component recipe

```bitbake
DEPENDS = "aws-greengrass-component-sdk"
inherit cargo

export RUSTFLAGS:append = " \
    --extern gg_sdk=${STAGING_LIBDIR}/rustlib/${RUST_HOST_SYS}/lib/libgg_sdk.rlib \
    -L ${STAGING_LIBDIR} \
"
```

### Deploying your component

1. Build your component recipe with `bitbake my-component`
2. Copy the binary to S3
3. Create a component recipe JSON (see samples in `/usr/share/greengrass/component-recipes/`)
4. Register with `aws greengrassv2 create-component-version`
5. Deploy to your thing group

See the [upstream deployment guide](https://github.com/aws-greengrass/aws-greengrass-component-sdk/blob/main/samples/README.md) for details.
