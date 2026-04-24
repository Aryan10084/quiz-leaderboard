#!/bin/sh

# Maven Wrapper startup script for Unix-like environments.

basedir=$(cd "$(dirname "$0")"; pwd)
JAVA_EXEC="${JAVA_HOME:-}/bin/java"

if [ -d "$basedir" ]; then
  MAVEN_PROJECT_DIR="$basedir"
else
  MAVEN_PROJECT_DIR=$(pwd)
fi

if [ ! -x "$JAVA_EXEC" ]; then
  JAVA_EXEC=java
fi

exec "$JAVA_EXEC" -Dmaven.multiModuleProjectDirectory="$MAVEN_PROJECT_DIR" -cp "$basedir/.mvn/wrapper/maven-wrapper.jar" org.apache.maven.wrapper.MavenWrapperMain "$@"