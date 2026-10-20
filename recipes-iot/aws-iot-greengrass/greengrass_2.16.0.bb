SUMMARY = "AWS IoT Greengrass Nucleus - Built from Source"
DESCRIPTION = "The Greengrass nucleus component provides functionality for device side orchestration of deployments and lifecycle management for execution of Greengrass components and applications. Built from source with custom patches."
HOMEPAGE = "https://github.com/aws-greengrass/aws-greengrass-nucleus"
LICENSE = "Apache-2.0"

CONFLICTS = "greengrass-bin"

require greengrass-nucleus.inc

# Maven repository mappings for custom Greengrass artifacts
MAVEN_REPO_URLS = "greengrass-common=https://d2jrmugq4soldf.cloudfront.net/snapshots yle-public=https://d2x444wtt5plvm.cloudfront.net/release"

# Maven dependencies are pre-downloaded and stored in greengrass-maven-deps.inc
# To update after a version upgrade, run: bitbake greengrass -c update_maven_deps
require greengrass-maven-deps.inc

# Fix repository URLs for Greengrass-specific artifacts after update_maven_deps
do_update_maven_deps[postfuncs] += "fix_greengrass_maven_repos"

python fix_greengrass_maven_repos() {
    import os
    import re

    thisdir = d.getVar("THISDIR")
    bpn = d.getVar("BPN")
    deps_file = os.path.join(thisdir, f"{bpn}-maven-deps.inc")

    if not os.path.exists(deps_file):
        return

    # Parse MAVEN_REPO_URLS to get repository mappings
    repo_urls = {}
    custom_repos = d.getVar("MAVEN_REPO_URLS") or ""
    for mapping in custom_repos.split():
        if "=" in mapping:
            repo_id, url = mapping.split("=", 1)
            repo_urls[repo_id] = url

    if not repo_urls:
        return

    # Read the file
    with open(deps_file, 'r') as f:
        content = f.read()

    # Get URLs from mappings
    greengrass_url = repo_urls.get("greengrass-common", "")
    yle_url = repo_urls.get("yle-public", "")

    if greengrass_url:
        # Fix Greengrass SNAPSHOT artifacts (com/aws/greengrass and -GG-SNAPSHOT)
        content = re.sub(
            r'https://repo1\.maven\.org/maven2/(com/aws/greengrass/[^"]*SNAPSHOT[^"]*)',
            f'{greengrass_url}/\\1',
            content
        )
        content = re.sub(
            r'https://repo1\.maven\.org/maven2/([^"]*-GG-SNAPSHOT[^"]*)',
            f'{greengrass_url}/\\1',
            content
        )

    if yle_url:
        # Fix yle-public artifacts
        content = re.sub(
            r'https://repo1\.maven\.org/maven2/(fi/yle/[^"]*)',
            f'{yle_url}/\\1',
            content
        )

    # Write back
    with open(deps_file, 'w') as f:
        f.write(content)

    bb.note("Fixed Greengrass-specific Maven repository URLs")
}

LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

# Build dependencies (gettext-native and python3-pyyaml-native come from the inc)
DEPENDS += "\
    maven-native \
    jdk-11-native \
"

SRC_URI = "\
    git://github.com/aws-greengrass/aws-greengrass-nucleus.git;protocol=https;nobranch=1;name=greengrass \
    file://001-service-time-wait.patch \
    file://002-fix-service-exec-and-docker.patch \
    file://greengrassv2-init.yaml \
    file://run-ptest \
    file://config.yaml.template \
    "

# Pin to specific version tag
SRCREV_greengrass = "v${PV}"

inherit maven_update_deps

COMPATIBLE_MACHINE:armv7a = "(.*)"
COMPATIBLE_MACHINE:armv7ve = "(.*)"
COMPATIBLE_MACHINE:x86 = "(.*)"
COMPATIBLE_MACHINE:x86-64 = "(.*)"
COMPATIBLE_MACHINE:aarch64 = "(.*)"
COMPATIBLE_MACHINE:riscv64 = "null"

# Nucleus artifacts, built from source (see do_install in greengrass-nucleus.inc)
GG_NUCLEUS_BINDIR = "${S}/scripts"
GG_NUCLEUS_JAR = "${S}/target/Greengrass.jar"
GG_INIT_YAML = "${UNPACKDIR}/greengrassv2-init.yaml"

# Configure Maven environment
export JAVA_HOME = "${STAGING_LIBDIR_NATIVE}/jvm/jdk-11-native"
export MAVEN_OPTS = "-Xmx2048m -XX:MaxPermSize=512m"

python do_configure() {
    import os
    import shutil
    import re

    s_dir = d.getVar("S")
    dl_dir = d.getVar("DL_DIR")
    thisdir = d.getVar("THISDIR")
    bpn = d.getVar("BPN")

    # Read the mapping file
    map_file = os.path.join(thisdir, f"{bpn}-maven-map.txt")
    if not os.path.exists(map_file):
        bb.fatal("Maven mapping file not found. Run: bitbake greengrass -c update_maven_deps")

    # Create Maven local repository
    maven_repo = os.path.join(s_dir, ".m2", "repository")
    os.makedirs(maven_repo, exist_ok=True)

    # Copy artifacts from DL_DIR to Maven repository structure
    snapshot_dirs = set()
    with open(map_file, 'r') as f:
        for line in f:
            line = line.strip()
            if not line:
                continue

            download_name, repo_path = line.split(' ', 1)
            src_file = os.path.join(dl_dir, download_name)
            dst_file = os.path.join(maven_repo, repo_path)

            if not os.path.exists(src_file):
                bb.fatal(f"Downloaded file not found: {src_file}")

            os.makedirs(os.path.dirname(dst_file), exist_ok=True)
            shutil.copy2(src_file, dst_file)

            # Track SNAPSHOT directories for symlink creation
            if "SNAPSHOT" in repo_path:
                snapshot_dirs.add(os.path.dirname(dst_file))

    # Create generic SNAPSHOT files for Maven (copy from timestamped versions)
    for snapshot_dir in snapshot_dirs:
        # Determine the SNAPSHOT version from directory name
        # e.g., /path/to/artifact/2.1.1-SNAPSHOT/ -> 2.1.1-SNAPSHOT
        snapshot_version = os.path.basename(snapshot_dir)

        # Get artifact name from parent directory
        # e.g., /path/to/component-common/2.1.1-SNAPSHOT/ -> component-common
        artifact_name = os.path.basename(os.path.dirname(snapshot_dir))

        for timestamped_file in os.listdir(snapshot_dir):
            # Match timestamped SNAPSHOT files: artifact-version-timestamp-buildnum.ext
            # e.g., component-common-2.1.1-20240722.172146-16.jar
            match = re.match(r'(.+)-(\d{8}\.\d{6})-(\d+)\.(jar|pom|xml)$', timestamped_file)
            if match:
                _, timestamp, build_num, ext = match.groups()
                generic_name = f"{artifact_name}-{snapshot_version}.{ext}"
                generic_path = os.path.join(snapshot_dir, generic_name)
                timestamped_path = os.path.join(snapshot_dir, timestamped_file)

                # Copy timestamped file to generic name
                if not os.path.exists(generic_path):
                    shutil.copy2(timestamped_path, generic_path)

    bb.note(f"Set up Maven local repository at {maven_repo}")
}

do_compile() {
    cd ${S}

    bbnote "Building Greengrass Nucleus with Maven..."
    bbnote "JAVA_HOME: ${JAVA_HOME}"

    # Build with Maven in offline mode using pre-downloaded dependencies
    # -B: batch mode (non-interactive)
    # -o: offline mode
    # -Dmaven.repo.local: use local repository
    # -DskipTests: skip running tests
    # -Dmaven.javadoc.skip: skip javadoc generation
    # -Dmaven.source.skip: skip source jar generation
    mvn -B -o clean package \
        -Dmaven.repo.local=${S}/.m2/repository \
        -DskipTests \
        -Dmaven.javadoc.skip=true \
        -Dmaven.source.skip=true \
        -Dcheckstyle.skip=true \
        -Dspotbugs.skip=true \
        -Dlicense.skip=true

    bbnote "Maven build completed successfully"

    # Verify the JAR was built
    if [ ! -f ${S}/target/Greengrass.jar ]; then
        bbfatal "Greengrass.jar was not built! Check Maven output."
    fi
}
