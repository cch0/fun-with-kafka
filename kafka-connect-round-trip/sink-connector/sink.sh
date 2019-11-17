#!/usr/bin/env bash
export CLASSPATH="$(find target/ -type f -name '*.jar'| grep '\-package' | tr '\n' ':')"

$KAFKA_HOME/bin/connect-standalone.sh config/worker.properties config/sink.properties