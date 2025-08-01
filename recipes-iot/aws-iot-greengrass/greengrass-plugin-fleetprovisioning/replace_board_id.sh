#!/bin/sh

file_path="$1"

default_iface=$(busybox route | grep default | awk '{print $8}')
mac_address=$(busybox ifconfig "$default_iface" | grep -o -E '([[:xdigit:]]{1,2}:){5}[[:xdigit:]]{1,2}' | tr ':' '_')

sed -i "s/<unique>/$mac_address/g" "$file_path"
