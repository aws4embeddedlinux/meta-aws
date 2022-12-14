# Copyright (c) 2022 Amazon.com, Inc. or its affiliates.
# SPDX-License-Identifier: MIT

import xml.etree.ElementTree as ET
import sys

if len(sys.argv) != 2:
    print(f"usage: {sys.argv[0]} [result file]")
    sys.exit(1)

tree = ET.parse(sys.argv[1])

for child in tree.getroot():
    result = 'PASS' if child.attrib['status'] == 'run' else 'FAIL'
    print(f"{result}: {child.attrib['name']}")
