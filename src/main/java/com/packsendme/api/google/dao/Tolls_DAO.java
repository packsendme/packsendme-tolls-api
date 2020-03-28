package com.packsendme.api.google.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mongodb.MongoClientException;
import com.packsendme.api.google.repository.ITolls_Repository;
import com.packsendme.api.google.repository.TollsCosts_Model;
import com.packsendme.lib.tolls.response.dto.TollsCostsResponse_Dto;
import com.packsendme.lib.tolls.response.dto.TollsResponse_Dto;

@Component("Tolls_DAO")
public class Tolls_DAO implements ITolls_DAO<TollsCostsResponse_Dto,TollsResponse_Dto> {

	private final Double average_price_toll_default = 0.0;
	
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
	public TollsCostsResponse_Dto find(String object) {
		TollsCostsResponse_Dto costsTolls_dto = new TollsCostsResponse_Dto();
		try {
			TollsCosts_Model tollModel = tolls_Repository.findCostTollByNameContry(object);
			if(tollModel != null) {
				costsTolls_dto.average_price_toll = tollModel.toll_cost;
				costsTolls_dto.currency_price = tollModel.currency_cost;
				costsTolls_dto.status = tollModel.status;
			}
			else{
				costsTolls_dto.average_price_toll = average_price_toll_default;
				costsTolls_dto.currency_price = null;
				costsTolls_dto.status = false;
			}
			return costsTolls_dto;
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
