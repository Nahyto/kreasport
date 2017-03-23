package fr.iutinfo.skeleton.api;

import fr.iutinfo.skeleton.common.dto.BaliseDto;
import fr.iutinfo.skeleton.common.dto.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static fr.iutinfo.skeleton.api.BDDFactory.getDbi;
import static fr.iutinfo.skeleton.api.BDDFactory.tableExist;

@Path("/balise")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BaliseResource {
    final static Logger logger = LoggerFactory.getLogger(BaliseResource.class);
    private static BaliseDAO dao = getDbi().open(BaliseDAO.class);

    public BaliseResource() throws SQLException {
        if (!tableExist("balise")) {
            logger.debug("Crate table parcours");
            dao.createBaliseTable();
            //dao.insert(new Balise(0, 25.25, 15.15,1));
        }
    }
    
    public static void main(String[] args) throws SQLException {
    	/*if (tableExist("balise")) {
			dao.dropBaliseTable();
		}*/
		BaliseResource p = new BaliseResource();
		//dao.delete(1);dao.delete(2);
		dao.insert(new Balise(1,5465464.65465464,4654464.454654654,1));
		dao.insert(new Balise(1, 56454646.654654654, 6546546546460.4654654456, 1));
		p.remplirB(1);
		//System.out.println(dao.findById(1));
		System.out.println(dao.all());
		
	}
    
    public void remplirB(int id) throws SQLException{
    	Connection con = null;
    	String balise = "";
		try{  
			Class.forName("org.sqlite.JDBC");
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}
		try{
			con = DriverManager.getConnection("jdbc:sqlite:" + System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "data.db");
		}catch(Exception e){
			e.printStackTrace();
		}
		ResultSet rs = null;
		try{
			Statement statement = con.createStatement();
			rs = statement.executeQuery("select * from balise where parcours = (Select id from parcours where id = "+id+");");
				while (rs.next()) {
					System.out.println("lol");
					balise+= " "+rs.getInt("id");
				}
			rs.close();
			System.out.println(balise);
			Statement statement1 = con.createStatement();
			statement1.executeUpdate("update parcours SET balise = '"+balise+"' where id = "+id);
			System.out.println("update parcours SET balise = '"+balise+"' where id = "+id);
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			con.close();
		}
	
		System.out.println(balise);

		//dao.updateBalise(id, balise);
    }

    @POST
    public BaliseDto createBalise(BaliseDto dto) {
        Balise user = new Balise();
        user.initFromDto(dto);
        int id = dao.insert(user);
        dto.setId(id);
        return dto;
    }

    @GET
    @Path("/{name}")
    public BaliseDto getBalise(@PathParam("name") String name) {
        Balise user = dao.findByName(name);
        if (user == null) {
            throw new WebApplicationException(404);
        }
        return user.convertToDto();
    }

    @GET
    public List<BaliseDto> getAllBalise(@QueryParam("q") String query) {
        List<Balise> users;
        if (query == null) {
            users = dao.all();
        } else {
            logger.debug("Search users with query: " + query);
            users = dao.search("%" + query + "%");
        }
        return users.stream().map(Balise::convertToDto).collect(Collectors.toList());
    }

    @DELETE
    @Path("/{id}")
    public void deleteUser(@PathParam("id") int id) {
        dao.delete(id);
    }

}
