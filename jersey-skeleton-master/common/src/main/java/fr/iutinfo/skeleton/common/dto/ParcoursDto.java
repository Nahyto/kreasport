package fr.iutinfo.skeleton.common.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;

public class ParcoursDto implements Principal {
    final static Logger logger = LoggerFactory.getLogger(ParcoursDto.class);
    private String name;
    private String key;
    private String balise;
    private int id = 0;

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
    
    public String getBalise() {
        return balise;
    }

    public void setBalise(String balise) {
        this.balise += " "+balise;
    }

    public String getkey() {
        return key;
    }

    public void setkey(String key) {
        this.key = key;
    }

}
