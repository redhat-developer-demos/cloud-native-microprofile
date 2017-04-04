package com.redhat.developers;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.wildfly.swarm.topology.Advertise;

@ApplicationScoped
@Path("/protected")
@Advertise("protected-service")
public class ProtectedEndpoint {

	@Context
	private SecurityContext securityContext;

	@GET
	@Produces("text/plain")
	public Response doGet() {
		// this will set the user id as userName
		String userName = securityContext.getUserPrincipal().getName();

		if (securityContext.getUserPrincipal() instanceof KeycloakPrincipal) {
			@SuppressWarnings("unchecked")
			KeycloakPrincipal<KeycloakSecurityContext> kp = (KeycloakPrincipal<KeycloakSecurityContext>) securityContext
					.getUserPrincipal();

			// this is how to get the real userName (or rather the login name)
			userName = kp.getKeycloakSecurityContext().getToken().getName();
		}
		return Response.ok("This is a Secured resource. You are logged as " + userName).build();
	}


}