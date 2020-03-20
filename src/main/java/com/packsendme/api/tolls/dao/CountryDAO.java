package com.packsendme.api.tolls.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mongodb.MongoClientException;
import com.packsendme.api.tolls.repository.CountryModel;
import com.packsendme.api.tolls.repository.CountryRepository;


@Component("countryDAO")
public class CountryDAO implements ICountryMongo<CountryModel> {
	
	@Autowired
	CountryRepository countryRepository;

	@Override
	public CountryModel add(CountryModel entity) {
		CountryModel country = null;
		try {
			country = countryRepository.insert(entity);
		}
		catch (MongoClientException e ) {
			e.printStackTrace();
		}
		return country;
	}

	@Override
	public CountryModel find(CountryModel countryFind) {
		CountryModel country = null;
		try {
			country = countryRepository.findCountryByCodNumber(
					countryFind.getIdcountry());
		}
		catch (MongoClientException e ) {
			e.printStackTrace();
		}
		return country;
	}

	
	@Override
	public List<CountryModel> findAll() {
		List<CountryModel> countryL = null;
		try {
			countryL = countryRepository.findAll();
		}
		catch (MongoClientException e ) {
			e.printStackTrace();
		}
		return countryL;
	}

	@Override
	public void remove(CountryModel entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public CountryModel update(CountryModel entity) {
		CountryModel country = null;
		try {
			country = countryRepository.save(entity);
		}
		catch (MongoClientException e ) {
			e.printStackTrace();
		}
		return country;
	}

	@Override
	public void addList(List<CountryModel> entity) {
		try {
			countryRepository.saveAll(entity);
		}
		catch (MongoClientException e ) {
			e.printStackTrace();
		}
	}



	

	
}
