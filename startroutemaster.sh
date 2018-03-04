#!/bin/bash

# time to create the routemaster properties file

gradle build shadow pTML; gradle -b build.h2zero.sqlite3.gradle h2zero


cat routemaster.orig.properties > routemaster.properties
cat src/test/resources/routemaster.rest.properties >> routemaster.properties

java -jar routemaster-rest-testing.jar \
     -h localhost \
     -p 5474 \
     -d . \
     -q false

rm src/test/sqlite-db/h2zero.sqlite3.db

echo ".read src/test/resources/create-database-sqlite3.sql" | sqlite3 src/test/sqlite-db/h2zero.sqlite3.db
