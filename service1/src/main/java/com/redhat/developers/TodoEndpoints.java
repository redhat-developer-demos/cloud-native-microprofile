package com.redhat.developers;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.wildfly.swarm.topology.Advertise;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@ApplicationScoped
@Path("/todos")
@Api
@Advertise("service1")
public class TodoEndpoints {

	@PersistenceUnit
	private EntityManagerFactory emf;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@ApiOperation("Create a TODO item")
	public Response createEntry(Todo todo) {
		EntityManager entityManager = emf.createEntityManager();
		entityManager.persist(todo);
		return Response.ok(todo.getId()).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation("Retrieve all TODO items")
	public Response doGet() {
		EntityManager entityManager = emf.createEntityManager();
		return Response.ok(entityManager.createQuery("SELECT t FROM Todo t").getResultList()).build();
	}

	@DELETE
	@Path("/{id:[0-9][0-9]*}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation("Remove a specific TODO item")
	public Response deleteEntry(@PathParam("id") long id) {
		EntityManager entityManager = emf.createEntityManager();
		Todo todo = entityManager.find(Todo.class, id);
		entityManager.remove(todo);
		return Response.ok().build();
	}

}
