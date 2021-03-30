package com.packsendme.api.google.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.packsendme.api.google.service.GoogleAPI_Service;


@RestController
@RequestMapping("/api/google")
public class GoogleAPI_Controller {
	
	private Map<String,Object> header = new HashMap<String,Object>();

	@Autowired
	private GoogleAPI_Service api_Service;
	
	/*
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@GetMapping("/distance/{from}/{to}/{measurement}")
	public ResponseEntity<?> getDistance(
			@RequestHeader("isoLanguageCode") String isoLanguageCode, 
			@RequestHeader("isoCountryCode") String isoCountryCode,
			@RequestHeader("isoCurrencyCode") String isoCurrencyCode,
			@RequestHeader("originApp") String originApp,
			@PathVariable("from") String from,
			@PathVariable("to") String to,
			@PathVariable("measurement") String measurement) throws JsonProcessingException, IOException 
	{
		header.put("isoLanguageCode", isoLanguageCode);
		header.put("isoCountryCode", isoCountryCode);
		header.put("isoCurrencyCode", isoCurrencyCode);
		header.put("originApp", originApp);
		
		return api_Service.getDistancesAPI(from,to,measurement,header);
	}
	*/
	
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@GetMapping("/distance/{from}/{to}/{measurement}")
	public ResponseEntity<?> getTracking(
			@RequestHeader("isoLanguageCode") String isoLanguageCode, 
			@RequestHeader("isoCountryCode") String isoCountryCode,
			@RequestHeader("isoCurrencyCode") String isoCurrencyCode,
			@RequestHeader("originApp") String originApp,
			@PathVariable("from") String from,
			@PathVariable("to") String to,
			@PathVariable("measurement") String measurement) throws JsonProcessingException, IOException 
	{
		header.put("isoLanguageCode", isoLanguageCode);
		header.put("isoCountryCode", isoCountryCode);
		header.put("isoCurrencyCode", isoCurrencyCode);
		header.put("originApp", originApp);
		return api_Service.getTrackingRoadwayAPI(from,to,measurement, header);
	}
	
}
