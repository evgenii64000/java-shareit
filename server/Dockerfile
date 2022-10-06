FROM amazoncorretto:11
COPY target/*.jar server.jar
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:9091
ENTRYPOINT ["java","-jar","/server.jar"]
