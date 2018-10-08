#!/bin/bash

set -e
# dev/stag/prod
set -e
exec java -jar $JAVA_OPTS /app.jar
