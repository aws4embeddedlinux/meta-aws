# Copyright (c) 2022 Amazon.com, Inc. or its affiliates.
# SPDX-License-Identifier: MIT

import json
import sys

if len(sys.argv) != 2:
    print(f"usage: {sys.argv[0]} [result file]")
    sys.exit(1)

with open (sys.argv[1], 'rb') as json_file:
        data = json.load(json_file)
        for testsuite in data['testsuites']:
            for test in testsuite['testsuite']:
                result = 'PASS' if not 'failures' in test else 'FAIL'
                print(f"{result}: {test['classname']}_{test['name']}")
