SUMMARY = "AWS C++ SDK"
DESCRIPTION = "AWS C++ SDK and ptest"
HOMEPAGE = "https://github.com/aws/aws-sdk-cpp"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

DEPENDS += "\
    aws-crt-cpp \
    curl \
"

AWS_SDK_PACKAGES = ""

PACKAGES_DYNAMIC = "^${PN}-.*"

SRC_URI = "\
    git://github.com/aws/aws-sdk-cpp.git;protocol=https;branch=main \
    file://0002-build-fix-building-without-external-dependencies.patch \
    file://run-ptest"

SRCREV = "3a4c61c4166912e3b17566ebf9edfb91ce9065fc"

S = "${WORKDIR}/git"

inherit cmake ptest pkgconfig

PACKAGECONFIG ??= "\
    ${@bb.utils.filter('DISTRO_FEATURES', 'pulseaudio', d)} \
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'with-tests', '', d)}"

PACKAGECONFIG[pulseaudio] = "-DPULSEAUDIO=TRUE, -DPULSEAUDIO=FALSE, pulseaudio"

# CMAKE_CROSSCOMPILING=OFF will enable build of unit tests
PACKAGECONFIG[with-tests] = "-DENABLE_TESTING=ON -DAUTORUN_UNIT_TESTS=OFF -DCMAKE_CROSSCOMPILING=OFF,-DENABLE_TESTING=OFF -DAUTORUN_UNIT_TESTS=OFF, googletest"

python populate_packages:prepend () {
    packages = []
    def hook(f, pkg, file_regex, output_pattern, modulename):
        packages.append(pkg)

    # Put the libraries into separate packages
    do_split_packages(d, d.expand('${libdir}'), r'^lib(.*)\.so$', '%s', 'library for %s', extra_depends='', prepend=True, hook=hook)

    d.setVar("AWS_SDK_PACKAGES", " ".join(packages))
}

# enable PACKAGECONFIG = "static" to build static instead of shared libs
PACKAGECONFIG[static] = "-DBUILD_SHARED_LIBS=OFF,-DBUILD_SHARED_LIBS=ON,,"

# Notify that libraries are not versioned
FILES_SOLIBSDEV = ""

# -Werror will cause deprecation warnings to fail the build e.g. OpenSSL cause one, so disable these warnings
OECMAKE_CXX_FLAGS += "-Wno-deprecated-declarations"

# -Wno-maybe-uninitialized is related to this: https://github.com/aws/aws-sdk-cpp/issues/2234
OECMAKE_CXX_FLAGS += "${@bb.utils.contains('PTEST_ENABLED', '1', '-Wno-maybe-uninitialized', '', d)}"

OECMAKE_CXX_FLAGS += "-Wno-psabi"

EXTRA_OECMAKE += "\
     -DBUILD_DEPS=OFF \
"

RDEPENDS:${PN}-ptest += "\
    bash \
    python3 \
"
# "aws-sdk-cpp" is a meta package which pulls in all aws-sdk-cpp libraries
ALLOW_EMPTY:${PN} = "1"
RRECOMMENDS:${PN} += "${AWS_SDK_PACKAGES}"
RRECOMMENDS:${PN}:class-native = ""

do_install_ptest () {
    install -d ${D}${PTEST_PATH}/tests
    find ${B}/generated/tests -executable -type f -exec install -m 0755 "{}" ${D}${PTEST_PATH}/tests/ \;
}

# this is related to this bug 
# https://github.com/aws/aws-sdk-cpp/issues/2242
EXTRA_OECMAKE:append:armv4 = " ${LIBS_BUILD_ON_ARM32} "
EXTRA_OECMAKE:append:armv5 = " ${LIBS_BUILD_ON_ARM32} "
EXTRA_OECMAKE:append:armv6 = " ${LIBS_BUILD_ON_ARM32} "
EXTRA_OECMAKE:append:class-target:arm = " ${LIBS_BUILD_ON_ARM32} "

# to save compile time you can specify libs you only want to build
# (we can't have spaces in -DBUILD_ONLY, hence the strange formatting)
LIBS_BUILD_ON_ARM32 = "\
-DBUILD_ONLY='\
access-management;\
accessanalyzer;\
account;\
acm-pca;\
acm;\
alexaforbusiness;\
amp;\
amplify;\
amplifybackend;\
amplifyuibuilder;\
apigateway;\
apigatewaymanagementapi;\
apigatewayv2;\
appconfig;\
appconfigdata;\
appflow;\
appintegrations;\
application-autoscaling;\
application-insights;\
applicationcostprofiler;\
appmesh;\
apprunner;\
appstream;\
appsync;\
arc-zonal-shift;\
athena;\
auditmanager;\
autoscaling-plans;\
autoscaling;\
awstransfer;\
backup-gateway;\
backup;\
backupstorage;\
batch;\
billingconductor;\
braket;\
budgets;\
ce;\
chime-sdk-identity;\
chime-sdk-media-pipelines;\
chime-sdk-meetings;\
chime-sdk-messaging;\
chime-sdk-voice;\
chime;\
cloud9;\
cloudcontrol;\
clouddirectory;\
cloudformation;\
cloudfront;\
cloudhsm;\
cloudhsmv2;\
cloudsearch;\
cloudsearchdomain;\
cloudtrail;\
codeartifact;\
codebuild;\
codecatalyst;\
codecommit;\
codedeploy;\
codeguru-reviewer;\
codeguruprofiler;\
codepipeline;\
codestar-connections;\
codestar-notifications;\
codestar;\
cognito-identity;\
cognito-idp;\
cognito-sync;\
comprehend;\
comprehendmedical;\
compute-optimizer;\
config;\
connect-contact-lens;\
connect;\
connectcampaigns;\
connectcases;\
connectparticipant;\
controltower;\
core;\
cur;\
customer-profiles;\
databrew;\
dataexchange;\
datapipeline;\
datasync;\
dax;\
detective;\
devicefarm;\
devops-guru;\
directconnect;\
discovery;\
dlm;\
dms;\
docdb-elastic;\
docdb;\
drs;\
ds;\
dynamodb;\
dynamodbstreams;\
ebs;\
ec2-instance-connect;\
ecr-public;\
ecr;\
ecs;\
eks;\
elastic-inference;\
elasticache;\
elasticbeanstalk;\
elasticfilesystem;\
elasticloadbalancing;\
elasticloadbalancingv2;\
elasticmapreduce;\
elastictranscoder;\
email;\
emr-containers;\
emr-serverless;\
es;\
eventbridge;\
events;\
evidently;\
finspace-data;\
finspace;\
firehose;\
fis;\
fms;\
forecast;\
forecastquery;\
frauddetector;\
fsx;\
gamelift;\
gamesparks;\
glacier;\
globalaccelerator;\
glue;\
grafana;\
greengrass;\
greengrassv2;\
groundstation;\
guardduty;\
health;\
healthlake;\
honeycode;\
iam;\
identitystore;\
imagebuilder;\
importexport;\
inspector2;\
inspector;\
iot-data;\
iot-jobs-data;\
iot-roborunner;\
iot1click-devices;\
iot1click-projects;\
iot;\
iotanalytics;\
iotdeviceadvisor;\
iotevents-data;\
iotevents;\
iotfleethub;\
iotfleetwise;\
iotsecuretunneling;\
iotsitewise;\
iotthingsgraph;\
iottwinmaker;\
iotwireless;\
ivs;\
ivschat;\
kafka;\
kafkaconnect;\
kendra;\
keyspaces;\
kinesis-video-archived-media;\
kinesis-video-media;\
kinesis-video-signaling;\
kinesis;\
kinesisanalytics;\
kinesisanalyticsv2;\
kinesisvideo;\
kms;\
lakeformation;\
lambda;\
lex-models;\
lex;\
lexv2-models;\
lexv2-runtime;\
license-manager-user-subscriptions;\
license-manager;\
lightsail;\
location;\
logs;\
lookoutequipment;\
lookoutmetrics;\
lookoutvision;\
m2;\
machinelearning;\
macie2;\
macie;\
managedblockchain;\
marketplace-catalog;\
marketplace-entitlement;\
marketplacecommerceanalytics;\
mediaconnect;\
mediaconvert;\
medialive;\
mediapackage-vod;\
mediapackage;\
mediastore-data;\
mediastore;\
mediatailor;\
memorydb;\
meteringmarketplace;\
mgn;\
migration-hub-refactor-spaces;\
migrationhub-config;\
migrationhuborchestrator;\
migrationhubstrategy;\
mobile;\
monitoring;\
mq;\
mturk-requester;\
mwaa;\
neptune;\
network-firewall;\
networkmanager;\
nimble;\
oam;\
omics;\
opensearch;\
opensearchserverless;\
opsworks;\
opsworkscm;\
organizations;\
outposts;\
panorama;\
personalize-events;\
personalize-runtime;\
personalize;\
pi;\
pinpoint-email;\
pinpoint-sms-voice-v2;\
pinpoint;\
pipes;\
polly;\
pricing;\
privatenetworks;\
proton;\
qldb-session;\
qldb;\
queues;\
quicksight;\
ram;\
rbin;\
rds-data;\
rds;\
redshift-data;\
redshift-serverless;\
redshift;\
rekognition;\
resiliencehub;\
resource-explorer-2;\
resource-groups;\
resourcegroupstaggingapi;\
s3-crt;\
s3;\
s3control;\
s3outposts;\
sagemaker-a2i-runtime;\
sagemaker-edge;\
sagemaker-featurestore-runtime;\
sagemaker-geospatial;\
sagemaker-metrics;\
sagemaker-runtime;\
sagemaker;\
savingsplans;\
scheduler;\
schemas;\
sdb;\
secretsmanager;\
securityhub;\
securitylake;\
serverlessrepo;\
service-quotas;\
servicecatalog-appregistry;\
servicecatalog;\
servicediscovery;\
sesv2;\
shield;\
signer;\
simspaceweaver;\
sms-voice;\
sms;\
snow-device-management;\
snowball;\
sns;\
sqs;\
ssm-contacts;\
ssm-incidents;\
ssm-sap;\
ssm;\
sso-admin;\
sso-oidc;\
sso;\
states;\
storagegateway;\
sts;\
support-app;\
support;\
swf;\
synthetics;\
text-to-speech;\
textract;\
timestream-query;\
timestream-write;\
transcribe;\
transcribestreaming;\
transfer;\
translate;\
voice-id;\
waf-regional;\
waf;\
wafv2;\
wellarchitected;\
wisdom;\
workdocs;\
worklink;\
workmail;\
workmailmessageflow;\
workspaces-web;\
workspaces;\
xray;'\
"

### removed due to build failures with arm32
### with arm64 and x86-64 they work
# ec2;\
# s3-encryption;\
# identity-management;\
