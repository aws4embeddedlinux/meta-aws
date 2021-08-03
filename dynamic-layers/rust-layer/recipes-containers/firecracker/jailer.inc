
CVE_PRODUCT = "jailer"

do_compile:prepend() {
   
    # artifically trigger a re-run of the build script
    touch ${S}/build.rs
}
