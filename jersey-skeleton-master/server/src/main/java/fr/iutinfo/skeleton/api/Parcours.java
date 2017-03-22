package fr.iutinfo.skeleton.api;

import com.google.common.base.Charsets;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

import fr.iutinfo.skeleton.common.dto.ParcoursDto;
import fr.iutinfo.skeleton.common.dto.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.security.SecureRandom;

public class Parcours implements Principal {
    final static Logger logger = LoggerFactory.getLogger(Parcours.class);
    //private static Parcours anonymous = new Parcours(-1, "Anonymous", "anonym");
    private String name;
    private String alias;
    private int id = 0;

    public Parcours(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Parcours(int id, String name, String alias) {
        this.id = id;
        this.name = name;
        this.alias = alias;
    }

    public Parcours() {
    }

 /*   public static Parcours getAnonymousUser() {
        return anonymous;
    }*/


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
    }

    public UserDto convertToDto() {
        UserDto dto = new UserDto();
        dto.setAlias(this.getAlias());
        dto.setId(this.getId());
        dto.setName(this.getName());
        return dto;
    }
}
