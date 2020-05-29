package com.packsendme.api.google.service;

import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.packsendme.api.google.component.ConnectionAPI_Component;
import com.packsendme.api.google.component.DistanceAPIData_Component;
import com.packsendme.api.google.component.TollsFuelTrackingData_Component;
import com.packsendme.api.google.component.TrackingAPIData_Component;
import com.packsendme.api.google.utility.SeparationElementTools;
import com.packsendme.lib.common.constants.generic.GoogleAPI_Constants;
import com.packsendme.lib.common.constants.generic.HttpExceptionPackSend;
import com.packsendme.lib.common.response.Response;
import com.packsendme.lib.common.response.dto.api.GoogleAPIDistanceResponse_Dto;
import com.packsendme.lib.common.response.dto.api.GoogleAPITrackingResponse_Dto;
import com.packsendme.lib.simulation.http.SimulationRequest_Dto;
import com.packsendme.tollsfuel.bre.model.TollsFuelBRE_Model; 

@Service
@ComponentScan("com.packsendme.api.google.component")
public class GoogleAPI_Service {
	
	@Autowired
	private TrackingAPIData_Component trackingData_Component;
	@Autowired
	private DistanceAPIData_Component analyzeDistance_Component;
	@Autowired
	private TollsFuelTrackingData_Component tollsFuelRoadwayData_Component;
	
	@Autowired
	ConnectionAPI_Component connectionAPI;
	
	SeparationElementTools elementTools = new SeparationElementTools();
	
	private final String API_DISTANCE = "GOOGLE_PLACE_DISNTACE";
	private final String API_TOLLS = "GOOGLE_PLACE_TOLLS"; 

	
	private GoogleAPITrackingResponse_Dto trackingResponse_Dto = new GoogleAPITrackingResponse_Dto(); 
	
	public ResponseEntity<?> getTrackingRoadwayAPI(SimulationRequest_Dto simulation, Map header) {
		Response<GoogleAPITrackingResponse_Dto> responseObj = null;
		String trackingJsonBody = null, geocodeJsonBody = null;
		try {

			System.out.println(" ");
			System.out.println("======    PARAMETROS  1  ========================================================");
			System.out.println(" GoogleAPITrackingResponse_Dto Oringn "+ simulation.address_origin);
			System.out.println(" GoogleAPITrackingResponse_Dto Destionation "+ simulation.address_destination);
			System.out.println("===============================================================================");

			
			String regionCountry = elementTools.subStringCountryFrom(simulation.address_origin);
			
			System.out.println(" ");
			System.out.println("======    PARAMETROS  2  ========================================================");
			System.out.println(" GoogleAPITrackingResponse_Dto regionCountry "+ regionCountry);
			System.out.println("===============================================================================");

			
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
		    	
		    	System.out.println(" ");
				System.out.println("======    PARAMETROS  4  ========================================================");
				System.out.println(" GoogleAPITrackingResponse_Dto regionCountry "+ geocodeJsonObject.toString());
				System.out.println("===============================================================================");

			    
	    		// GET Fuel AND Tolls BRE-Cache 
	    		TollsFuelBRE_Model tollsFuelBREObj = tollsFuelRoadwayData_Component.getTollsFuelBREFromCache(geocodeJsonObject, header) ;
		    	
	    		// Get TrackingBRE -> ParserData
	    		if(tollsFuelBREObj != null){
	    			trackingResponse_Dto = trackingData_Component.getTrackingDataByJson(trackingJsonObject, simulation, tollsFuelBREObj);
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
	
	public ResponseEntity<?> getDistancesAPI(SimulationRequest_Dto simulation, Map header) {
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
