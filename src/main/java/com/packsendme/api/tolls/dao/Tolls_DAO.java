package com.packsendme.api.tolls.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mongodb.MongoClientException;
import com.packsendme.api.tolls.dto.TollsResponse_Dto;
import com.packsendme.api.tolls.repository.ITolls_Repository;
import com.packsendme.api.tolls.repository.TollsCountry_Model;

@Component("Tolls_DAO")
public class Tolls_DAO implements ITolls_DAO<TollsCountry_Model,TollsResponse_Dto> {

	@Autowired
	ITolls_Repository tolls_Repository;

	@Override
	public TollsCountry_Model add(TollsCountry_Model entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addList(List<TollsCountry_Model> entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, TollsCountry_Model> find(TollsResponse_Dto object) {
		TollsCountry_Model tollModel = null;
		Map<String, TollsCountry_Model> costsTolls = new HashMap<String, TollsCountry_Model>();
		try {
			for(Map.Entry<String, Integer> entry : object.tolls.entrySet()) {
				System.out.println(" find by "+ entry.getKey());
				tollModel = tolls_Repository.findCostTollByNameContry(entry.getKey());
				System.out.println(" tollModel by "+ tollModel.toll_cost);

				costsTolls.put(tollModel.name_country, tollModel);
			}
			return costsTolls;
		}
		catch (MongoClientException e ) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<TollsCountry_Model> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove(TollsCountry_Model entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public TollsCountry_Model update(TollsCountry_Model entity) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	@Override
	public Map<String, Tolls_Country_Model> find(TollsResponse_Dto tollDto) {
		Tolls_Country_Model tollModel = null;
		Map<String, Tolls_Country_Model> costsTolls = new HashMap<String, Tolls_Country_Model>();
		try {
			for(Map.Entry<String, Integer> entry : tollDto.tolls.entrySet()) {
				tollModel = tolls_Repository.findCostTollByNameContry(entry.getKey());
				costsTolls.put(tollModel.name_country, tollModel);
			}
			return costsTolls;
		}
		catch (MongoClientException e ) {
			e.printStackTrace();
			return null;
		}
	}*/
	
}
