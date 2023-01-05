SUMMARY = "AWS C++ SDK"
DESCRIPTION = "AWS C++ SDK and ptest"
HOMEPAGE = "https://github.com/aws/aws-sdk-cpp"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

DEPENDS += "\
    aws-c-auth \
    aws-crt-cpp \
    curl \
"

AWS_SDK_PACKAGES = ""

PACKAGES_DYNAMIC = "^${PN}-.*"

SRC_URI = "\
    git://github.com/aws/aws-sdk-cpp.git;protocol=https;branch=main \
    file://0002-build-fix-building-without-external-dependencies.patch \
    file://run-ptest"

SRCREV = "a0dcbc69c6000577ff0e8171de998ccdc2159c88"

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

# to save compile time you can specify libs you only want to build
# (we can't have spaces in -DBUILD_ONLY, hence the strange formatting)
EXTRA_OECMAKE += "\
-DBUILD_ONLY='\
accessanalyzer;\
access-management;\
account;\
acm;\
acm-pca;\
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
applicationcostprofiler;\
application-insights;\
appmesh;\
apprunner;\
appstream;\
appsync;\
arc-zonal-shift;\
athena;\
auditmanager;\
autoscaling;\
autoscaling-plans;\
awstransfer;\
backup;\
backup-gateway;\
backupstorage;\
batch;\
billingconductor;\
braket;\
budgets;\
ce;\
chime;\
chime-sdk-identity;\
chime-sdk-media-pipelines;\
chime-sdk-meetings;\
chime-sdk-messaging;\
chime-sdk-voice;\
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
codeguruprofiler;\
codeguru-reviewer;\
codepipeline;\
codestar;\
codestar-connections;\
codestar-notifications;\
cognito-identity;\
cognito-idp;\
cognito-sync;\
comprehend;\
comprehendmedical;\
compute-optimizer;\
config;\
connect;\
connectcampaigns;\
connectcases;\
connect-contact-lens;\
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
docdb;\
docdb-elastic;\
drs;\
ds;\
dynamodb;\
dynamodbstreams;\
ebs;\
ecr;\
ecr-public;\
ecs;\
eks;\
elasticache;\
elasticbeanstalk;\
elasticfilesystem;\
elastic-inference;\
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
finspace;\
finspace-data;\
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
inspector;\
inspector2;\
iot1click-devices;\
iot1click-projects;\
iotanalytics;\
iot-data;\
iotdeviceadvisor;\
iotevents;\
iotevents-data;\
iotfleethub;\
iotfleetwise;\
iot-jobs-data;\
iot-roborunner;\
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
kinesis;\
kinesisanalytics;\
kinesisanalyticsv2;\
kinesisvideo;\
kinesis-video-archived-media;\
kinesis-video-media;\
kinesis-video-signaling;\
kms;\
lakeformation;\
lambda;\
lex;\
lex-models;\
lexv2-models;\
lexv2-runtime;\
license-manager;\
license-manager-user-subscriptions;\
location;\
logs;\
lookoutequipment;\
lookoutmetrics;\
lookoutvision;\
m2;\
machinelearning;\
macie;\
macie2;\
managedblockchain;\
marketplace-catalog;\
marketplacecommerceanalytics;\
marketplace-entitlement;\
mediaconnect;\
mediaconvert;\
medialive;\
mediapackage;\
mediapackage-vod;\
mediastore;\
mediastore-data;\
mediatailor;\
memorydb;\
meteringmarketplace;\
mgn;\
migrationhub-config;\
migrationhuborchestrator;\
migration-hub-refactor-spaces;\
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
personalize;\
personalize-events;\
personalize-runtime;\
pi;\
pipes;\
polly;\
pricing;\
privatenetworks;\
proton;\
qldb;\
qldb-session;\
queues;\
ram;\
rbin;\
rekognition;\
resiliencehub;\
resource-explorer-2;\
resource-groups;\
resourcegroupstaggingapi;\
savingsplans;\
scheduler;\
schemas;\
sdb;\
secretsmanager;\
securitylake;\
serverlessrepo;\
servicecatalog;\
servicecatalog-appregistry;\
servicediscovery;\
service-quotas;\
sesv2;\
shield;\
signer;\
simspaceweaver;\
sms;\
sms-voice;\
snowball;\
snow-device-management;\
sns;\
sqs;\
sso;\
sso-admin;\
sso-oidc;\
states;\
storagegateway;\
sts;\
support;\
support-app;\
swf;\
synthetics;\
textract;\
text-to-speech;\
timestream-query;\
timestream-write;\
transcribe;\
transcribestreaming;\
transfer;\
translate;\
voice-id;\
waf;\
waf-regional;\
wafv2;\
wellarchitected;\
wisdom;\
workdocs;\
worklink;\
workmail;\
workmailmessageflow;\
workspaces;\
workspaces-web;\
xray;'\
"

### removed due to build failures with arm32, arm64 and x86-64 they work ###
# this issue: https://github.com/aws/aws-sdk-cpp/issues/2242
# ec2;\
# ec2-instance-connect;\
# polly-sample;\
# ssm;\
# ssm-contacts;\
# ssm-incidents;\
# ssm-sap;\
# sagemaker;\
# sagemaker-a2i-runtime;\
# sagemaker-edge;\
# sagemaker-featurestore-runtime;\
# sagemaker-geospatial;\
# sagemaker-metrics;\
# sagemaker-runtime;\
# rds;\
# rds-data;\
# securityhub;\
# quicksight;\
# iot;\
# redshift;\
# redshift-data;\
# redshift-serverless;\
# pinpoint;\
# pinpoint-email;\
# pinpoint-sms-voice-v2;\
# glue;\
# lightsail;\
# s3-crt;\
# s3;\
# s3control;\
# s3-encryption;\
# s3outposts;\
# identity-management;\