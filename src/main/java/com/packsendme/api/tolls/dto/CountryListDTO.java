package com.packsendme.api.tolls.dto;

import java.io.Serializable;
import java.util.List;

public class CountryListDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public List<?> countries;

	public List<?> getCountries() {
		return countries;
	}

	public void setCountries(List<?> countries) {
		this.countries = countries;
	}
	
}
