wget -c http://central.maven.org/maven2/com/netflix/hystrix/hystrix-dashboard/1.5.10/hystrix-dashboard-1.5.10.war
wget -c https://repo1.maven.org/maven2/org/wildfly/swarm/swarmtool/2017.3.3/swarmtool-2017.3.3-standalone.jar
java -jar swarmtool-2017.3.3-standalone.jar hystrix-dashboard-1.5.10.war
java -jar -Dswarm.port.offset=2 hystrix-dashboard-1.5.10-swarm.jar