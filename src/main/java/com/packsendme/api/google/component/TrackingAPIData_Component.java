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
import com.packsendme.lib.common.constants.GoogleAPI_Constants;
import com.packsendme.lib.simulation.request.dto.SimulationRequest_Dto;
import com.packsendme.tollsfuel.bre.model.TollsFuelBRE_Model;
import com.packsendme.tollsfuel.bre.rule.price.model.TollsFuelPriceCountryBRE_Model;

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
	
	Map<Integer, String> latlongHistory_map = new HashMap<Integer, String>();
	int count = 0;
	
	@Autowired
	private ConnectionAPI_Component connectionGoogle;
	
	@Autowired
	TollsFuelTrackingData_Component tollsFuelData_Component;
	
	@Autowired
	private DistanceAPIData_Component analyzeDistance_Component;
	
	
	public GoogleAPITrackingResponse_Dto getTrackingDataByJson(JSONObject trackingJsonObj, SimulationRequest_Dto simulation, TollsFuelBRE_Model tollsFuel_Cache){
		int tolls_amount = 0;
        String countryName = null, countryNameChange = null;
        JSONObject jsonHtmlInstLast = null;
        GoogleAPIDistanceResponse_Dto distance_dto = new GoogleAPIDistanceResponse_Dto();
  		Map<String, RoadwayTrackingResponse_Dto> tracking_map = new HashMap<String, RoadwayTrackingResponse_Dto>();
  		RoadwayTrackingResponse_Dto  trackingResponse_Dto = null; 
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
 
				    // Change Country in Direction/Country JSON-GOOGLE
				    if (separationElementObj.analyzeContain(scheme,ANALYSE_PATTERN_COUNTRY) == true){
				    	// Find Distance
				    	distance_dto = getLatLongForDistance(jsonHtmlInst, ANALYSE_PATTERN_END, simulation);
				    	trackingResponse_Dto = setTrackingResponse_Dto(countryName, tolls_amount, tollsFuel_Cache, distance_dto);
						tolls_amount = 0;
					    tracking_map.put(countryName, trackingResponse_Dto);
				    	countryName = separationElementObj.subStringCountry(scheme);
				    	countryNameChange = countryName;
				    }
			    	// Analyze Tolls in Direction JSON-GOOGLE
			    	if (separationElementObj.analyzeContain(scheme,ANALYSE_PATTERN_TOLLS) == true){
				    	tolls_amount++;
				    }
				    jsonHtmlInstLast = jsonHtmlInst;
				}
				
			    System.out.println(" ====================================================================");
			    System.out.println(" 2 ENTROU AQUI");
				distance_dto = getLatLongForDistance(jsonHtmlInstLast, ANALYSE_PATTERN_END, simulation);
				System.out.println(" 2 distance_dto "+ distance_dto.distance);
				System.out.println(" 2 countryName "+ countryName);
				System.out.println(" 2 tolls_amount "+ tolls_amount);
				System.out.println(" 2 tollsFuel_Cache "+ tollsFuel_Cache.name_rule);
		    	trackingResponse_Dto = setTrackingResponse_Dto(countryName, tolls_amount, tollsFuel_Cache, distance_dto);
			    tracking_map.put(countryName, trackingResponse_Dto);
			    System.out.println(" 2 SAIU AQUI");
			    System.out.println(" ====================================================================");

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
	
	
	
	private RoadwayTrackingResponse_Dto setTrackingResponse_Dto(String countryName, int tolls_amount, TollsFuelBRE_Model tollsFuel_Cache, GoogleAPIDistanceResponse_Dto distance) {
		RoadwayTrackingResponse_Dto trackingResponse_dto = null;

		//Find Fuel Price by Country
		TollsFuelPriceCountryBRE_Model tollsFuelObjResult = getTollsFuelPriceFromCountry(countryName.trim(),tollsFuel_Cache);
		
		if(tolls_amount > 0) {
			if(tollsFuelObjResult != null) {
				
				trackingResponse_dto = new RoadwayTrackingResponse_Dto(countryName,tolls_amount,tollsFuelObjResult.tolls_price,distance.distance,
						tollsFuelObjResult.fuel_price,tollsFuelObjResult.currency_price,tollsFuelObjResult.unity_measurement_distance);
			}
			else {
				trackingResponse_dto = new RoadwayTrackingResponse_Dto(countryName,tolls_amount,AVERAGE_PRICE_DEFAULT,distance.distance,
	    				AVERAGE_PRICE_DEFAULT,null,null);
			}
		}
		else{
			trackingResponse_dto = new RoadwayTrackingResponse_Dto(countryName,0,AVERAGE_PRICE_DEFAULT,distance.distance,
					tollsFuelObjResult.fuel_price,tollsFuelObjResult.currency_price,tollsFuelObjResult.unity_measurement_distance);
		}
		return trackingResponse_dto;
	}
	
	//****************************************************************************************************************************//
	// FIND FUEL AND TOLLS IN BRE
	//****************************************************************************************************************************//
	public TollsFuelPriceCountryBRE_Model getTollsFuelPriceFromCountry(String countryName, TollsFuelBRE_Model tollsFuelObj) {
		for(TollsFuelPriceCountryBRE_Model tollsFuelPrice : tollsFuelObj.tollsfuelPriceCountry) {
    		System.out.println("getFuelPriceFromObjBRE - COUNTRY NAME "+ countryName);
			if(countryName.equals(tollsFuelPrice.country_name)) {
				return tollsFuelPrice;
			}
		}
		return null;
	}
	    
	
	//****************************************************************************************************************************//
	// API_DISTANCE - CALL
	//****************************************************************************************************************************//
	
 	public GoogleAPIDistanceResponse_Dto getLatLongForDistance(JSONObject object, String patterns, SimulationRequest_Dto simulationDto) {
    	Map latlong_map = new HashMap();
		GoogleAPIDistanceResponse_Dto distanceResponse_dto = null;
    	SimulationRequest_Dto simulation = new SimulationRequest_Dto();
   
    	
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
