FROM flurdy/activator:1.3.2

MAINTAINER flurdy

ENV DEBIAN_FRONTEND noninteractive

ENV APPDIR /opt/app

RUN mkdir -p /etc/opt/app && \
  mkdir -p $HOME/.sbt/0.13 

ADD . /opt/app

WORKDIR /opt/app

RUN rm -rf /opt/app/target /opt/app/project/project /opt/app/project/target 

RUN cp /opt/app/conf/* /etc/opt/app/ && \
  cp /opt/app/conf/repositories $HOME/.sbt/ && \
  cp /opt/app/conf/local.sbt $HOME/.sbt/0.13/ 

RUN /usr/local/bin/activator stage

WORKDIR /opt/app

ENTRYPOINT ["/opt/app/target/universal/stage/bin/frontend"]


EXPOSE 9000
EXPOSE 9999
