#!/bin/bash

# time to create the routemaster properties file

gradle -b build.h2zero.sqlite3.gradle clean h2zero
gradle build shadow pTML

cat routemaster.orig.properties > routemaster.properties
cat src/test/resources/routemaster.rest.properties >> routemaster.properties

java -jar routemaster-rest-testing.jar \
     -h localhost \
     -p 5474 \
     -d . \
     -q false
