package fr.iutinfo.skeleton.api;

import static fr.iutinfo.skeleton.api.BDDFactory.getDbi;
import static fr.iutinfo.skeleton.api.BDDFactory.tableExist;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.iutinfo.skeleton.common.dto.ParcoursDto;

@Path("/parcours")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ParcoursResource {
    final static Logger logger = LoggerFactory.getLogger(ParcoursResource.class);
    private static ParcoursDao dao = getDbi().open(ParcoursDao.class);
    private static BaliseDAO daoBalise = getDbi().open(BaliseDAO.class);

    public ParcoursResource() throws SQLException {
        if (!tableExist("parcours")) {
            logger.debug("Crate table parcours");
            dao.createParcoursTable();
            dao.insert(new Parcours(0, "Margaret Thatcher", "la Dame de fer"));
        }
    }
    
    public static void main(String[] args) throws SQLException {
    	/*if (tableExist("parcours")) {
			dao.dropParcoursTable();
		}*/
		ParcoursResource p = new ParcoursResource();
		//dao.insert(new Parcours(1, "Chasse au trésor", "5648413254654dsfgsdfg"));
		System.out.println(dao.all());
	}
    
  
    

    @POST
    public ParcoursDto createParcours(ParcoursDto dto) {
        Parcours user = new Parcours();
        user.initFromDto(dto);
        int id = dao.insert(user);
        dto.setId(id);
        return dto;
    }
    
    @PUT
    @Path("/{id}")
    public void updateParcours(@PathParam("id") int id,ParcoursDto dto) throws SQLException {
        Parcours parcours = new Parcours();
        parcours.initFromDto(dto);
        dao.updateParcours(parcours.getName(), parcours.getkey(), id);
    }
    
    @GET
    @Path("/{id}")
    public ParcoursDto getParcoursId(@PathParam("id") int id) {
        Parcours user = dao.findById(id);
        if (user == null) {
            throw new WebApplicationException(404);
        }
        return user.convertToDto();
    }
    

    @GET
    public List<ParcoursDto> getAllParcours(@QueryParam("q") String query) {
        List<Parcours> parcoursList;
        if (query == null) {
			parcoursList = dao.all();
			for (Parcours parcours : parcoursList) {
				parcours.setBaliseList(daoBalise.allFromParcours(parcours.getId()));
			}
        } else {
            logger.debug("Search users with query: " + query);
            parcoursList = dao.search("%" + query + "%");
        }
        return parcoursList.stream().map(Parcours::convertToDto).collect(Collectors.toList());
    }
    
    @GET
    @Path("/balise/{id}")
    public List<Balise> getAllBaliseParcours(@PathParam("id") int id){
    	return dao.getBalisebyId(id);
    }

    @DELETE
    public void deleteUser(@PathParam("id") int id) {
        dao.delete(id);
    }

}
