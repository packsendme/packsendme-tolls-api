package com.packsendme.api.google.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class SimulationRequestGoogle_Dto implements Serializable {
	
	/**
	 * 
	 */
	public static final long serialVersionUID = 1L;
	public String origin_from;
	public String destination_to;
	public String measurement;
	
	public SimulationRequestGoogle_Dto(String origin_from, String destination_to, String measurement) {
		super();
		this.origin_from = origin_from;
		this.destination_to = destination_to;
		this.measurement = measurement;
	}

	public SimulationRequestGoogle_Dto() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
