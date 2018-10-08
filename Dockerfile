FROM openjdk:11

VOLUME /tmp

ADD build/libs/*-all.jar /app.jar
ADD conf  /conf

# Add entrypoint.sh script
ADD entrypoint.sh /entrypoint.sh

RUN chmod +x /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]
