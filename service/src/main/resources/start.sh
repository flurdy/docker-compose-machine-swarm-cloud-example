#!/bin/sh

set -e

JAVA_OPTS="-Xms512m -Xmx1024m"

APP=pizzaservice

APP_LOG_CONFIG=/etc/opt/${APP}/logback.xml
APP_CONFIG=/etc/opt/${APP}/docker.conf
APP_HOME=/opt/app
SHAPELESS_VERSION="_2.11-1.2.4"
APP_CLASSPATH="$APP_HOME/target/scala-2.11/${APP}.jar:$APP_HOME/lib/shapeless${SHAPELESS_VERSION}.jar"
APP_CLASS=com.flurdy.example.server.ServiceApplication

JAVA_OPTS="-Dconfig.file=${APP_CONFIG} -Dlogback.configurationFile=${APP_LOG_CONFIG} ${JAVA_OPTS}"

/usr/bin/java -cp ${APP_CLASSPATH} ${JAVA_OPTS} ${APP_CLASS}
