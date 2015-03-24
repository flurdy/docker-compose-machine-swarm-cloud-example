FROM flurdy/activator:1.3.2

MAINTAINER flurdy

ENV DEBIAN_FRONTEND noninteractive

ENV APPDIR /opt/app

RUN mkdir -p /etc/opt/app && \
  mkdir -p $HOME/.sbt/0.13 

ADD . /opt/app

WORKDIR /opt/app

RUN rm -rf /opt/app/target /opt/app/project/project /opt/app/project/target 

RUN cp /opt/app/src/main/resources/* /etc/opt/app/ && \
  cp /opt/app/src/main/resources/repositories $HOME/.sbt/ && \
  cp /opt/app/src/main/resources/local.sbt $HOME/.sbt/0.13 && \
  chmod +x /opt/app/src/main/resources/start.sh 

RUN /usr/local/bin/activator assembly 

ENTRYPOINT ["/opt/app/src/main/resources/start.sh"]

CMD "-Dconfig.file=/etc/opt/app/docker.conf"

EXPOSE 8880
