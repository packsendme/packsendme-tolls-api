package com.packsendme.api.google.service;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.packsendme.api.google.component.AnalyzeDistanceData_Component;
import com.packsendme.api.google.component.AnalyzeTollsData_Component;
import com.packsendme.api.google.component.ConnectionGoogleAPI_Component;
import com.packsendme.lib.common.constants.HttpExceptionPackSend;
import com.packsendme.lib.common.response.Response;
import com.packsendme.lib.distance.response.dto.DistanceResponse_Dto;
import com.packsendme.lib.simulation.request.dto.SimulationRequest_Dto;
import com.packsendme.lib.tolls.response.dto.TollsResponse_Dto; 

@Service
@ComponentScan("com.packsendme.api.google.component")
public class GoogleAPI_Service {
	
	@Autowired
	private AnalyzeTollsData_Component analyzeTolls_Component;
	
	@Autowired
	private AnalyzeDistanceData_Component analyzeDistance_Component;
	
	@Autowired
	ConnectionGoogleAPI_Component connectionAPI;
	
	private final String API_DISTANCE = "GOOGLE_PLACE_DISNTACE";
	private final String API_TOLLS = "GOOGLE_PLACE_TOLLS"; 

	
	private com.packsendme.lib.tolls.response.dto.TollsResponse_Dto tollsResponse_Dto = new TollsResponse_Dto(); 
	
	public ResponseEntity<?> getTollsAPI(SimulationRequest_Dto simulation) {
		Response<TollsResponse_Dto> responseObj = null;
		String jsonBodyS = null;
		try {
		    ResponseEntity<String> response = connectionAPI.connectionGoogleAPI(simulation, API_TOLLS);

			if (response.getStatusCode() == HttpStatus.OK) {
		    	jsonBodyS = response.getBody();
		    	JSONParser parser = new JSONParser();
		    	JSONObject jsonObject = (JSONObject) parser.parse(jsonBodyS);
			    
		    	if(jsonObject.get("status").equals("OK")) {
		    		tollsResponse_Dto = analyzeTolls_Component.analyzeJsonTolls(jsonObject, simulation);
		    		responseObj = new Response<TollsResponse_Dto>(0,HttpExceptionPackSend.GOOGLEAPI_PLACE.getAction(), tollsResponse_Dto);
					return new ResponseEntity<>(responseObj, HttpStatus.ACCEPTED);
		    	}
		    	else {
					responseObj = new Response<TollsResponse_Dto>(0,HttpExceptionPackSend.GOOGLEAPI_PLACE.getAction(), null);
					return new ResponseEntity<>(responseObj, HttpStatus.BAD_REQUEST);
		    	}
			}
			else {
				return new ResponseEntity<>(responseObj, HttpStatus.NOT_FOUND);
			}
		}
		catch (Exception e ) {
			e.printStackTrace();
			responseObj = new Response<TollsResponse_Dto>(0,HttpExceptionPackSend.FAIL_EXECUTION.getAction(), null);
			return new ResponseEntity<>(responseObj, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public ResponseEntity<?> getDistancesAPI(SimulationRequest_Dto simulation) {
		Response<DistanceResponse_Dto> responseObj = null;
		DistanceResponse_Dto distanceResponse_dto = new DistanceResponse_Dto();
		try {
		    ResponseEntity<String> response = connectionAPI.connectionGoogleAPI(simulation, API_DISTANCE);

			if (response.getStatusCode() == HttpStatus.OK) {
				String jsonData = response.getBody();
		    	JSONParser parser = new JSONParser();
		    	JSONObject jsonObject = (JSONObject) parser.parse(jsonData);
		    	
		    	if(jsonObject.get("status").equals("OK")) {
		    		distanceResponse_dto = analyzeDistance_Component.analyzeJsonDistance(jsonObject,simulation);
					responseObj = new Response<DistanceResponse_Dto>(0,HttpExceptionPackSend.GOOGLEAPI_PLACE.getAction(), distanceResponse_dto);
					return new ResponseEntity<>(responseObj, HttpStatus.ACCEPTED);
		    	}
		    	else {
					responseObj = new Response<DistanceResponse_Dto>(0,HttpExceptionPackSend.GOOGLEAPI_PLACE.getAction(), null);
					return new ResponseEntity<>(responseObj, HttpStatus.BAD_REQUEST);
		    	}

			}
			else {
				return new ResponseEntity<>(responseObj, HttpStatus.NOT_FOUND);
			}

		}
		catch (ParseException e ) {
			e.printStackTrace();
			responseObj = new Response<DistanceResponse_Dto>(0,HttpExceptionPackSend.FAIL_EXECUTION.getAction(), null);
			return new ResponseEntity<>(responseObj, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
}
