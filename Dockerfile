# syntax=docker/dockerfile:1
FROM openjdk:11-jdk-slim

LABEL maintainer="SA"
LABEL purpose="Flink + Spring Boot base image with preloaded Maven dependencies"

ENV MAVEN_VERSION=3.9.9
ENV MAVEN_HOME=/usr/share/maven
ENV PATH=$MAVEN_HOME/bin:$PATH
ENV JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64

FROM openjdk:11-jdk-slim

ENV MAVEN_VERSION=3.9.9
ENV MAVEN_HOME=/usr/share/maven
ENV PATH=$MAVEN_HOME/bin:$PATH

# These will come from .env
ARG ARTIFACTORY_USER
ARG ARTIFACTORY_PASS
ARG ARTIFACTORY_URL

ENV ARTIFACTORY_USER=$ARTIFACTORY_USER
ENV ARTIFACTORY_PASS=$ARTIFACTORY_PASS
ENV ARTIFACTORY_URL=$ARTIFACTORY_URL

# Install base tools
RUN apt-get update && apt-get install -y --no-install-recommends \
    git curl unzip ca-certificates && \
    rm -rf /var/lib/apt/lists/*

# Install Maven manually (we don’t want apt layers)
RUN curl -fsSL https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
    -o /tmp/maven.tar.gz && \
    mkdir -p /usr/share && \
    tar -xzf /tmp/maven.tar.gz -C /usr/share && \
    mv /usr/share/apache-maven-${MAVEN_VERSION} /usr/share/maven && \
    rm /tmp/maven.tar.gz

# Copy Maven settings for Artifactory
COPY settings.xml /usr/share/maven/conf/settings.xml

# Create workspace and pre-fetch dependencies
WORKDIR /workspace

# Copy only pom.xml first for dependency resolution
COPY pom.xml .

# Pre-download all Maven dependencies into /root/.m2
RUN mvn dependency:go-offline -s /usr/share/maven/conf/settings.xml

# Optional: verify dependencies (ensures tests’ libs also cached)
RUN mvn test-compile -s /usr/share/maven/conf/settings.xml

# Cleanup optional build cache
RUN rm -rf /root/.m2/repository/org/skaria/example/flink/somerecord-to-command

# Set working directory for CI pipelines
WORKDIR /app

CMD ["mvn", "-v"]
