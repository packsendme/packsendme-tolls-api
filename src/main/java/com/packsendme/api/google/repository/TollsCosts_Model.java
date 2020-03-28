package com.packsendme.api.google.repository;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "tolls_country_pks")
@Getter
@Setter
public class TollsCosts_Model implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String name_country;
	public Double toll_cost;
	public String currency_cost;
	public String currency_desc;
	public boolean status;
	
}
