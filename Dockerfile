FROM adoptopenjdk:11-jre-hotspot
RUN mkdir /opt/app
COPY target/PostgresHighload-1.0-SNAPSHOT-jar-with-dependencies.jar /opt/app/japp.jar
CMD ["java", "-jar", "/opt/app/japp.jar"]