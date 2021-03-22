package com.packsendme.api.google.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.packsendme.roadbrewa.entity.Roadway;

@Repository
public interface IRoadway_Repository extends MongoRepository<Roadway, String>{

	@Query("{'category' :  {$eq: ?0}}")
	Roadway findBusinessRuleByCategory(String category);
	
	@Query("{'transport' : ?0, status : {$eq : ?1}}")
	Roadway findRodwayBreByTransportStatus(String transport, String status);
	
	@Query("{'blocked_id' :  {$eq: ?0}}")
	List<Roadway> findRodwaysBreByBlockId(String blocked_id);

}
