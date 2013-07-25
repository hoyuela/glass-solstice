require 'calabash-android/cucumber'
require 'yaml'

# Service timeout when waiting for elements
DEFAULT_SERVICE_TIMEOUT = 20

# Config file to hold user settings
CONFIG = YAML::load_file(File.join(File.dirname(File.expand_path(__FILE__)), 'config.yml'))