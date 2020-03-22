package com.packsendme.api.tolls.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ITolls_Repository extends MongoRepository<TollsCountry_Model, String> {
	
	//@Query("{'name_country' : ?0}")
	@Query("{'name_country' :  {$eq: ?0}}")
	TollsCountry_Model findCostTollByNameContry(String name_country);
}
