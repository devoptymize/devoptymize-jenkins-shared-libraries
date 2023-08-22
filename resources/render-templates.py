# Filename: render-templates.py
# Description: This python script renders/parse jinja template to yaml file.
# Author: CloudifyOps
# Date: 2023-05-04
# Version: 1.0
# License: Copyright (c) [2023] CloudifyOps
# This script is licensed under CloudifyOps
# dependency versions: 
## python - 3.8.10
## jinja2 - 2.10.1
## jinja2-cli - 0.8.2
## pyyaml - 5.3.1
#!/usr/bin/env python
import os
import argparse
from jinja2 import Environment, FileSystemLoader
from yaml import load, Loader, dump

parser = argparse.ArgumentParser(description='Render a Jinja2 template with YAML variables.')
parser.add_argument('template_file', help='The Jinja2 template file.')
parser.add_argument('variables_file', help='The YAML variables file.')
parser.add_argument('output_file', help='The output YAML file.')
args = parser.parse_args()

# Load the Jinja2 template from file
env = Environment(loader=FileSystemLoader(os.path.dirname(args.template_file)))
template = env.get_template(os.path.basename(args.template_file))

# Load the YAML variables from file
with open(args.variables_file, 'r') as f:
    variables = load(f, Loader=Loader)

# Render the template with the variables
output = template.render(variables)

# Write the rendered output to the output file
with open(args.output_file, 'w') as f:
    f.write(output)

print(f'Rendered template written to {args.output_file}')

