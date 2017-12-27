# extend base ubuntu
FROM ubuntu:xenial

MAINTAINER MindTickle, https://github.com/MindTickle

# Install Java.
RUN \
  apt-get update && \
  apt-get install -y software-properties-common && \
  echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | debconf-set-selections && \
  add-apt-repository -y ppa:webupd8team/java && \
  apt-get update && \
  apt-get install -y oracle-java8-installer && \
  rm -rf /var/lib/apt/lists/* && \
  rm -rf /var/cache/oracle-jdk8-installer

# Define commonly used JAVA_HOME variable
ENV JAVA_HOME /usr/lib/jvm/java-8-oracle

# ensure installation
RUN java -version

# install application
ARG port="80"
ENV PORT=$port
EXPOSE $PORT

ARG jvm_heap_size="512m"
ENV JVM_HEAP_SIZE=$jvm_heap_size

VOLUME /logs

COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh

ARG target="app_build"
COPY $target/annotation-avengers /app
WORKDIR /app

ARG env="local"
ENV ENV=$env

ENTRYPOINT ["/docker-entrypoint.sh"]