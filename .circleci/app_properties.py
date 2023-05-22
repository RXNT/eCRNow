#!/usr/bin/python3

# Installation

# usage: env.py [-h] --filename APP_PROPS_FILE [APP_PROPS_FILE ...] [--output [JSON_OUTFILE]]

# Process some integers.

# optional arguments:
#   -h, --help            show this help message and exit
#   --filename ENV_FILE [ENV_FILE ...]
#                         The .env file with key-value pairs.
#   --output [JSON_OUTFILE]
#                         The name of your output json file.

# Default Output
# .env.json file that contains serialized JSON of key/val pairs
# Custom Output (--output)
# json encoded file with custom filename

import sys, getopt, json
import argparse

parser = argparse.ArgumentParser(description='Process some integers.')

parser.add_argument('--filename', dest='app_props_file', nargs='+', default=sys.stdin, required=True, help='The .env file with key-value pairs.')

parser.add_argument('--output', dest='json_outfile', nargs='?', default='env.json', help='The name of your output json file.')

args = parser.parse_args()


def convert_app_props_file(app_props_filename):
    print('Loading file: ', app_props_filename)

    props_file = open(app_props_filename)
    prop_lines = props_file.read().splitlines()
    props_file.close()

    config = {}
    for i, line in enumerate(prop_lines):

        if (line.startswith("#")):
            continue

        key_val = line.split("=", 1)
        key = key_val[0].strip()
        val = key_val[1].strip()

        if (not val.startswith("@Microsoft.KeyVault")):
            config[key] = val

    with open(args.json_outfile, 'w', encoding='utf-8') as f:
        json.dump(config, f, ensure_ascii=False, indent=4)
        print('Wrote json to ' + args.json_outfile)

if __name__ == "__main__":
    convert_app_props_file(args.app_props_file[0])