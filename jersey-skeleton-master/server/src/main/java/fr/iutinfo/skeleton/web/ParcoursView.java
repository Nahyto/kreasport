package fr.iutinfo.skeleton.web;


import fr.iutinfo.skeleton.api.BDDFactory;
import fr.iutinfo.skeleton.api.Parcours;
import fr.iutinfo.skeleton.api.ParcoursDao;

import org.glassfish.jersey.server.mvc.Template;
import fr.iutinfo.skeleton.api.Parcours;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/parcours")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.TEXT_HTML)
public class ParcoursView {
    private static ParcoursDao dao = BDDFactory.getDbi().open(ParcoursDao.class);

    @GET
    @Template
    public List<Parcours> getAll() {
        return dao.all();
    }

    @GET
    @Template(name = "detail")
    @Path("/{id}")
    public Parcours getDetail(@PathParam("id") String id) {
        Parcours user = null;
            user = dao.findById(Integer.parseInt(id));
        if (user == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return user;
    }

}

