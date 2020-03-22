package com.packsendme.api.tolls.repository;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "tolls_country_pks")
@Getter
@Setter
public class TollsCountry_Model implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	public int id;
	public String name_country;
	public Double toll_cost;
	public String currency_cost;
	public String currency_desc;
	public String status;
	
	
	
	
	public TollsCountry_Model() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName_country() {
		return name_country;
	}
	public void setName_country(String name_country) {
		this.name_country = name_country;
	}
	public Double getToll_cost() {
		return toll_cost;
	}
	public void setToll_cost(Double toll_cost) {
		this.toll_cost = toll_cost;
	}
	public String getCurrency_cost() {
		return currency_cost;
	}
	public void setCurrency_cost(String currency_cost) {
		this.currency_cost = currency_cost;
	}
	public String getCurrency_desc() {
		return currency_desc;
	}
	public void setCurrency_desc(String currency_desc) {
		this.currency_desc = currency_desc;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	
}
