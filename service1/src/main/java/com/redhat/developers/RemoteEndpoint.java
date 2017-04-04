package com.redhat.developers;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.wildfly.swarm.topology.Topology;
import org.wildfly.swarm.topology.Topology.Entry;

import feign.RequestLine;
import feign.hystrix.HystrixFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@ApplicationScoped
@Path("/remote")
@Api
public class RemoteEndpoint {

	@Context
	private SecurityContext securityContext;

	interface HelloApi {

		@RequestLine("GET /hello")
		public String hello();
	}

	interface SecureAPI {

		@RequestLine("GET /protected")
		public String securedResource();
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@ApiOperation("Return a remote endpoint")
	@Path("/invoke")
	public Response invoke() throws NamingException {
		List<Entry> topology = Topology.lookup().asMap().get("service2");
		String url = "http://localhost:9000";
		if (topology != null) {
			Entry helloService = topology.get(0);
			url = "http://" + helloService.getAddress() + ":" + helloService.getPort();
		}

		String helloResponse = HystrixFeign.builder().target(HelloApi.class, url, () -> "Hello fallback mesage")
				.hello();
		return Response.ok("I'm service1 and this is the response from service2: " + helloResponse).build();
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@ApiOperation("Return a Secured remote endpoint")
	@Path("/invoke-protected")
	public Response invokeSecure() throws NamingException {
		List<Entry> topology = Topology.lookup().asMap().get("protected-service");
		String url = "http://localhost:9000";
		if (topology != null) {
			Entry protectedService = topology.get(0);
			url = "http://" + protectedService.getAddress() + ":" + protectedService.getPort();
		}
		String accessToken = null;
		if (securityContext.getUserPrincipal() instanceof KeycloakPrincipal) {
			@SuppressWarnings("unchecked")
			KeycloakPrincipal<KeycloakSecurityContext> kp = (KeycloakPrincipal<KeycloakSecurityContext>) securityContext.getUserPrincipal();
			accessToken = kp.getKeycloakSecurityContext().getTokenString();
		}
		String securedResponse = HystrixFeign.builder()
				.requestInterceptor(new OAuthRequestInterceptor(accessToken))
				.target(
						SecureAPI.class, 
						url,
						() -> "Secured endpoint fallback mesage")
				.securedResource();
		return Response.ok("I'm service1 and this is the response from service2: " + securedResponse).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation("Return the current Topology")
	@Path("/topology")
	public Response topology() throws NamingException {
		return Response.ok(Topology.lookup().asMap()).build();
	}

}
