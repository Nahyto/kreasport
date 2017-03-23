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
    private String key;
    private int id = 0;
    private String balise = "";

    public Parcours(int id, String name) throws SQLException {
        this.id = id;
        this.name = name;
    }

    public Parcours(int id, String name, String key) throws SQLException {
        this.id = id;
        this.name = name;
        this.key = key;
    }
    
  
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
        return name.equals(user.name) && key.equals(user.key);
    }

    @Override
    public String toString() {
        return id + ": " + key + ", " + name +","+balise;
    }

    public String getkey() {
        return key;
    }

    public void setkey(String key) {
        this.key = key;
    }

  

    

  

   /* public boolean isInUserGroup() {
        return !(id == anonymous.getId());
    }*/

  /*  public boolean isAnonymous() {
        return this.getId() == getAnonymousUser().getId();
    }*/

   

   

    public void initFromDto(ParcoursDto dto) {
        this.setkey(dto.getkey());
        this.setId(dto.getId());
        this.setName(dto.getName());
        this.setBalise(dto.getBalise());
    }

    public ParcoursDto convertToDto() {
    	ParcoursDto dto = new ParcoursDto();
        dto.setkey(this.getkey());
        dto.setId(this.getId());
        dto.setName(this.getName());
        dto.setBalise(this.getBalise());
        return dto;
    }
}
