FROM java:8
COPY . /usr/src/myapp
WORKDIR /usr/src/myapp
ENV CLASSPATH=".:json.jar:rabbitmq-client.jar:commons-io-1.2.jar:commons-cli-1.1.jar"
RUN javac Worker.java
CMD ["java", "Worker"]
