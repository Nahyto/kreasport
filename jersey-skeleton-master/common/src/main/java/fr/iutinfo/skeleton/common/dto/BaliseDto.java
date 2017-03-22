package fr.iutinfo.skeleton.common.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;

public class BaliseDto implements Principal {
    final static Logger logger = LoggerFactory.getLogger(BaliseDto.class);
    private double longitude;
    private double latitude;
    private int parcours;
    private int id = 0;

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
    
}

