FROM openjdk:11

VOLUME /tmp

ADD build/libs/*-all.jar /app.jar
ADD conf  /conf

EXPOSE 8080/tcp

# Add entrypoint.sh script
ADD entrypoint.sh /entrypoint.sh

RUN chmod +x /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]
