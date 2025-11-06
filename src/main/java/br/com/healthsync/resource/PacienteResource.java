package br.com.healthsync.resource;

import br.com.healthsync.dao.PacienteDAO;
import br.com.healthsync.model.Paciente;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/pacientes")
public class PacienteResource {

    private final PacienteDAO pacienteDAO = new PacienteDAO();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Paciente> getAllPacientes() {
        return pacienteDAO.readAll();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPacienteById(@PathParam("id") Long id) {
        Paciente paciente = pacienteDAO.readById(id);
        if (paciente != null) {
            return Response.ok(paciente).build();
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("mensagem", "Paciente com o ID " + id + " não foi encontrado.");
            return Response.status(Response.Status.NOT_FOUND)
                         .entity(errorResponse)
                         .build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPaciente(Paciente paciente) {
        pacienteDAO.create(paciente);
        return Response.status(Response.Status.CREATED).entity(paciente).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePaciente(@PathParam("id") Long id, Paciente paciente) {
        paciente.setId(id);
        pacienteDAO.update(paciente);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response deletePaciente(@PathParam("id") Long id) {
        pacienteDAO.delete(id);
        return Response.noContent().build();
    }
}
