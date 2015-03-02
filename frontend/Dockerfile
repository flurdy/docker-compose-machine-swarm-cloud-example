FROM flurdy/activator:latest

MAINTAINER flurdy

ENV DEBIAN_FRONTEND noninteractive

ENV APPDIR /var/local/application

ADD repositories /root/.sbt/

ADD . /var/local/application

WORKDIR /var/local/application

RUN /usr/local/bin/activator stage

WORKDIR /var/local/application

ENTRYPOINT /var/local/application/target/universal/stage/bin/frontend

CMD "-Dconfig.file=/etc/opt/frontend/docker.conf"

EXPOSE 9000
EXPOSE 9999
