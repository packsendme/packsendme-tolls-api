package com.packsendme.api.tolls.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.packsendme.api.tolls.service.DistancePlaceAPI_Service;
import com.packsendme.api.tolls.service.TollsCost_Service;
import com.packsendme.lib.simulation.request.dto.SimulationRequest_Dto;


@RestController
@RequestMapping("/api/tolls/")
public class TollsAPI_Controller {

	@Autowired
	private DistancePlaceAPI_Service googleDirection;

	@Autowired
	private TollsCost_Service tolls_Service;
	
	@GetMapping("/distance/{origins}/{destinations}")
	public ResponseEntity<?> getDistance(
			@Validated @PathVariable ("origins") String origins,
			@Validated @PathVariable ("destinations") String destinations) throws JsonProcessingException, IOException {		
		return googleDirection.loadDistancesCities(origins, destinations);
	}

	@PostMapping("/costs")
	public ResponseEntity<?> getCostsByDirection(
			@Validated @RequestBody SimulationRequest_Dto simulation) throws JsonProcessingException, IOException {
		return tolls_Service.getTollsAnalyze(simulation);
	}
	
}
