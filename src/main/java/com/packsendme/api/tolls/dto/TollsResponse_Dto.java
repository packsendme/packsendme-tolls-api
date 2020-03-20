package com.packsendme.api.tolls.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.packsendme.api.tolls.repository.TollsCountry_Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TollsResponse_Dto implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public boolean status_tolls;
	public Map<String, Integer> tolls = new HashMap<String, Integer>();
	public Map<String, TollsCountry_Model> costsTolls = new HashMap<String, TollsCountry_Model>();

}
