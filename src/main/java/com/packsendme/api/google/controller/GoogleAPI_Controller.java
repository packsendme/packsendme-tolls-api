package com.packsendme.api.google.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.packsendme.api.google.service.GoogleAPI_Service;
import com.packsendme.lib.simulation.request.dto.SimulationRequest_Dto;


@RestController
@RequestMapping("/api/google")
public class GoogleAPI_Controller {

	@Autowired
	private GoogleAPI_Service api_Service;
	
	@GetMapping("/distance")
	public ResponseEntity<?> getDistance(
			@Validated @RequestBody SimulationRequest_Dto simulation) throws JsonProcessingException, IOException {
		return api_Service.getDistancesAPI(simulation);
	}

	@PostMapping("/tolls")
	public ResponseEntity<?> getCostsTolls(
			@Validated @RequestBody SimulationRequest_Dto simulation) throws JsonProcessingException, IOException {
		return api_Service.getTollsAPI(simulation);
	}
	
}
