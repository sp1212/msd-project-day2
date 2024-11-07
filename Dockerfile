FROM adoptopenjdk/openjdk11
EXPOSE 8080
RUN mkdir /app
COPY build/libs/msd-project-day2-0.0.1-SNAPSHOT.jar /app/data.jar

ENV APIHOST=172.17.0.1:8080
# ENV AUTHHOST=http://authsrv:8081
ENTRYPOINT ["java","-jar","/app/data.jar"]
