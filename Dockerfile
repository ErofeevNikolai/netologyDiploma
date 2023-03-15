FROM azul/zulu-openjdk-alpine:11-jre

EXPOSE 5050

COPY target/netologyDiploma-0.0.1-SNAPSHOT.jar diplom.jar

ENTRYPOINT ["java","-jar","diplom.jar"]