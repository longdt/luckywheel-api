FROM openjdk:11

VOLUME /tmp

ADD build/libs/*.jar /app.jar

# Add entrypoint.sh script
ADD entrypoint.sh /entrypoint.sh

RUN chmod +x /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]
