require aws-iot-device-sdk-v2.inc
PV=1.5.2
SRC_URI = "LIC_FILES_CHKSUM = "file://aws-common-runtime/aws-crt-cpp/aws-common-runtime/aws-c-mqtt/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
file://aws-common-runtime/aws-crt-cpp/aws-common-runtime/aws-c-compression/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
file://aws-common-runtime/aws-crt-cpp/aws-common-runtime/aws-c-http/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
file://aws-common-runtime/aws-crt-cpp/aws-common-runtime/aws-c-common/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
file://aws-common-runtime/aws-crt-cpp/aws-common-runtime/aws-c-auth/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
file://aws-common-runtime/aws-crt-cpp/aws-common-runtime/aws-c-auth/tests/aws-sig-v4-test-suite/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
file://aws-common-runtime/aws-crt-cpp/aws-common-runtime/aws-c-io/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
file://aws-common-runtime/aws-crt-cpp/aws-common-runtime/s2n/LICENSE;md5=26d85861cd0c0d05ab56ebff38882975 \
file://aws-common-runtime/aws-crt-cpp/aws-common-runtime/s2n/pq-crypto/bike_r1/LICENSE;md5=175792518e4ac015ab6696d16c4f607e \
file://aws-common-runtime/aws-crt-cpp/aws-common-runtime/s2n/pq-crypto/bike_r2/LICENSE;md5=175792518e4ac015ab6696d16c4f607e \
file://aws-common-runtime/aws-crt-cpp/aws-common-runtime/s2n/tests/saw/spec/extras/HMAC/LICENSE;md5=bf1045b54de7b80b9a618e368f3cdce6 \
file://aws-common-runtime/aws-crt-cpp/aws-common-runtime/aws-c-cal/LICENSE;md5=34400b68072d710fecd0a2940a0d1658 \
file://aws-common-runtime/aws-crt-cpp/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
"
SRC_URI = "git://github.com/awslabs/aws-c-common.git;branch=master;destsuffix=${S}/aws-common-runtime/aws-crt-cpp/aws-common-runtime/aws-c-common;name=aws-c-common \
           git://github.com/awslabs/aws-c-io.git;branch=master;destsuffix=${S}/aws-common-runtime/aws-crt-cpp/aws-common-runtime/aws-c-io;name=aws-c-io \
           git://github.com/awslabs/aws-c-compression.git;branch=master;destsuffix=${S}/aws-common-runtime/aws-crt-cpp/aws-common-runtime/aws-c-compression;name=aws-c-compression \
           git://github.com/awslabs/aws-c-cal.git;branch=master;destsuffix=${S}/aws-common-runtime/aws-crt-cpp/aws-common-runtime/aws-c-cal;name=aws-c-cal \
           git://github.com/awslabs/aws-c-auth.git;branch=master;destsuffix=${S}/aws-common-runtime/aws-crt-cpp/aws-common-runtime/aws-c-auth;name=aws-c-auth \
           git://github.com/awslabs/aws-c-http.git;branch=master;destsuffix=${S}/aws-common-runtime/aws-crt-cpp/aws-common-runtime/aws-c-http;name=aws-c-http \
           git://github.com/awslabs/aws-c-mqtt.git;branch=master;destsuffix=${S}/aws-common-runtime/aws-crt-cpp/aws-common-runtime/aws-c-mqtt;name=aws-c-mqtt \
           git://github.com/awslabs/s2n.git;branch=master;destsuffix=${S}/aws-common-runtime/aws-crt-cpp/aws-common-runtime/s2n;name=s2n \
           git://github.com/awslabs/aws-crt-cpp.git;branch=master;destsuffix=${S}/aws-common-runtime/aws-crt-cpp;name=aws-crt-cpp \
           "
SRCREV_aws-c-common = "d023c9cb10e22bace150550a7357eab36164af52"
SRCREV_aws-c-io = "3b09cdceed9db43b2ac8642083417051de8d9041"
SRCREV_aws-c-compression = "c7e477bf6ab7df17cdad223300541fe3aa978f35"
SRCREV_aws-c-cal = "1c087719f78442f2ceb7bc8c79ac1e5f27bfc2d0"
SRCREV_aws-c-auth = "543e3764d7fa4f1228ed630d1aa40e4a0de396d6"
SRCREV_aws-c-http = "e5386127cd43b5aea476500b9a38d7e9b72b82f7"
SRCREV_aws-c-mqtt = "5ed5ee07e3be9515a30a6ff47a43ab7b9144ef3a"
SRCREV_s2n = "a977c447702c11e515876d896416e50ce1bc9adf"
SRCREV_aws-crt-cpp = "cec5295673483a4c0e8ea25da4304291ad101db6"
