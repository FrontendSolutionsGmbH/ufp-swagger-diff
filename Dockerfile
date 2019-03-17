
FROM openjdk:8-slim
WORKDIR /
COPY ./target/swagger-diff-1.0-SNAPSHOT.jar  /swaggervalidator-1.0-SNAPSHOT.jar
RUN mkdir /ufp-swagger

RUN mkdir /ufp-swagger-diff
RUN mkdir /ufp-swagger-diff/output
RUN mkdir /ufp-swagger-diff/input
VOLUME /ufp-swagger-diff/output
VOLUME /ufp-swagger-diff/input

CMD ["java","-jar","swaggervalidator-1.0-SNAPSHOT.jar"]




