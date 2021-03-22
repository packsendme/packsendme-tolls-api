package com.packsendme.api.google.component;

import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import com.packsendme.api.google.dao.TollsFuelCacheImpl_Dao;
import com.packsendme.roadbrewa.entity.TollsFuel;

@Component
@EnableFeignClients(basePackages="com.packsendme.api.google.controller")
@ComponentScan("com.packsendme.api.google.dao")
public class TollsFuelTrackingData_Component {
	
	@Autowired
	ConnectionAPI_Component connectionGoogle_Geocode;
	
	@Autowired
	RegionCountryRoadway_Component location_component;
	
	@Autowired
	TollsFuelCacheImpl_Dao tollsFuelCache_Dao;

	private final String ANALYSE_ARRAY_RESULT = "results";
	private final String ANALYSE_ELEMENT_ADDRESS = "address_components";
	private final String ANALYSE_ELEMENT_SHORTNAME = "short_name";

	private final String REGION_COUNTRY_DEFAUL = "South America";
	
	
	// Parser Json Result Google API - Geo 
	public String getRegionCountryByJson(JSONObject regionJsonObj) {
		String resultLocation = null, locationParse = null;
		
		
		System.out.print(" ----------------------------");
		System.out.print(" JSON getRegionCountryByJson "+ regionJsonObj.toJSONString());
		System.out.print(" ----------------------------");
		
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
		    
			System.out.print(" ----------------------------");
			System.out.print(" RESULTADOR COUNTRY LOCATION "+ locationParse);
			System.out.print(" ----------------------------");

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
	
	public TollsFuel getTollsFuelBREFromCache(String countryFind) {
		TollsFuel tollsFuelObj = null;

		System.out.println("===============================================================================");
		System.out.println(" STATUS fuelRegionCache "+ countryFind);
		System.out.println("===============================================================================");
		
		try {
			tollsFuelObj = tollsFuelCache_Dao.findOne(countryFind);
			return tollsFuelObj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
