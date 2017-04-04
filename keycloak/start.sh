wget -c http://repo2.maven.org/maven2/org/wildfly/swarm/servers/keycloak/2017.3.3/keycloak-2017.3.3-swarm.jar
java -jar -Dswarm.port.offset=3 keycloak-2017.3.3-swarm.jar