#!/usr/bin/env bash
#
#  Copyright (C) 2006-2023 Talend Inc. - www.talend.com
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#

set -xe

# This script allow to package the current project with maven on ci settings.xml
# You can provide extra parameters to tune maven
# $1: extra_mvn_parameters
main() {
  _EXTRA_MVN_PARAMETERS=("$@")

  mvn install --update-snapshots \
              --batch-mode \
              --settings ci/settings.xml \
              --projects core \
              ${_EXTRA_MVN_PARAMETERS[*]}

  mvn install --update-snapshots \
              --batch-mode \
              --settings ci/settings.xml \
              --file components/components-jdbc/pom.xml \
              --projects components-jdbc-runtime-beam \
              ${_EXTRA_MVN_PARAMETERS[*]}

  mvn package --update-snapshots \
              --batch-mode \
              --settings ci/settings.xml \
              --file components/pom.xml \
              --projects components-jdbc-runtime-beam \
              ${_EXTRA_MVN_PARAMETERS[*]}

}

main "$@"
