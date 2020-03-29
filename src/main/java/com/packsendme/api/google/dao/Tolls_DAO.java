package com.packsendme.api.google.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mongodb.MongoClientException;
import com.packsend.api.google.dto.TollsCosts_Dto;
import com.packsendme.api.google.repository.ITolls_Repository;
import com.packsendme.api.google.repository.TollsCosts_Model;
import com.packsendme.lib.tolls.response.dto.TollsResponse_Dto;

@Component("Tolls_DAO")
public class Tolls_DAO implements ITolls_DAO<TollsCosts_Dto,TollsResponse_Dto> {

	private final Double average_price_toll_default = 0.0;
	
	@Autowired
	ITolls_Repository tolls_Repository;

	@Override
	public TollsCosts_Dto add(TollsCosts_Dto entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addList(List<TollsCosts_Dto> entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public TollsCosts_Dto find(String object) {
		TollsCosts_Dto costsTolls_dto = new TollsCosts_Dto();
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
	public List<TollsCosts_Dto> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove(TollsCosts_Dto entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public TollsCosts_Dto update(TollsCosts_Dto entity) {
		// TODO Auto-generated method stub
		return null;
	}

		
}
