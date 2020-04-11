package com.packsendme.api.google.component;

import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.packsendme.api.google.controller.IBusinessManager_SA_Client;
import com.packsendme.fuel.bre.rule.model.FuelBRE_Model;
import com.packsendme.lib.common.constants.CacheBRE_Constants;
import com.packsendme.lib.common.constants.Region_Constants;
import com.packsendme.tolls.bre.model.TollsBRE_Model;

@Component
public class TollsFuelRoadwayData_Component {
	
	@Autowired
	ConnectionAPI_Component connectionGoogle_Geocode;
	
	@Autowired
	RegionCountryRoadway_Component location_component;
	
	@Autowired
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
		String regionCountry = getRegionCountryByJson(regionJsonObj);
		String fuelRegionCache = getFuelCacheName(regionCountry);
		ResponseEntity<?> fuelResponse_Entity = businessManager_SA_Client.getFuelBRE_SA(fuelRegionCache);
		FuelBRE_Model fuel_bre = (FuelBRE_Model) fuelResponse_Entity.getBody();
		return fuel_bre;
	}
	
	public TollsBRE_Model getTollsBREFromCache(JSONObject regionJsonObj) {
		String regionCountry = getRegionCountryByJson(regionJsonObj);
		String tollsRegionCache = getTollsCacheName(regionCountry);
		ResponseEntity<?> tollsResponse_Entity = businessManager_SA_Client.getTollsBRE_SA(tollsRegionCache);
		TollsBRE_Model tolls_bre = (TollsBRE_Model) tollsResponse_Entity.getBody();
		return tolls_bre;
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
