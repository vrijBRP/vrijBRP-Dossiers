FROM amazoncorretto:11-alpine
ARG jar

ENV LANG nl_NL.UTF-8
ENV LANGUAGE nl_NL:nl
ENV LC_ALL nl_NL.UTF-8
ENV JAVA_TOOL_OPTIONS -Dfile.encoding=UTF8 -Duser.language=nl -Duser.country=NL \
    -Dlog4j2.formatMsgNoLookups=true -Djava.security.egd=file:/dev/./urandom
ENV TZ Europe/Amsterdam

COPY ${jar} "app.jar"
COPY initclient.sh /initclient.sh
RUN chmod a+rx /initclient.sh && apk add --no-cache tzdata

EXPOSE 8080

CMD [ "java", "-jar", "app.jar" ]
