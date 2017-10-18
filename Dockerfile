# Dockerfile

FROM java:8

MAINTAINER me@nalbam.com

EXPOSE 8080

ADD ROOT.jar /

CMD ["java", "-jar", "/ROOT.jar"]
