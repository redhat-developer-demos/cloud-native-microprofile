package com.redhat.developers;

import java.io.File;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.wildfly.swarm.health.Health;
import org.wildfly.swarm.health.HealthStatus;

@Path("/myapp")
public class HealthEndpoint {

	@GET
	@Path("/diskspace")
	@Health
	public HealthStatus checkDiskspace() {
		HealthStatus diskStatus = HealthStatus.named("diskSpace");
		File path = new File(".");
		long freeBytes = path.getFreeSpace();
		long threshold = 1024 * 1024 * 100; // 100mb
		return freeBytes > threshold ? diskStatus.up() : diskStatus.down().withAttribute("freebytes", freeBytes);
	}

	@GET
	@Path("/db")
	@Health
	public HealthStatus checkDatabaseConnection() {
		//Logic to check connection with DB
		return HealthStatus.named("db").up();
	}

}
