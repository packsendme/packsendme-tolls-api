package com.packsendme.api.google.component;

import java.io.IOException;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.packsendme.api.google.controller.IBusinessManager_SA_Client;
import com.packsendme.fuel.bre.rule.model.FuelBRE_Model;
import com.packsendme.lib.common.constants.CacheBRE_Constants;
import com.packsendme.lib.common.constants.Region_Constants;
import com.packsendme.tolls.bre.model.TollsBRE_Model;

@Component
@EnableFeignClients(basePackages="com.packsendme.api.google.controller")
public class TollsFuelTrackingData_Component {
	
	@Autowired
	ConnectionAPI_Component connectionGoogle_Geocode;
	
	@Autowired
	RegionCountryRoadway_Component location_component;
	
	@Autowired(required=true)
	IBusinessManager_SA_Client businessManager_SA_Client;
	
	private final String ANALYSE_ARRAY_RESULT = "results";
	private final String ANALYSE_ELEMENT_ADDRESS = "address_components";
	private final String ANALYSE_ELEMENT_SHORTNAME = "short_name";

	private final String REGION_COUNTRY_DEFAUL = "South America";
	
	
	// Parser Json Result Google API - Geo 
	public String getRegionCountryByJson(JSONObject regionJsonObj) {
		String resultLocation = null, locationParse = null;
		try {
    		JSONArray resultArray = (JSONArray) regionJsonObj.get(ANALYSE_ARRAY_RESULT);
		    Iterator<JSONObject> iterator = resultArray.iterator();
		    while (iterator.hasNext()) {
		    	JSONObject resultJson = iterator.next();
		        JSONArray addressArray = (JSONArray) resultJson.get(ANALYSE_ELEMENT_ADDRESS);  
		        for (Iterator addressIt = addressArray.iterator(); addressIt.hasNext();) {
		        	JSONObject addressJson = (JSONObject) addressIt.next();
		            locationParse = addressJson.get(ANALYSE_ELEMENT_SHORTNAME).toString();
	            }
		     }
			resultLocation = location_component.findCountry(locationParse);
			System.out.print(" RESULTADOR COUNTRY LOCATION "+ resultLocation);
			return resultLocation;
		} 
		catch (Exception e) {
			System.out.print(" ERROR "+ e);
			resultLocation = location_component.findCountry(REGION_COUNTRY_DEFAUL);
			return resultLocation;
		}
	}
	
	public FuelBRE_Model getFuelBREFromCache(JSONObject regionJsonObj) {
		Gson gson = new Gson();
		
		ObjectMapper mapper = new ObjectMapper();
		String regionCountry = getRegionCountryByJson(regionJsonObj);
		String fuelRegionCache = getFuelCacheName(regionCountry);
		ResponseEntity<?> fuelResponse_Entity = businessManager_SA_Client.getFuelBRE_SA(fuelRegionCache);
		
		System.out.println(" ");
		System.out.println("===============================================================================");
		System.out.println("getFuelPriceFromObjBRE "+ fuelResponse_Entity.getStatusCode());
		System.out.println("getFuelPriceFromObjBRE ---- fuelRegionCache "+regionCountry+" | "+ fuelRegionCache);
		System.out.println("===============================================================================");
		System.out.println(" ");
		
		if(fuelResponse_Entity.getStatusCode() == HttpStatus.ACCEPTED) {
			String json = fuelResponse_Entity.getBody().toString();
			//FuelBRE_Model fuelBRE = gson.fromJson(json, FuelBRE_Model.class);
			
			FuelBRE_Model fuelBRE;
			try {
				fuelBRE = mapper.readValue(json, FuelBRE_Model.class);
				
				System.out.println(" ");
				System.out.println("===============================================================================");
				System.out.println(" ");
				System.out.println("getFuelBREFromCache JSON "+ json);
				System.out.println("getFuelPriceFromObjBRE "+ fuelBRE.name_rule);
				System.out.println("getFuelPriceFromObjBRE "+ fuelBRE.fuelPriceCountry.size());
				System.out.println(" ");
				System.out.println("===============================================================================");
				System.out.println(" ");
				return fuelBRE;
				
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	

			
		
		}
		return null;
	}
	
	public TollsBRE_Model getTollsBREFromCache(JSONObject regionJsonObj) {
		Gson gson = new Gson();
		String regionCountry = getRegionCountryByJson(regionJsonObj);
		String tollsRegionCache = getTollsCacheName(regionCountry);
		ResponseEntity<?> tollsResponse_Entity = businessManager_SA_Client.getTollsBRE_SA(tollsRegionCache);
		if(tollsResponse_Entity.getStatusCode() == HttpStatus.ACCEPTED) {
			String json = tollsResponse_Entity.getBody().toString();
			TollsBRE_Model tollsBRE = gson.fromJson(json, TollsBRE_Model.class);
			return tollsBRE;
		}
		return null;
	}

	
	public String getFuelCacheName(String region) {
		String cache = null;
		switch (region) {
			case Region_Constants.EUROPE_REGION:
				cache = CacheBRE_Constants.FUEL_BRE_EURO_CACHE;
				break;
			case Region_Constants.NORTH_AMERICA_REGION:
				cache = CacheBRE_Constants.FUEL_BRE_NA_CACHE;
				break;
			case Region_Constants.SOUTH_AMERICA_REGION:
				cache = CacheBRE_Constants.FUEL_BRE_SA_CACHE;
				break;
			default:
				break;
		}
		return cache;
	}

	public String getTollsCacheName(String region) {
		String cache = null;
		switch (region) {
			case Region_Constants.EUROPE_REGION:
				cache = CacheBRE_Constants.TOLLS_BRE_EURO_CACHE;
				break;
			case Region_Constants.NORTH_AMERICA_REGION:
				cache = CacheBRE_Constants.TOLLS_BRE_NA_CACHE;
				break;
			case Region_Constants.SOUTH_AMERICA_REGION:
				cache = CacheBRE_Constants.TOLLS_BRE_SA_CACHE;
				break;
			default:
				break;
		}
		return cache;
	}


}
