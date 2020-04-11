package com.packsendme.api.google.service;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.packsend.api.google.dto.GoogleAPIDistanceResponse_Dto;
import com.packsend.api.google.dto.GoogleAPITrackingResponse_Dto;
import com.packsendme.api.google.component.DistanceAPIData_Component;
import com.packsendme.api.google.component.ConnectionAPI_Component;
import com.packsendme.api.google.component.TollsFuelRoadwayData_Component;
import com.packsendme.api.google.component.TrackingAPIData_Component;
import com.packsendme.api.google.utility.SeparationElementTools;
import com.packsendme.fuel.bre.rule.model.FuelBRE_Model;
import com.packsendme.lib.common.constants.GoogleAPI_Constants;
import com.packsendme.lib.common.constants.HttpExceptionPackSend;
import com.packsendme.lib.common.response.Response;
import com.packsendme.lib.simulation.request.dto.SimulationRequest_Dto;
import com.packsendme.tolls.bre.model.TollsBRE_Model; 

@Service
@ComponentScan("com.packsendme.api.google.component")
public class GoogleAPI_Service {
	
	@Autowired
	private TrackingAPIData_Component trackingData_Component;
	@Autowired
	private DistanceAPIData_Component analyzeDistance_Component;
	@Autowired
	private TollsFuelRoadwayData_Component tollsFuelRoadwayData_Component;
	
	@Autowired
	ConnectionAPI_Component connectionAPI;
	
	SeparationElementTools elementTools = new SeparationElementTools();
	
	private final String API_DISTANCE = "GOOGLE_PLACE_DISNTACE";
	private final String API_TOLLS = "GOOGLE_PLACE_TOLLS"; 

	
	private GoogleAPITrackingResponse_Dto trackingResponse_Dto = new GoogleAPITrackingResponse_Dto(); 
	
	public ResponseEntity<?> getTrackingRoadwayAPI(SimulationRequest_Dto simulation) {
		Response<GoogleAPITrackingResponse_Dto> responseObj = null;
		String trackingJsonBody = null, geocodeJsonBody = null;
		try {
			String regionCountry = elementTools.subStringCountryFrom(simulation.address_origin);
			// Call API Google -> Direction/Geocode 
			ResponseEntity<String> responseAPITracking = connectionAPI.connectionGoogleAPI(simulation, null, API_TOLLS);
			ResponseEntity<String> responseAPIGeocode = connectionAPI.connectionGoogleAPI(null, regionCountry, GoogleAPI_Constants.API_GEOCODE);
			
			// Result APIs Direction/Geocode
			if ((responseAPITracking.getStatusCode() == HttpStatus.OK) && (responseAPIGeocode.getStatusCode() == HttpStatus.OK)) {
				trackingJsonBody = responseAPITracking.getBody();
				geocodeJsonBody = responseAPIGeocode.getBody();
		    	JSONParser parser = new JSONParser();
		    	JSONObject trackingJsonObject = (JSONObject) parser.parse(trackingJsonBody);
		    	JSONObject geocodeJsonObject = (JSONObject) parser.parse(geocodeJsonBody);
			    
	    		// GET Fuel AND Tolls BRE-Cache 
	    		FuelBRE_Model fuelBREObj = tollsFuelRoadwayData_Component.getFuelBREFromCache(geocodeJsonObject) ;
	    		TollsBRE_Model tollsBREObj = tollsFuelRoadwayData_Component.getTollsBREFromCache(geocodeJsonObject) ;
		    	
	    		// Get TrackingBRE -> ParserData
	    		if((fuelBREObj != null) && (tollsBREObj != null)) {
	    			trackingResponse_Dto = trackingData_Component.getTrackingDataByJson(trackingJsonObject, simulation, fuelBREObj, tollsBREObj);
			    	responseObj = new Response<GoogleAPITrackingResponse_Dto>(HttpExceptionPackSend.GOOGLEAPI_PLACE.value(),HttpExceptionPackSend.GOOGLEAPI_PLACE.getAction(), trackingResponse_Dto);
		    	}
	    		else {
			    	responseObj = new Response<GoogleAPITrackingResponse_Dto>(HttpExceptionPackSend.NOT_FOUND_GOOGLEAPI_PLACE.value(),HttpExceptionPackSend.NOT_FOUND_GOOGLEAPI_PLACE.getAction(), null);
	    		}
				return new ResponseEntity<>(responseObj, HttpStatus.ACCEPTED);
		    }
		    else {
		    	responseObj = new Response<GoogleAPITrackingResponse_Dto>(HttpExceptionPackSend.NOT_FOUND_GOOGLEAPI_PLACE.value(),HttpExceptionPackSend.NOT_FOUND_GOOGLEAPI_PLACE.getAction(), null);
				return new ResponseEntity<>(responseObj, HttpStatus.ACCEPTED);
		    }
		}
		catch (Exception e ) {
			e.printStackTrace();
			responseObj = new Response<GoogleAPITrackingResponse_Dto>(HttpExceptionPackSend.FAIL_EXECUTION.value(),HttpExceptionPackSend.FAIL_EXECUTION.getAction(), null);
			return new ResponseEntity<>(responseObj, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public ResponseEntity<?> getDistancesAPI(SimulationRequest_Dto simulation) {
		Response<GoogleAPIDistanceResponse_Dto> responseObj = null;
		GoogleAPIDistanceResponse_Dto distanceResponse_dto = new GoogleAPIDistanceResponse_Dto();
		try {
		    ResponseEntity<String> response = connectionAPI.connectionGoogleAPI(simulation, null, API_DISTANCE);

			if (response.getStatusCode() == HttpStatus.OK) {
				String jsonData = response.getBody();
		    	JSONParser parser = new JSONParser();
		    	JSONObject jsonObject = (JSONObject) parser.parse(jsonData);
		    	
		    	if(jsonObject.get("status").equals("OK")) {
		    		distanceResponse_dto = analyzeDistance_Component.getDistanceDataByJson(jsonObject,simulation);
					responseObj = new Response<GoogleAPIDistanceResponse_Dto>(0,HttpExceptionPackSend.GOOGLEAPI_PLACE.getAction(), distanceResponse_dto);
					return new ResponseEntity<>(responseObj, HttpStatus.ACCEPTED);
		    	}
		    	else {
					responseObj = new Response<GoogleAPIDistanceResponse_Dto>(0,HttpExceptionPackSend.GOOGLEAPI_PLACE.getAction(), null);
					return new ResponseEntity<>(responseObj, HttpStatus.BAD_REQUEST);
		    	}

			}
			else {
				return new ResponseEntity<>(responseObj, HttpStatus.NOT_FOUND);
			}

		}
		catch (ParseException e ) {
			e.printStackTrace();
			responseObj = new Response<GoogleAPIDistanceResponse_Dto>(0,HttpExceptionPackSend.FAIL_EXECUTION.getAction(), null);
			return new ResponseEntity<>(responseObj, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
}
