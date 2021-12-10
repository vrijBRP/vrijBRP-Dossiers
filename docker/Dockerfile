FROM adoptopenjdk/openjdk11:jdk-11.0.5_10-alpine
ARG jar

ENV LANG nl_NL.UTF-8
ENV LANGUAGE nl_NL:nl
ENV LC_ALL nl_NL.UTF-8
ENV JAVA_TOOL_OPTIONS -Dfile.encoding=UTF8
ENV TZ Europe/Amsterdam

COPY ${jar} "app.jar"
COPY initclient.sh /initclient.sh
RUN chmod a+rx /initclient.sh && apk add --no-cache tzdata

EXPOSE 8080

CMD [ \
  "java", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-Dlog4j2.formatMsgNoLookups=true", \
  "-jar","app.jar" \
]
