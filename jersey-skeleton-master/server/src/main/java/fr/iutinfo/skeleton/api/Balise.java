package fr.iutinfo.skeleton.api;

import com.google.common.base.Charsets;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

import fr.iutinfo.skeleton.common.dto.BaliseDto;
import fr.iutinfo.skeleton.common.dto.BaliseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.security.SecureRandom;

public class Balise implements Principal {
    final static Logger logger = LoggerFactory.getLogger(Balise.class);
    private double longitude;
    private double latitude;
    private int parcours;
    private int id = 0;
    private String description = "";

    public Balise(int id, double longitude, double latitude, String description, int parcours) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.parcours = parcours;
        this.description = description;
    }
    public Balise() {
    }

 /*   public static Balise getAnonymousBalise() {
        return anonymous;
    }*/
    
    public String getDescription(){
    	return description;
    }
    
    public void setDescription(String desc){
    	this.description = desc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public int getParcours() {
        return parcours;
    }

    public void setParcours(int parcours) {
        this.parcours = parcours;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


  


    @Override
    public boolean equals(Object arg) {
        if (getClass() != arg.getClass())
            return false;
        Balise user = (Balise) arg;
        return (longitude == user.longitude) && (latitude == user.latitude);
    }

    @Override
    public String toString() {
        return id + ": " + longitude + ", " + latitude +","+parcours;
    }

   

  

    

  

   /* public boolean isInBaliseGroup() {
        return !(id == anonymous.getId());
    }*/

  /*  public boolean isAnonymous() {
        return this.getId() == getAnonymousBalise().getId();
    }*/

   

   

    public void initFromDto(BaliseDto dto) {
        this.setLatitude(dto.getLatitude());
        this.setId(dto.getId());
        this.setLongitude(dto.getLongitude());
        this.setParcours(dto.getParcours());
        this.setDescription(dto.getDescription());
    }

    public BaliseDto convertToDto() {
        BaliseDto dto = new BaliseDto();
        dto.setId(this.getId());
        dto.setLatitude(this.getLatitude());
        dto.setLongitude(this.getLongitude());
        dto.setParcours(this.getParcours());
        dto.setDescription(this.getDescription());
        return dto;
    }
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
}
