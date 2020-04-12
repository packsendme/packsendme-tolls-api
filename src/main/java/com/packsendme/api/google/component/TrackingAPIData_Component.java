package com.packsendme.api.google.component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.packsendme.api.google.dto.GoogleAPIDistanceResponse_Dto;
import com.packsendme.api.google.dto.GoogleAPITrackingResponse_Dto;
import com.packsendme.api.google.dto.RoadwayTrackingResponse_Dto;
import com.packsendme.api.google.utility.SeparationElementTools;
import com.packsendme.fuel.bre.rule.model.FuelBRE_Model;
import com.packsendme.fuel.bre.rule.price.model.FuelPriceCountryBRE_Model;
import com.packsendme.fuel.bre.rule.price.model.TollsPriceCountryBRE_Model;
import com.packsendme.lib.common.constants.GoogleAPI_Constants;
import com.packsendme.lib.simulation.request.dto.SimulationRequest_Dto;
import com.packsendme.tolls.bre.model.TollsBRE_Model;

@Component
public class TrackingAPIData_Component {

	private final Double AVERAGE_PRICE_DEFAULT = 0.0;

	private final String ANALYSE_PATTERN_TOLLS = "Toll";
	private final String ANALYSE_PATTERN_COUNTRY = "Entering";
	private final String ANALYSE_PATTERN_START = "Start";
	private final String ANALYSE_PATTERN_END = "End";
	
	private final String ANALYSE_ARRAY_ROUTES = "routes";
	private final String ANALYSE_ARRAY_LEGS = "legs";
	private final String ANALYSE_ARRAY_STEPS = "steps";
	
	// ELEMENT
	private final String ANALYSE_ELEMENT_ADDRESS = "start_address";
	private final String ANALYSE_ELEMENT_HTML = "html_instructions";
	private final String ANALYSE_ELEMENT_STARTLOCATION = "start_location";
	private final String ANALYSE_ELEMENT_ENDLOCATION = "end_location";
	private final String ANALYSE_ELEMENT_DISTANCE = "distance";
	private final String ANALYSE_ELEMENT_TEXT = "text";
	
	@Autowired
	private ConnectionAPI_Component connectionGoogle;
	
	@Autowired
	TollsFuelTrackingData_Component tollsFuelData_Component;
	
	@Autowired
	private DistanceAPIData_Component analyzeDistance_Component;
	
	public GoogleAPITrackingResponse_Dto getTrackingDataByJson(JSONObject trackingJsonObj, SimulationRequest_Dto simulation, FuelBRE_Model fuelBRE, TollsBRE_Model tollsBRE){
		int tolls_amount = 0;
        String countryName = null, countryNameChange = null;
        JSONObject jsonHtmlInstLast = null;
        GoogleAPIDistanceResponse_Dto distance_dto = new GoogleAPIDistanceResponse_Dto();
  		Map<String, RoadwayTrackingResponse_Dto> tracking_map = new HashMap<String, RoadwayTrackingResponse_Dto>();
  		RoadwayTrackingResponse_Dto trackingResponse_dto = null;
  		GoogleAPITrackingResponse_Dto googleTrackingResponse_dto = new GoogleAPITrackingResponse_Dto();
  		SeparationElementTools separationElementObj = new SeparationElementTools();

        try {
	        JSONArray jsonRoutes = (JSONArray) trackingJsonObj.get(ANALYSE_ARRAY_ROUTES);
			Iterator<JSONObject> itRoutes = jsonRoutes.iterator();
			JSONArray jsonSteps = null;
			while (itRoutes.hasNext()) {
				JSONObject jsonLegs = itRoutes.next();
				JSONArray jsonArrayLegs = (JSONArray) jsonLegs.get(ANALYSE_ARRAY_LEGS);  
				
			    for (Iterator itLegs = jsonArrayLegs.iterator(); itLegs.hasNext();) {
			    	JSONObject jsonStepsX = (JSONObject) itLegs.next();
			    	
			    	// GET TOTAL DISTANCE
			    	Map totalDistance_map = ((Map)jsonStepsX.get(ANALYSE_ELEMENT_DISTANCE));
			    	String distanceS = totalDistance_map.get(ANALYSE_ELEMENT_TEXT).toString();
			    	googleTrackingResponse_dto.distance_total = separationElementObj.getDistanceParse(distanceS);
			    	
			    	// Find Distance (Origin Location)
		        	distance_dto = getLatLongForDistance(jsonStepsX, ANALYSE_PATTERN_START, simulation);
			    	
        	    	String countryOrigin = jsonStepsX.get(ANALYSE_ELEMENT_ADDRESS).toString();
        	    	countryName = separationElementObj.subStringCountryFrom(countryOrigin);
			    	jsonSteps = (JSONArray) jsonStepsX.get(ANALYSE_ARRAY_STEPS);  //steps   
				}
			    
				for (Iterator itSteps = jsonSteps.iterator(); itSteps.hasNext();) {
				    JSONObject jsonHtmlInst = (JSONObject) itSteps.next();
				    String scheme = ((String) jsonHtmlInst.get(ANALYSE_ELEMENT_HTML)).trim();
				    
					// Find Distance
				    if(countryNameChange != null) {
					    if(countryNameChange.equals(countryName)) {
					    	distance_dto = getLatLongForDistance(jsonHtmlInst, ANALYSE_PATTERN_START, simulation);
					    	countryNameChange = null;
			    		}
				    }
 
				    // Change Country in Direction JSON-GOOGLE
				    if (separationElementObj.analyzeContain(scheme,ANALYSE_PATTERN_COUNTRY) == true){
				    	if(tolls_amount > 0) {
				    		// Find Distance
				    		distance_dto = getLatLongForDistance(jsonHtmlInst, ANALYSE_PATTERN_END, simulation);
				    		//Find Tolls/Fuel Price by Country

				    		FuelPriceCountryBRE_Model fuelObjBRE = getFuelPriceFromObjBRE(countryName,fuelBRE);
				    		TollsPriceCountryBRE_Model tollsObjBRE = getTollsPriceFromObjBRE(countryName,tollsBRE);
				    		
				    		
				    		System.out.println(" ");
				    		System.out.println(" ====================================================================== ");
				    		System.out.println("NAME "+ countryName);
				    		System.out.println("DISTANCE "+ distance_dto.distance);
				    		System.out.println("TOLLS "+ tolls_amount);
				    		System.out.println("TOLLS PRICE "+ tollsObjBRE.tolls_price);
				    		System.out.println("FUEL PRICE "+ fuelObjBRE.fuel_price);
				    		System.out.println("FUEL CURRENCY "+ fuelObjBRE.currency_price);
				    		System.out.println("FUEL UNITY "+ fuelObjBRE.unity_measurement_volume);
				    		System.out.println(" ====================================================================== ");
				    		System.out.println(" ");
				    		


				    		if((fuelObjBRE != null) || (tollsObjBRE != null)) {
				    			trackingResponse_dto = new RoadwayTrackingResponse_Dto(countryName,tolls_amount,tollsObjBRE.tolls_price,distance_dto.distance,
				    				fuelObjBRE.fuel_price,fuelObjBRE.currency_price,fuelObjBRE.unity_measurement_volume);
				    		}
				    		else {
				    			trackingResponse_dto = new RoadwayTrackingResponse_Dto(countryName,tolls_amount,AVERAGE_PRICE_DEFAULT,distance_dto.distance,
					    				AVERAGE_PRICE_DEFAULT,null,null);
				    		}
							tolls_amount = 0;
				    	}
				    	else {
				    		//Find Fuel Price by Country
				    		FuelPriceCountryBRE_Model fuelObjBRE = getFuelPriceFromObjBRE(countryName,fuelBRE);
				    		// Find Distance
				    		distance_dto = getLatLongForDistance(jsonHtmlInst, ANALYSE_PATTERN_END, simulation);

				    		if(fuelObjBRE != null) {
				    			trackingResponse_dto = new RoadwayTrackingResponse_Dto(countryName,0,AVERAGE_PRICE_DEFAULT,distance_dto.distance,
				    				fuelObjBRE.fuel_price,fuelObjBRE.currency_price,fuelObjBRE.unity_measurement_volume);
				    		}
				    		else {
				    			trackingResponse_dto = new RoadwayTrackingResponse_Dto(countryName,0,AVERAGE_PRICE_DEFAULT,distance_dto.distance,
					    				AVERAGE_PRICE_DEFAULT,null,null);
				    		}
				    	}
				    	tracking_map.put(countryName, trackingResponse_dto);
			    		countryName = separationElementObj.subStringCountry(scheme);
			    		countryNameChange = countryName;
				    }
				    // Analyze Tolls in Direction JSON-GOOGLE
				    if (separationElementObj.analyzeContain(scheme,ANALYSE_PATTERN_TOLLS) == true){
				    	tolls_amount++;
				    }
				    jsonHtmlInstLast = jsonHtmlInst;
				}
				
				if(tolls_amount > 0) {
		    		// Find Distance
		    		distance_dto = getLatLongForDistance(jsonHtmlInstLast, ANALYSE_PATTERN_END, simulation);
		    		//Find Tolls/Fuel Price by Country
		    		FuelPriceCountryBRE_Model fuelObjBRE = getFuelPriceFromObjBRE(countryName,fuelBRE);
		    		TollsPriceCountryBRE_Model tollsObjBRE = getTollsPriceFromObjBRE(countryName,tollsBRE);
		    		
		    		if((fuelObjBRE != null) && (tollsObjBRE != null)) {
		    			trackingResponse_dto = new RoadwayTrackingResponse_Dto(countryName,tolls_amount,tollsObjBRE.tolls_price,distance_dto.distance,
		    				fuelObjBRE.fuel_price,fuelObjBRE.currency_price,fuelObjBRE.unity_measurement_volume);
		    		}
		    		else {
			    		trackingResponse_dto = new RoadwayTrackingResponse_Dto(countryName,tolls_amount,AVERAGE_PRICE_DEFAULT,distance_dto.distance,
			    				AVERAGE_PRICE_DEFAULT,null,null);
		    		}
		    		
		    		tracking_map.put(countryName, trackingResponse_dto);
				}
		    	else {
		    		// Find Distance
		    		distance_dto = getLatLongForDistance(jsonHtmlInstLast, ANALYSE_PATTERN_END, simulation);
		    		//Find Fuel Price by Country
		    		FuelPriceCountryBRE_Model fuelObjBRE = getFuelPriceFromObjBRE(countryName,fuelBRE);

		    		if(fuelObjBRE != null) {
		    			trackingResponse_dto = new RoadwayTrackingResponse_Dto(countryName,0,AVERAGE_PRICE_DEFAULT,distance_dto.distance,
		    				fuelObjBRE.fuel_price,fuelObjBRE.currency_price,fuelObjBRE.unity_measurement_volume);
		    		}else {
				    	trackingResponse_dto = new RoadwayTrackingResponse_Dto(countryName,tolls_amount,AVERAGE_PRICE_DEFAULT,distance_dto.distance,
				    		AVERAGE_PRICE_DEFAULT,null,null);
			    	}
		    		tracking_map.put(countryName, trackingResponse_dto);
		    	}
			}
			if (tracking_map.size() > 0) {
				googleTrackingResponse_dto.status = true;
				googleTrackingResponse_dto.trackingRoadway = tracking_map;
			}
			else{
				googleTrackingResponse_dto.status = false;
				googleTrackingResponse_dto.trackingRoadway = null;
			}
			return googleTrackingResponse_dto;
        }
        catch (Exception e) {
        	e.printStackTrace();
        	return null;
		}
    }
	
	//****************************************************************************************************************************//
	// FIND FUEL AND TOLLS IN BRE
	//****************************************************************************************************************************//
	public FuelPriceCountryBRE_Model getFuelPriceFromObjBRE(String countryName, FuelBRE_Model fuelbre) {
		List<FuelPriceCountryBRE_Model> fuelPriceCountry = fuelbre.fuelPriceCountry;
		for(FuelPriceCountryBRE_Model fuelP : fuelPriceCountry) {
    		System.out.println("getFuelPriceFromObjBRE - COUNTRY NAME "+ countryName);

			if(countryName.equals(fuelP.country_name)) {
	    		System.out.println("getFuelPriceFromObjBRE - IF - COUNTRY NAME "+ fuelP.country_name);
				return fuelP;
			}
		}
		return null;
	}
	    
	public TollsPriceCountryBRE_Model getTollsPriceFromObjBRE(String countryName, TollsBRE_Model fuelbre) {
		List<TollsPriceCountryBRE_Model> tollsPriceCountry = fuelbre.tollsPriceCountry;
		for(TollsPriceCountryBRE_Model tollsP : tollsPriceCountry) {
			if(countryName.equals(tollsP.country_name)) {
	    		System.out.println("getTollsPriceFromObjBRE - IF - COUNTRY NAME "+ tollsP.country_name);
				return tollsP;
			}
		}
		return null;
	}
	
	//****************************************************************************************************************************//
	// API_DISTANCE - CALL
	//****************************************************************************************************************************//
	
 	public GoogleAPIDistanceResponse_Dto getLatLongForDistance(JSONObject object, String patterns, SimulationRequest_Dto simulationDto) {
    	Map latlong_map = null;
		GoogleAPIDistanceResponse_Dto distanceResponse_dto = null;
    	SimulationRequest_Dto simulation = new SimulationRequest_Dto();
    	Map<Integer, String> latlongHistory_map = new HashMap<Integer, String>();
    	int count = 0;
    	
    	if(patterns.equals(ANALYSE_PATTERN_START)) {
    		latlong_map = ((Map)object.get(ANALYSE_ELEMENT_STARTLOCATION));
    	}
    	else if(patterns.equals(ANALYSE_PATTERN_END)) {
    		latlong_map = ((Map)object.get(ANALYSE_ELEMENT_ENDLOCATION));
    	}
    	
    	String latilongFrom = latlong_map.get("lat").toString();
    	latilongFrom = latilongFrom+","+latlong_map.get("lng").toString();
    	count++;
    	latlongHistory_map.put(count, latilongFrom);
    	
    	if(latlongHistory_map.size() == 2) {
    		simulation.address_origin = latlongHistory_map.get(1);
    		simulation.address_destination = latlongHistory_map.get(2);
    		simulation.unity_measurement_distance_txt = simulationDto.unity_measurement_distance_txt;
    		try {
    			String typeAPI = GoogleAPI_Constants.API_DISTANCE;
    			ResponseEntity<String> distanceResponse = connectionGoogle.connectionGoogleAPI(simulation, null, typeAPI);
    			
    			if (distanceResponse.getStatusCode() == HttpStatus.OK) {
    				String jsonData = distanceResponse.getBody();
    		    	JSONParser parser = new JSONParser();
    		    	JSONObject jsonObject = (JSONObject) parser.parse(jsonData);
    		    	
    		    	if(jsonObject.get("status").equals("OK")) {
    		    		distanceResponse_dto = analyzeDistance_Component.getDistanceDataByJson(jsonObject,simulation);
    		    		System.out.println(" ================================================== ");
    	    			System.out.println(" address_origin "+ simulation.address_origin);
    	    			System.out.println(" address_destination "+ simulation.address_destination);
    	    	    	System.out.println(" distance "+ distanceResponse_dto.distance);
    	    	    	System.out.println(" ================================================== ");
    	    	    	latlongHistory_map = new HashMap<Integer, String>();
    	    	    	count = 0;
    		    	}
    			}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	return distanceResponse_dto;
	}

}
