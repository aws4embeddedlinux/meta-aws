SRC_URI += "\
    file://0002-kvm-bindings-use-local-version.patch \
    file://0003-Remove-abort-panic-from-the-workspace.patch \
"
UPSTREAM_VERSION_UNKNOWN = "1"
# set to match only git_invalid_tag_regex because UPSTREAM_VERSION_UNKNOWN seems to be broken for git
UPSTREAM_CHECK_GITTAGREGEX = "git_invalid_tag_regex"