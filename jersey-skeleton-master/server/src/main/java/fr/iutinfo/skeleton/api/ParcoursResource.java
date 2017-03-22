package fr.iutinfo.skeleton.api;

import fr.iutinfo.skeleton.common.dto.ParcoursDto;
import fr.iutinfo.skeleton.common.dto.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static fr.iutinfo.skeleton.api.BDDFactory.getDbi;
import static fr.iutinfo.skeleton.api.BDDFactory.tableExist;

@Path("/parcours")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ParcoursResource {
    final static Logger logger = LoggerFactory.getLogger(ParcoursResource.class);
    private static ParcoursDao dao = getDbi().open(ParcoursDao.class);

    public ParcoursResource() throws SQLException {
        if (!tableExist("parcours")) {
            logger.debug("Crate table parcours");
            dao.createParcoursTable();
            dao.insert(new Parcours(0, "Margaret Thatcher", "la Dame de fer"));
        }
    }
    
    /*public static void main(String[] args) throws SQLException {
		ParcoursResource p = new ParcoursResource();
		dao.insert(new Parcours(42, "lojkll", "mdr"));
		System.out.println(dao.all());
	}*/

    @POST
    public ParcoursDto createParcours(ParcoursDto dto) {
        Parcours user = new Parcours();
        user.initFromDto(dto);
        int id = dao.insert(user);
        dto.setId(id);
        return dto;
    }

    @GET
    @Path("/{name}")
    public UserDto getParcours(@PathParam("name") String name) {
        Parcours user = dao.findByName(name);
        if (user == null) {
            throw new WebApplicationException(404);
        }
        return user.convertToDto();
    }

    @GET
    public List<UserDto> getAllParcours(@QueryParam("q") String query) {
        List<Parcours> users;
        if (query == null) {
            users = dao.all();
        } else {
            logger.debug("Search users with query: " + query);
            users = dao.search("%" + query + "%");
        }
        return users.stream().map(Parcours::convertToDto).collect(Collectors.toList());
    }

    @DELETE
    @Path("/{id}")
    public void deleteUser(@PathParam("id") int id) {
        dao.delete(id);
    }

}
