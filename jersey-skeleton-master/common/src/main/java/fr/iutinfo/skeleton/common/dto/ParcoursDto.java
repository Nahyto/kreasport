package fr.iutinfo.skeleton.common.dto;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParcoursDto implements Principal {
    final static Logger logger = LoggerFactory.getLogger(ParcoursDto.class);
    private String name;
    private String key;
    private String balise;
	private List<BaliseDto> baliseDtoList;
    private int id = 0;

	public ParcoursDto() {
		baliseDtoList = new ArrayList<>();
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

	public List<BaliseDto> getBaliseDtoList() {
		return baliseDtoList;
	}

	public void setBaliseDtoList(List<BaliseDto> baliseDtoList) {
		this.baliseDtoList = baliseDtoList;
	}

}
