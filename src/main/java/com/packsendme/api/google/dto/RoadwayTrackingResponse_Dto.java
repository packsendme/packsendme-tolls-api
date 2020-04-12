package com.packsendme.api.google.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoadwayTrackingResponse_Dto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String name_country;
	public int toll_amount;
	public Double toll_price;
	public Double country_distance;
	public Double fuel_price;

	public String currency_price;
	public String unity_measurement_distance;
	
	public RoadwayTrackingResponse_Dto(String name_country, int toll_amount, Double toll_price, Double country_distance,
			Double fuel_price, String currency_price, String unity_measurement_distance) {
		super();
		this.name_country = name_country;
		this.toll_amount = toll_amount;
		this.toll_price = toll_price;
		this.country_distance = country_distance;
		this.fuel_price = fuel_price;
		this.currency_price = currency_price;
		this.unity_measurement_distance = unity_measurement_distance;
	}
	
	

	

	
}
