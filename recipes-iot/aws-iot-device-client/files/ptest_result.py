# Copyright (c) 2022 Amazon.com, Inc. or its affiliates.
# SPDX-License-Identifier: MIT

import json
import sys

if len(sys.argv) != 2:
    print(f"usage: {sys.argv[0]} [result file]")
    sys.exit(1)

data = json.load(open(sys.argv[1]))


for suite in data['testsuites']:
    for test in suite['name']:
        print (test)
for child in tree.getroot():
    result = 'PASS' if child.attrib['status'] == 'run' else 'FAIL'
    print(f"{result} {child.attrib['name']}")
