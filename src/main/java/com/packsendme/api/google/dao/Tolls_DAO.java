package com.packsendme.api.google.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mongodb.MongoClientException;
import com.packsendme.api.google.repository.ITolls_Repository;
import com.packsendme.api.google.repository.TollsCosts_Model;
import com.packsendme.lib.tolls.response.dto.TollsCostsResponse_Dto;
import com.packsendme.lib.tolls.response.dto.TollsResponse_Dto;

@Component("Tolls_DAO")
public class Tolls_DAO implements ITolls_DAO<TollsCostsResponse_Dto,TollsResponse_Dto> {

	@Autowired
	ITolls_Repository tolls_Repository;

	@Override
	public TollsCostsResponse_Dto add(TollsCostsResponse_Dto entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addList(List<TollsCostsResponse_Dto> entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, TollsCostsResponse_Dto> find(TollsResponse_Dto object) {
		Map<String, TollsCostsResponse_Dto> costsTolls = new HashMap<String, TollsCostsResponse_Dto>();
		ModelMapper modelMapper = new ModelMapper();
		try {
			for(Map.Entry<String, Integer> entry : object.countryTolls.entrySet()) {
				System.out.println(" find by "+ entry.getKey());
				TollsCosts_Model tollModel = tolls_Repository.findCostTollByNameContry(entry.getKey());
				TollsCostsResponse_Dto tollsCostsResponse_Dto = modelMapper.map(tollModel, TollsCostsResponse_Dto.class);
				costsTolls.put(tollsCostsResponse_Dto.name_country, tollsCostsResponse_Dto);
			}
			return costsTolls;
		}
		catch (MongoClientException e ) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<TollsCostsResponse_Dto> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove(TollsCostsResponse_Dto entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public TollsCostsResponse_Dto update(TollsCostsResponse_Dto entity) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
