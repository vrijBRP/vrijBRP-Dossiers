#!/usr/bin/env sh
#
# Create OAuth clients with given scope.
#
# E.g. Create OAuth client with api scope with docker-compose:
# docker-compose exec dossiers /initclient.sh api

java -classpath app.jar -Dloader.main=nl.procura.burgerzaken.dossiers.oauth.InitClient org.springframework.boot.loader.PropertiesLauncher "$@"
