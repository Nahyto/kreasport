package fr.iutinfo.skeleton.api;

import java.security.Principal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.iutinfo.skeleton.common.dto.BaliseDto;
import fr.iutinfo.skeleton.common.dto.ParcoursDto;

public class Parcours implements Principal {
	final static Logger logger = LoggerFactory.getLogger(Parcours.class);
	// private static Parcours anonymous = new Parcours(-1, "Anonymous", "anonym");
	private String name;
	private String key;
	private int id = 0;
	private String balise = "";
	private List<Balise> baliseList;
	private String description = "";

	public Parcours(int id, String name) throws SQLException {
		this.id = id;
		this.name = name;
		baliseList = new ArrayList<>();
	}

	public Parcours(int id, String name, String key) throws SQLException {
		this(id, name);
		this.key = key;
	}

	public Parcours() {
	}

	public String getDescription(){
		return description;
	}

	public void setDescription(String desc){
		this.description = desc;
	}


	public String getBalise() {
		return balise;
	}

	public void setBalise(String b) {
		balise = b;
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



	/**
	 * @return the baliseList
	 */
	public List<Balise> getBaliseList() {
		return baliseList;
	}

	/**
	 * @param baliseList the baliseList to set
	 */
	public void setBaliseList(List<Balise> baliseList) {
		this.baliseList = baliseList;
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
		return id + ": " + key + ", " + name + "," + description;
	}

	public String getkey() {
		return key;
	}

	public void setkey(String key) {
		this.key = key;
	}







	/*
	 * public boolean isInUserGroup() { return !(id == anonymous.getId()); }
	 */

	/*
	 * public boolean isAnonymous() { return this.getId() == getAnonymousUser().getId(); }
	 */





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
		if (baliseList == null) {
			baliseList = new ArrayList<>();
		}
		dto.setBaliseDtoList(convertBaliseListToDtoList(baliseList));
		return dto;
	}

	public List<BaliseDto> convertBaliseListToDtoList(List<Balise> baliseList) {
		List<BaliseDto> baliseDtoList = new ArrayList<>();
		for (Balise balise : baliseList) {
			BaliseDto baliseDto = balise.convertToDto();
			baliseDtoList.add(baliseDto);
		}
		return baliseDtoList;
	}
}
