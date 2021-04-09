package com.packsendme.api.google.component;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.packsendme.api.google.dto.SimulationRequestGoogle_Dto;
import com.packsendme.api.google.utility.SeparationElementTools;
import com.packsendme.lib.common.constants.generic.GoogleAPI_Constants;
import com.packsendme.lib.common.response.dto.api.GoogleAPIDistanceResponse_Dto;
import com.packsendme.lib.common.response.dto.api.GoogleAPITrackingResponse_Dto;
import com.packsendme.lib.common.response.dto.api.RoadwayTrackingResponse_Dto;
import com.packsendme.roadbrewa.entity.TollsFuel;

@Component
public class TrackingAPIData_Component {

	private final double AVERAGE_PRICE_DEFAULT = 0.0;

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
	private final String ANALYSE_ELEMENT_DURATION = "duration";
	private final String ANALYSE_ELEMENT_VALUE = "value";
	private final String ANALYSE_ELEMENT_TXT = "text";

	
	Map<Integer, String> latlongHistory_map = new HashMap<Integer, String>();
	int count = 0;
	
	@Autowired
	private ConnectionAPI_Component connectionGoogle;
	
	@Autowired
	TollsFuelTrackingData_Component tollsFuelData_Component;
	
	@Autowired
	private DistanceAPIData_Component analyzeDistance_Component;
	
	
	public GoogleAPITrackingResponse_Dto getTrackingDataByJson(JSONObject trackingJsonObj, SimulationRequestGoogle_Dto simulationRequestDto){
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
			    	String distanceS = totalDistance_map.get(ANALYSE_ELEMENT_VALUE).toString();
			    	googleTrackingResponse_dto.distance_total = Double.parseDouble(distanceS);  //separationElementObj.getDistanceParse(distanceS);
				    
			    	System.out.println(" ");
			    	System.out.println(" ===============| DISTANCIA F | =====================================================");
			    	System.out.println(" | DISTANCIA F | "+ totalDistance_map.get(ANALYSE_ELEMENT_TXT).toString());
			    	System.out.println(" ====================================================================================");
			    	System.out.println(" ");
	
			    	googleTrackingResponse_dto.distanceF = "RICARDO"; // totalDistance_map.get(ANALYSE_ELEMENT_TXT).toString();
			    	
			    	// GET DURATION / SECOND
			    	Map duration_map = ((Map)jsonStepsX.get(ANALYSE_ELEMENT_DURATION));
			    	String durationS = duration_map.get(ANALYSE_ELEMENT_VALUE).toString();
			    	googleTrackingResponse_dto.duration = Integer.parseInt(durationS);
			    	googleTrackingResponse_dto.durationF = duration_map.get(ANALYSE_ELEMENT_TXT).toString();
			    	
			    	// Find Distance (Origin Location)
			    	distance_dto = new GoogleAPIDistanceResponse_Dto();
		        	distance_dto = getLatLongForDistance(jsonStepsX, ANALYSE_PATTERN_START, simulationRequestDto);
			    	
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
					    	distance_dto = new GoogleAPIDistanceResponse_Dto();
					    	distance_dto = getLatLongForDistance(jsonHtmlInst, ANALYSE_PATTERN_START, simulationRequestDto);
					    	countryNameChange = null;
			    		}
				    }
 
				    // Change Country in Direction/Country JSON-GOOGLE
				    if (separationElementObj.analyzeContain(scheme,ANALYSE_PATTERN_COUNTRY) == true){
				    	// Find Distance
				    	distance_dto = new GoogleAPIDistanceResponse_Dto();
				    	distance_dto = getLatLongForDistance(jsonHtmlInst, ANALYSE_PATTERN_END, simulationRequestDto);
				    	trackingResponse_Dto = setTrackingResponse_Dto(countryName, tolls_amount, distance_dto);
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

			    System.out.println(" ===============| getTrackingDataByJson | =====================================================");
			    distance_dto = new GoogleAPIDistanceResponse_Dto();
		    	distance_dto = getLatLongForDistance(jsonHtmlInstLast, ANALYSE_PATTERN_END, simulationRequestDto);
			    System.out.println(" ===============| RETORNO ===============| ");
		    	trackingResponse_Dto = setTrackingResponse_Dto(countryName, tolls_amount, distance_dto);
		    	System.out.println(" * distance_dto M "+ distance_dto.distanceM);
				System.out.println(" * distance_dto F "+ distance_dto.distanceF);
				System.out.println(" * countryName "+ countryName);
				System.out.println(" * tolls_amount "+ tolls_amount);
			    
			    tracking_map.put(countryName, trackingResponse_Dto);
			    System.out.println(" ================================================================================================ ");
			    trackingResponse_Dto = null;
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
	
	
	
	private RoadwayTrackingResponse_Dto setTrackingResponse_Dto(String countryName, int tolls_amount, GoogleAPIDistanceResponse_Dto distance) {
		RoadwayTrackingResponse_Dto trackingResponse_dto = null;
		DecimalFormat df2 = new DecimalFormat("#.###");
		
		//Find Fuel Price by Country
		TollsFuel tollsFuelObjResult = getTollsFuelPriceFromCountry(countryName.trim());
		
		if(tollsFuelObjResult != null) {
			trackingResponse_dto = new RoadwayTrackingResponse_Dto(countryName,tolls_amount,tollsFuelObjResult.tolls_price,distance.distanceF,
			distance.distanceM, Double.valueOf(df2.format(tollsFuelObjResult.fuelGasoline_price)), Double.valueOf(df2.format(tollsFuelObjResult.fuelDiesel_price)), tollsFuelObjResult.currency,distance.measureUnit);
		}
		else {
			trackingResponse_dto = new RoadwayTrackingResponse_Dto(countryName,tolls_amount,AVERAGE_PRICE_DEFAULT,distance.distanceF, distance.distanceM,
    				AVERAGE_PRICE_DEFAULT,AVERAGE_PRICE_DEFAULT, "", "");
		}
		return trackingResponse_dto;
	}
	
	//****************************************************************************************************************************//
	// FIND FUEL AND TOLLS IN BRE
	//****************************************************************************************************************************//
	public TollsFuel getTollsFuelPriceFromCountry(String countryName) {
		TollsFuel tollsfuelPriceCountry = tollsFuelData_Component.getTollsFuelBREFromCache(countryName);
		return tollsfuelPriceCountry;
	}
	    
	
	//****************************************************************************************************************************//
	// API_DISTANCE - CALL
	//****************************************************************************************************************************//
	
 	public GoogleAPIDistanceResponse_Dto getLatLongForDistance(JSONObject object, String patterns, SimulationRequestGoogle_Dto simulationDto) {
    	Map latlong_map = new HashMap();
		GoogleAPIDistanceResponse_Dto distanceResponse_dto = null;
    	SimulationRequestGoogle_Dto simulationGoogle_Dto = new SimulationRequestGoogle_Dto();
   
    	
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
    		simulationGoogle_Dto.origin_from = latlongHistory_map.get(1);
    		simulationGoogle_Dto.destination_to = latlongHistory_map.get(2);
    		simulationGoogle_Dto.measurement = simulationDto.measurement;
    		try {
    			String typeAPI = GoogleAPI_Constants.API_DISTANCE;
    			ResponseEntity<String> distanceResponse = connectionGoogle.connectionGoogleAPI(simulationGoogle_Dto, null, typeAPI);
    			
    			if (distanceResponse.getStatusCode() == HttpStatus.OK) {
    				String jsonData = distanceResponse.getBody();
    		    	JSONParser parser = new JSONParser();
    		    	JSONObject jsonObject = (JSONObject) parser.parse(jsonData);
    		    	
    		    	if(jsonObject.get("status").equals("OK")) {
    		    		distanceResponse_dto = analyzeDistance_Component.getDistanceDataByJson(jsonObject,simulationGoogle_Dto);
    		    		System.out.println(" ==================| getLatLongForDistance | ================================ ");
    	    			System.out.println(" address_origin "+ simulationGoogle_Dto.origin_from);
    	    			System.out.println(" address_destination "+ simulationGoogle_Dto.destination_to);
    	    	    	System.out.println(" distance "+ distanceResponse_dto.distanceF);
    	    	    	System.out.println(" distance "+ distanceResponse_dto.distanceM);
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
