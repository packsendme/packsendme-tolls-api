package com.packsendme.api.tolls.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.packsendme.api.tolls.component.AnalyzeData_Component;
import com.packsendme.api.tolls.dto.TollsResponse_Dto;
import com.packsendme.lib.common.constants.HttpExceptionPackSend;
import com.packsendme.lib.common.response.Response;
import com.packsendme.lib.simulation.request.dto.SimulationRequest_Dto;
 

@Service
@ComponentScan("com.packsendme.api.tolls.component")
public class TollsCost_Service {
	
	@Value(value = "${api.tolls.direction}")
	public String direction_api_url;
	
	@Value(value = "${api.tolls.key}")
	private String key_api;

	@Autowired
	private AnalyzeData_Component analyzeData_Component;
	
	private TollsResponse_Dto tollsResponse_Dto = new TollsResponse_Dto(); 
	
	public ResponseEntity<?> getTollsAnalyze(SimulationRequest_Dto simulation) {
		Response<TollsResponse_Dto> responseObj = null;
		String jsonBodyS = null;
		try {
			RestTemplate restTemplate = new RestTemplate();
			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			HttpEntity request = new HttpEntity(headers);
			
			Map<String, String> uriParam = new HashMap<>();
		    uriParam.put("origin", simulation.address_origin);
		    uriParam.put("destination", simulation.address_destination);
		    uriParam.put("key", key_api);
		    uriParam.put("travel_mode", "DRIVING");
		    uriParam.put("avoidTolls", "false");
			
		    System.out.println(" ===================================");
		    System.out.println(" origin "+ simulation.address_origin);
		    System.out.println(" destination "+ simulation.address_destination);
		    System.out.println(" key "+ key_api);
		    System.out.println(" travel_mode "+ "DRIVING");
		    System.out.println(" avoidTolls "+ "false");
		    System.out.println(" ===================================");
		    
		    
		    ResponseEntity<String> response = restTemplate.exchange(
		    		direction_api_url,
		    		HttpMethod.GET, 
		    		request,
                    String.class,
                    uriParam);

		    if (response.getStatusCode() == HttpStatus.OK) {
		    	jsonBodyS = response.getBody();
		    	JSONParser parser = new JSONParser();
		    	JSONObject jsonObject = (JSONObject) parser.parse(jsonBodyS);

		    	if(jsonObject.get("status") == "OK") {
		    		tollsResponse_Dto = analyzeData_Component.analyzeJsonTolls(jsonObject);

				    System.out.println(" ===================================");
				    System.out.println(" tollsResponse_Dto "+ tollsResponse_Dto.tolls.size());
				    System.out.println(" ===================================");

		    		
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
	
	
}
