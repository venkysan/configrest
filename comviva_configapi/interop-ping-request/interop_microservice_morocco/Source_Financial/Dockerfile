FROM ${REGISTRY_URL}openjdk:8u212-jre-alpine3.9
EXPOSE 7080
WORKDIR /opt/application/interop
COPY InteropHpsTxn-G1R3.jar InteropHpsTxn-G1R3.jar
USER 10001
ENTRYPOINT exec java $JAVA_OPTS -jar /opt/application/interop/InteropHpsTxn-G1R3.jar --spring.config.additional-location=file:////opt/application/common/application.properties --spring.cloud.config.enabled=false
