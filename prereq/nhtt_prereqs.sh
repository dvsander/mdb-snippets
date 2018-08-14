#!/bin/sh

# This script will run system requirement checks for the rdbms2mongodb lab

RED='\033[0;31m'
NC='\033[0m'
GREEN='\033[0;32m'

expected_java="1.8"
expected_javac="1.8"

SOMETHING_MISSING=0

function print_good {
   printf "${GREEN}$1${NC}\n"
}

function print_error {
  printf "${RED}$1${NC}\n"
  SOMETHING_MISSING=1
}

function check_version {
  version=$( $1 --version|head -1|awk '{ print $3 }'|awk -F\, '{ print $1}'|awk -F\. '{print $1"."$2}' )
  if [ "$version" != "$2" ]
  then
    print_error "  Your current version $version is not the expected: $2"
  else
    print_good "  Correct version found: $version"
  fi
}

# Generic check, just looking for the binary in tha path
function check_tool() {
  echo
  echo "Checking installation for '$1'"
  tool_exec=$(which $2)
  if [ ! -z $tool_exec ]
  then
    print_good "  '$1' installed: $tool_exec"
  else
    print_error "  '$1' could not be found!"
  fi
}

function check_brew {
  echo
  echo "Checking installation for 'Brew'"
  brew_exec=$(which brew)
  if [ ! -z $brew_exec ]
  then
    print_good "  'Brew' installed: $brew_exec"
  else
    print_error "  'Brew' could not be found!"
  fi
}

function check_python_module() {
  echo
  echo "Checking installation for Python module '$1'"
  ret=$(python -c "import $1")
  if [ "$?" == "0" ]
  then
    print_good "  Python module '$1' installed"
  else
    print_error "  Python module '$1' could not be imported!"
  fi
}


check_tool Brew brew
check_tool pip pip
check_python_module pymongo
check_python_module virtualenv
check_tool npm npm
check_tool m m
check_tool MTools mlaunch
check_tool VirtualBox virtualbox
check_tool Vagrant vagrant
check_tool Git git

if [ $SOMETHING_MISSING != 0 ]
then
  echo
  print_error "Check your setup! Something seems to be missing."
  print_error "Installation instructions for the above tools are available at:"
  print_error "  https://wiki.corp.mongodb.com/display/10GEN/Tools+to+install+for+NHTT"
  echo
fi

exit $SOMETHING_MISSING
