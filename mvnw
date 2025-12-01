#!/bin/sh
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
# ----------------------------------------------------------------------------

set -euo pipefail

WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

BASEDIR=$(cd "$(dirname "$0")"; pwd)

WRAPPER_JAR="$BASEDIR/.mvn/wrapper/maven-wrapper.jar"
WRAPPER_PROPERTIES="$BASEDIR/.mvn/wrapper/maven-wrapper.properties"
WRAPPER_FALLBACK_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"

if [ ! -f "$WRAPPER_JAR" ]; then
  echo "Downloading Maven wrapper jar..."
  mkdir -p "$BASEDIR/.mvn/wrapper"
  URL=$(grep wrapperUrl "$WRAPPER_PROPERTIES" | cut -d= -f2)
  if [ -z "$URL" ]; then
    URL="$WRAPPER_FALLBACK_URL"
  fi
  curl -fsSL "$URL" -o "$WRAPPER_JAR"
fi

JAVA_EXE="${JAVA_HOME:-}/bin/java"
if [ -x "$JAVA_EXE" ]; then
  exec "$JAVA_EXE" -classpath "$WRAPPER_JAR" $WRAPPER_LAUNCHER "$@"
else
  exec java -classpath "$WRAPPER_JAR" $WRAPPER_LAUNCHER "$@"
fi


