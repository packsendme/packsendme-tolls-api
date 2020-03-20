package com.packsendme.api.tolls.dto;

import java.io.Serializable;

public class CountryDto implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String idcountry;
	private String codcountry;
	private String namecountry;
	private String nameimagecountry;
	private String formatnumbercountry;
	
	public String getCodcountry() {
		return codcountry;
	}
	public void setCodcountry(String codcountry) {
		this.codcountry = codcountry;
	}
	public String getNamecountry() {
		return namecountry;
	}
	public void setNamecountry(String namecountry) {
		this.namecountry = namecountry;
	}
	public String getNameimagecountry() {
		return nameimagecountry;
	}
	public void setNameimagecountry(String nameimagecountry) {
		this.nameimagecountry = nameimagecountry;
	}
	public String getFormatnumbercountry() {
		return formatnumbercountry;
	}
	public void setFormatnumbercountry(String formatnumbercountry) {
		this.formatnumbercountry = formatnumbercountry;
	}
	public String getIdcountry() {
		return idcountry;
	}
	public void setIdcountry(String idcountry) {
		this.idcountry = idcountry;
	}
	
	

 
	
	

}
