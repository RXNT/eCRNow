#!/usr/bin/python3

# Installation
# pip3 install python-dotenv

# usage: env.py [-h] --filename ENV_FILE [ENV_FILE ...] [--output [JSON_OUTFILE]]

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
from dotenv import dotenv_values

parser = argparse.ArgumentParser(description='Process some integers.')

parser.add_argument('--filename', dest='env_file', nargs='+', default=sys.stdin, required=True, help='The .env file with key-value pairs.')

parser.add_argument('--output', dest='json_outfile', nargs='?', default='env.json', help='The name of your output json file.')

args = parser.parse_args()

def clean_non_ms_keyvault_values(config):
    for key in config.copy().keys():
        if(not str(config[key]).startswith("@Microsoft.KeyVault")):
            config.pop(key, None)

    return config

def convert_env_file(env_filename):
    print('Loading file: ', args.env_file[0])
    config = clean_non_ms_keyvault_values(
        dotenv_values(env_filename)
    )

    with open(args.json_outfile, 'w', encoding='utf-8') as f:
        json.dump(config, f, ensure_ascii=False, indent=4)
        print('Wrote json to ' + args.json_outfile)
        
if __name__ == "__main__":
    convert_env_file(args.env_file[0])