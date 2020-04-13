package com.packsendme.api.google.component;

import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.packsendme.api.google.controller.IBusinessManager_SA_Client;
import com.packsendme.lib.common.constants.CacheBRE_Constants;
import com.packsendme.lib.common.constants.HttpExceptionPackSend;
import com.packsendme.lib.common.constants.Region_Constants;
import com.packsendme.lib.common.response.Response;
import com.packsendme.tollsfuel.bre.model.TollsFuelBRE_Model;

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
	
	public TollsFuelBRE_Model getTollsFuelBREFromCache(JSONObject regionJsonObj) {
		Gson gson = new Gson();
		TollsFuelBRE_Model tollsFuelObj = null;
		
		String regionCountry = getRegionCountryByJson(regionJsonObj);
		String fuelRegionCache = getFuelCacheName(regionCountry);


		System.out.println("===============================================================================");
		System.out.println(" STATUS fuelRegionCache "+ fuelRegionCache);
		System.out.println(" STATUS regionCountry "+ regionCountry);
		System.out.println("===============================================================================");
		
		
		try {
			ResponseEntity<?> fuelResponse_Entity = businessManager_SA_Client.getTollsFuelBRE_SA(fuelRegionCache);
			if(fuelResponse_Entity.getStatusCode() == HttpStatus.ACCEPTED) {
				String jsonPayload = fuelResponse_Entity.getBody().toString();
				Response<Object> response = gson.fromJson(jsonPayload, Response.class);
				if(response.getResponseCode() == HttpExceptionPackSend.FOUND_BUSINESS_RULE.value()) {
					System.out.println(" MY OBJECT  "+ response.getBody().toString());
					String jsonObject = response.getBody().toString();
					tollsFuelObj = gson.fromJson(jsonObject, TollsFuelBRE_Model.class);
					System.out.println(" ");
					System.out.println(" ");
					System.out.println("===============================================================================");
					System.out.println("TOLLS PRICE "+ tollsFuelObj.tollsfuelPriceCountry.size());
					System.out.println("getFuelPriceFromObjBRE "+ tollsFuelObj.name_rule);
					System.out.println("===============================================================================");
					System.out.println(" ");
					System.out.println(" ");
				}
			}
			return tollsFuelObj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
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
				cache = CacheBRE_Constants.TOLLSFUEL_BRE_SA_CACHE;
				break;
			default:
				break;
		}
		return cache;
	}


}
