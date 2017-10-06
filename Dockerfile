# Dockerfile

FROM java:8

MAINTAINER me@nalbam.com

RUN curl -s toast.sh/install | bash
RUN ~/toaster/toast.sh config eb local nalbam
RUN ~/toaster/toast.sh init java
RUN ~/toaster/toast.sh init eb

ENV PATH "/usr/local/java/bin:${PATH}"

ENV JAVA_HOME /usr/local/java

EXPOSE 8080

ADD ROOT.jar /

CMD ["java", "-jar", "/ROOT.jar"]
