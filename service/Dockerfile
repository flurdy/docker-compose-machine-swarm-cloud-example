FROM flurdy/activator:latest

MAINTAINER flurdy

ENV DEBIAN_FRONTEND noninteractive

ENV APPDIR /var/local/application

ADD repositories /root/.sbt/

ADD . /var/local/application

WORKDIR /var/local/application

ENTRYPOINT ["/usr/local/bin/activator"]

EXPOSE 8880
