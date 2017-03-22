package fr.iutinfo.skeleton.api;

import java.security.Principal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.iutinfo.skeleton.common.dto.ParcoursDto;

public class Parcours implements Principal {
    final static Logger logger = LoggerFactory.getLogger(Parcours.class);
    //private static Parcours anonymous = new Parcours(-1, "Anonymous", "anonym");
    private String name;
    private String alias;
    private int id = 0;
    private String balise;

    public Parcours(int id, String name) throws SQLException {
        this.id = id;
        this.name = name;
    }

    public Parcours(int id, String name, String alias) throws SQLException {
        this.id = id;
        this.name = name;
        this.alias = alias;
    }
    
   /* public void remplirB() throws SQLException{
    	Connection con = null;
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
		String[] liste = null;
		ResultSet rs = null, rs1 = null;
		try{
			Statement statement = con.createStatement();
			rs1 = statement.executeQuery("select * from balise where parcours = (Select id from parcours where name = "+name+");");
				while (rs.next()) {
					balise += rs.getString("id");
				}
			rs1.close();
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			con.close();
		}
    }*/
    
    public Parcours() {
    }

    public String getBalise(){
    	return balise;
    }
    
    public void setBalise(String b){
    	balise += " "+b;
    }
    


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



  


    @Override
    public boolean equals(Object arg) {
        if (getClass() != arg.getClass())
            return false;
        Parcours user = (Parcours) arg;
        return name.equals(user.name) && alias.equals(user.alias);
    }

    @Override
    public String toString() {
        return id + ": " + alias + ", " + name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

  

    

  

   /* public boolean isInUserGroup() {
        return !(id == anonymous.getId());
    }*/

  /*  public boolean isAnonymous() {
        return this.getId() == getAnonymousUser().getId();
    }*/

   

   

    public void initFromDto(ParcoursDto dto) {
        this.setAlias(dto.getAlias());
        this.setId(dto.getId());
        this.setName(dto.getName());
        this.setBalise(dto.getBalise());
    }

    public ParcoursDto convertToDto() {
    	ParcoursDto dto = new ParcoursDto();
        dto.setAlias(this.getAlias());
        dto.setId(this.getId());
        dto.setName(this.getName());
        dto.setBalise(this.getBalise());
        return dto;
    }
}
