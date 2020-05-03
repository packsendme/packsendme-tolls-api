package com.packsendme.api.google.component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.packsendme.lib.common.constants.generic.GoogleAPI_Constants;
import com.packsendme.lib.common.constants.generic.HttpExceptionPackSend;
import com.packsendme.lib.common.response.Response;
import com.packsendme.lib.simulation.request.dto.SimulationRequest_Dto;


enum UnityMeasurement {
    KM, MI
}

@Component
public class ConnectionAPI_Component {

	@Value(value = "${api.google.tolls}")
	public String tolls_api_url;
	
	@Value(value = "${api.google.key}")
	private String key_api;
	
	@Value(value = "${api.google.distance}")
	private String distance_api_url;
	
	@Value(value = "${api.google.geocode}")
	private String geocode_api_url;
	
	private final String UNITY_MEASUREMENT_KM = "metric";
	private final String UNITY_MEASUREMENT_MI = "imperial";
	
	
	public ResponseEntity<String> connectionGoogleAPI(SimulationRequest_Dto simulation, String countryAddress, String typeAPI) {
		try {
			RestTemplate restTemplate = new RestTemplate();
			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			HttpEntity request = new HttpEntity(headers);
			
			Map<String, String> uriParam = new HashMap<>();
			if (typeAPI.equals(GoogleAPI_Constants.API_GEOCODE)) {
			    uriParam.put("address", countryAddress);
			}
			else {
			    uriParam.put("origin", simulation.address_origin);
			    uriParam.put("destination", simulation.address_destination);
			    uriParam.put("units", getUnity(simulation.unity_measurement_distance_txt));
			}
		    uriParam.put("key", key_api);
		    uriParam.put("travel_mode", "DRIVING");
		    
		    ResponseEntity<String> response = restTemplate.exchange(
		    		getGoogleAPIURL(typeAPI),
		    		HttpMethod.GET, 
		    		request,
                    String.class,
                    uriParam);
		    
		    return response;
		}
		catch (Exception e ) {
			e.printStackTrace();
			Response<String> responseObj = new Response<String>(0,HttpExceptionPackSend.FAIL_EXECUTION.getAction(), null);
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public String getUnity(String unity) {
		String unitM;
		
		switch (unity) {
		case "KM":
			unitM = UNITY_MEASUREMENT_KM;
			break;
		case "MI":
			unitM = UNITY_MEASUREMENT_MI;
			break;
		default:
			unitM = UNITY_MEASUREMENT_KM;
			break;
		}
		return unitM;
	}
	
	
	public String getGoogleAPIURL(String typeAPI) {
		String api = null;
		switch (typeAPI) {
			case GoogleAPI_Constants.API_DISTANCE:
				api = distance_api_url;
				break;
			case GoogleAPI_Constants.API_TOLLS:
				api = tolls_api_url;
				break;
			case GoogleAPI_Constants.API_GEOCODE:
				api = geocode_api_url;
				break;
			default:
				break;
		}
		return api;
	}

}
