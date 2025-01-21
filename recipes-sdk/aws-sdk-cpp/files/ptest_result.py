# Copyright (c) 2022 Amazon.com, Inc. or its affiliates.
# SPDX-License-Identifier: MIT

import json
import sys
import re

def check_sanitizer_failure(test):
    # Look for sanitizer errors in system-out or system-err if they exist
    system_out = test.get('system-out', '')
    system_err = test.get('system-err', '')
    
    sanitizer_patterns = [
        r'==\d+==ERROR: .*Sanitizer',
        r'runtime error:',
        r'SUMMARY: .*Sanitizer',
        r'ERROR: .*Sanitizer detected'
    ]
    
    for pattern in sanitizer_patterns:
        if (re.search(pattern, system_out) or 
            re.search(pattern, system_err)):
            return True
    return False

if len(sys.argv) != 2:
    print(f"usage: {sys.argv[0]} [result file]")
    sys.exit(1)

with open(sys.argv[1], 'rb') as json_file:
    data = json.load(json_file)
    for testsuite in data['testsuites']:
        for test in testsuite['testsuite']:
            # Check both regular test failures and sanitizer failures
            has_failures = 'failures' in test
            has_sanitizer_failure = check_sanitizer_failure(test)
            
            result = 'FAIL' if (has_failures or has_sanitizer_failure) else 'PASS'
            print(f"{result}: {test['classname']}_{test['name']}")