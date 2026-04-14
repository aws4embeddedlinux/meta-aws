# aws-greengrass-component-sdk Patches

## Overview

These patches modify the upstream `aws-greengrass-component-sdk` Rust/C
hybrid library for cross-compilation under Yocto/OE. The SDK provides an
IPC interface for Greengrass components written in C, C++, and Rust.

Upstream repo: https://github.com/aws-greengrass/aws-greengrass-component-sdk

## Patch Analysis (as of upgrade 0.4.0 → 1.0.1)

### 0001-Add-bindgen-to-build.rs.patch
- **Purpose**: Adds `bindgen` as a build dependency and inserts bindgen
  invocation into `rust/build.rs` to generate Rust FFI bindings (`c.rs`)
  at build time from the C headers. Also adds a custom `timespec` struct
  definition via `raw_line()` to avoid platform-dependent issues, and
  blocklists the system `timespec` type.
- **Upstream-Status**: Pending
- **Status in v1.0.1**: MOSTLY UPSTREAMED. v1.0.1 already includes bindgen
  in `Cargo.toml` and has its own bindgen invocation in `build.rs`. However,
  the upstream version does NOT include the `timespec` blocklist/raw_line
  workaround or the `GG_OBJ_NULL` raw_line. The patch's Hunk #1
  (Cargo.toml) is now a no-op. Hunk #2 (build.rs) FAILS because upstream
  restructured `build.rs` significantly (dual layout support, different
  variable names).
- **Action needed**: Regenerate. Only the `timespec`-related raw_lines and
  blocklist are still needed (if patch 0002 is still needed). The bindgen
  invocation itself is now upstream.

### 0002-Fix-timespec-types-for-32-bit-platforms.patch
- **Purpose**: Fixes `rust/src/ipc.rs` to use `c_long` instead of `i64` for
  `timespec` fields (`tv_sec`, `tv_nsec`). On 32-bit ARM, `c_long` is 32
  bits while `i64` is 64 bits, causing struct layout mismatch with the C
  `timespec`.
- **Upstream-Status**: Pending
- **Applied only on**: `arm` (32-bit) via `SRC_URI:append:arm`
- **Status in v1.0.1**: STILL NEEDED. v1.0.1 still uses `i64` casts:
  `tv_sec: d.as_secs() as i64` and `tv_nsec: i64::from(d.subsec_nanos())`.
- **Action needed**: May need context refresh if surrounding code changed,
  but the fix itself is still required for 32-bit ARM.

### 0003-Build-gg-sdk-as-cdylib.patch
- **Purpose**: Adds `[lib] crate-type = ["cdylib", "rlib"]` to
  `rust/Cargo.toml` so the Rust library builds as both a C-compatible
  shared library (`libgg_sdk.so`) and a Rust static library (`.rlib`).
  Without this, only an rlib is produced and C consumers can't link it.
- **Upstream-Status**: Inappropriate [Yocto-specific]
- **Status in v1.0.1**: PARTIALLY UPSTREAMED. v1.0.1 has `[lib]` with
  `name = "gg_sdk"` but does NOT include `crate-type = ["cdylib", "rlib"]`.
- **Action needed**: Regenerate. The patch currently inserts `[lib]` before
  `[build-dependencies]`, but v1.0.1 already has a `[lib]` section. Needs
  to add `crate-type` to the existing `[lib]` section instead.

### 0004-Update-Cargo.lock-for-bindgen.patch
- **Purpose**: Adds all bindgen transitive dependencies to `rust/Cargo.lock`
  so that the Yocto cargo fetcher can download them. Without this, the lock
  file doesn't know about bindgen's dependency tree.
- **Upstream-Status**: Submitted (PR #91)
- **Status in v1.0.1**: UPSTREAMED. v1.0.1's `Cargo.lock` already includes
  bindgen and all its dependencies.
- **Action needed**: DROP. This patch is no longer needed.

### 0005-Disable-strip-in-Cargo-profile.patch
- **Purpose**: Removes `strip = true` from `[profile.release]` in
  `rust/Cargo.toml` so that Yocto's own stripping mechanism handles debug
  symbol removal instead of Cargo doing it during compilation.
- **Upstream-Status**: Inappropriate [Yocto-specific]
- **Status in v1.0.1**: STILL NEEDED. v1.0.1 still has `strip = true`.
- **Action needed**: Context refresh may be needed if Cargo.toml layout
  changed, but the fix itself is still required.

## Summary for v1.0.1

| Patch | Needed? | Action |
|-------|---------|--------|
| 0001 (bindgen in build.rs) | Partially | Regenerate — only timespec workaround needed |
| 0002 (32-bit timespec fix) | Yes (arm only) | Verify context, likely applies cleanly |
| 0003 (cdylib crate-type) | Yes | Regenerate for new Cargo.toml layout |
| 0004 (Cargo.lock update) | No | Drop — upstreamed in v1.0.1 |
| 0005 (disable strip) | Yes | Verify context, likely applies cleanly |
