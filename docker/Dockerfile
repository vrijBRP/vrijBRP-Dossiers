ARG SOURCE

FROM $SOURCE

COPY jar/target/dossiers.jar app.jar
COPY docker/initclient.sh /initclient.sh

RUN chmod a+rx /initclient.sh

EXPOSE 8080

CMD [ "java", "-jar", "app.jar" ]
