FROM eclipse-temurin:19

ENV LANGUAGE='en_US:en'

# We make four distinct layers so if there are application changes the library layers can be re-used
COPY target/quarkus-app/lib/ /deployments/lib/
COPY target/quarkus-app/*.jar /deployments/
COPY target/quarkus-app/app/ /deployments/app/
COPY target/quarkus-app/quarkus/ /deployments/quarkus/

EXPOSE 8080

CMD ["java", "--enable-preview", "-Djdk.tracePinnedThreads=full", "-XX:StartFlightRecording=dumponexit=true,filename=/home/jfr/", "-jar", "/deployments/quarkus-run.jar"]

