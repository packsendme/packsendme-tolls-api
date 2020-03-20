package com.packsendme.api.tolls.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;


public interface CountryRepository extends MongoRepository<CountryModel, String>   {
	

    @Query("{'idcountry' : ?0}")
	CountryModel findCountryByCodNumber(String idcountry);


}