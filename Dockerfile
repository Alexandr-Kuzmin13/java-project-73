FROM gradle:7.4-jdk17

WORKDIR /

COPY . /

RUN gradle installDist

CMD ./build/install/java-project-73/bin/java-project-73