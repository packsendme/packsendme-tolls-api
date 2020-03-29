package com.packsendme.api.google.component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.packsendme.api.google.dao.Tolls_DAO;
import com.packsendme.lib.common.constants.GoogleAPI_Constants;
import com.packsendme.lib.distance.response.dto.DistanceResponse_Dto;
import com.packsendme.lib.simulation.request.dto.SimulationRequest_Dto;
import com.packsendme.lib.tolls.response.dto.TollsCostsResponse_Dto;
import com.packsendme.lib.tolls.response.dto.TollsCountryResponse_Dto;
import com.packsendme.lib.tolls.response.dto.TollsResponse_Dto;

@Component
@ComponentScan("com.packsendme.api.google.dao")
public class AnalyzeTollsData_Component {
	
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
	private final String ANALYSE_ELEMENT_VALUE = "value";

	
	@Autowired
	private Tolls_DAO toll_dao;
	
	@Autowired
	private ConnectionGoogleAPI_Component connectionGoogle;
	
	private DistanceResponse_Dto distanceResponse_dto = new DistanceResponse_Dto();

	
	@Autowired
	private AnalyzeDistanceData_Component analyzeDistance_Component;
	
	private Map<Integer, String> latlongHistory_map = new HashMap<Integer, String>();
	private int count = 0;
	
	public TollsResponse_Dto analyzeJsonTolls(JSONObject jsonObject, SimulationRequest_Dto simulation){
		int tolls = 0;
        String countryName = null, countryNameChange = null;
        String lati_long_start = null, lati_long_end = null, distanceUniqueS = null;
        double distanceUnique = 0.0;
        

  		Map<String, TollsCountryResponse_Dto> countryTolls_map = new HashMap<String, TollsCountryResponse_Dto>();
  		TollsCountryResponse_Dto tollsCountry_Dto = new TollsCountryResponse_Dto();
        TollsCostsResponse_Dto tollsCosts_Dto = new TollsCostsResponse_Dto(); 
        TollsResponse_Dto tollsResponse_Dto = new TollsResponse_Dto();

        try {
	        JSONArray jsonRoutes = (JSONArray) jsonObject.get(ANALYSE_ARRAY_ROUTES);
			Iterator<JSONObject> itRoutes = jsonRoutes.iterator();
			JSONArray jsonSteps = null;
			while (itRoutes.hasNext()) {
				JSONObject jsonLegs = itRoutes.next();
				JSONArray jsonArrayLegs = (JSONArray) jsonLegs.get(ANALYSE_ARRAY_LEGS);  
				
			    for (Iterator itLegs = jsonArrayLegs.iterator(); itLegs.hasNext();) {
			    	JSONObject jsonStepsX = (JSONObject) itLegs.next();
			    	
			    	// GET DISTANCE
		    		Map distance_map = ((Map)jsonStepsX.get(ANALYSE_ELEMENT_DISTANCE));
		        	distanceUniqueS = distance_map.get(ANALYSE_ELEMENT_VALUE).toString();
		        	distanceUnique = Double.parseDouble(distanceUniqueS);
			    	
		    		// Find Distance
		    		distanceResponse_dto = getLatLongForDistance(jsonStepsX, ANALYSE_PATTERN_START, simulation);
			    	
        	    	String countryOrigin = jsonStepsX.get(ANALYSE_ELEMENT_ADDRESS).toString();
        	    	countryName = subStringCountryOrigin(countryOrigin);
			    	jsonSteps = (JSONArray) jsonStepsX.get(ANALYSE_ARRAY_STEPS);  //steps   
				}
			    
				for (Iterator itSteps = jsonSteps.iterator(); itSteps.hasNext();) {
				    JSONObject jsonHtmlInst = (JSONObject) itSteps.next();
				    String scheme = ((String) jsonHtmlInst.get(ANALYSE_ELEMENT_HTML)).trim();
				    
					// Find Distance
				    if(countryNameChange != null) {
					    if(countryNameChange.equals(countryName)) {
			    			distanceResponse_dto = getLatLongForDistance(jsonHtmlInst, ANALYSE_PATTERN_START, simulation);
			    			System.out.println(" Distance Change Country "+ distanceResponse_dto.distance);
			    			tollsCountry_Dto.distance_country = distanceResponse_dto.getDistance();
			    		}
				    }
 
				    // Analyze Change Country in Direction JSON-GOOGLE
				    if (analyzeContain(scheme,ANALYSE_PATTERN_COUNTRY) == true){
				    	if(tolls > 0) {
				    		tollsCountry_Dto.name_country = countryName;
				    		tollsCountry_Dto.toll_amount = tolls;
				    		// Find Distance
				    		distanceResponse_dto = getLatLongForDistance(jsonHtmlInst, ANALYSE_PATTERN_END, simulation);
				    		tollsCountry_Dto.distance_country = distanceResponse_dto.getDistance();
				    		//Find Tolls Price by Country
				    		tollsCosts_Dto = toll_dao.find(countryName);
				    		tollsCountry_Dto.costsTolls = tollsCosts_Dto;
				    		countryTolls_map.put(countryName, tollsCountry_Dto);
				    		tolls = 0;
				    	}
			    		countryName = subStringCountry(scheme);
			    		countryNameChange = countryName;
				    }
				    // Analyze Tolls in Direction JSON-GOOGLE
				    if (analyzeContain(scheme,ANALYSE_PATTERN_TOLLS) == true){
				    	tolls++;
				    }
				}
		    	if(tolls > 0) {
		    		tollsCountry_Dto.name_country = countryName;
		    		tollsCountry_Dto.toll_amount = tolls;
		    		// Find Distance
		    		tollsCountry_Dto.distance_country = distanceUnique;
		    		//Find Tolls Price by Country
		    		tollsCosts_Dto = toll_dao.find(countryName);
		    		tollsCountry_Dto.costsTolls = tollsCosts_Dto;
		    		countryTolls_map.put(countryName, tollsCountry_Dto);
		    	}
			}
			if (countryTolls_map.size() > 0) {
				tollsResponse_Dto.status_tolls = true;
				tollsResponse_Dto.countryTolls = countryTolls_map;
			}
			else{
				tollsResponse_Dto.status_tolls = false;
				tollsResponse_Dto.countryTolls = null;
			}
			return tollsResponse_Dto;
        }
        catch (Exception e) {
        	e.printStackTrace();
        	return null;
		}
    }
	
	public DistanceResponse_Dto getLatLongForDistance(JSONObject object, String patterns, SimulationRequest_Dto simulationDto) {
    	Map latlong_map = null;
		DistanceResponse_Dto distanceResponse_dto = null;
    	SimulationRequest_Dto simulation = new SimulationRequest_Dto();
    	
    	if(patterns.equals(ANALYSE_PATTERN_START)) {
    		latlong_map = ((Map)object.get(ANALYSE_ELEMENT_STARTLOCATION));
    	}
    	else if(patterns.equals(ANALYSE_PATTERN_END)) {
    		latlong_map = ((Map)object.get(ANALYSE_ELEMENT_ENDLOCATION));
    	}
    	
    	String latilongFrom = latlong_map.get("lat").toString();
    	latilongFrom = latilongFrom+","+latlong_map.get("lng").toString();
    	
    	System.out.println(" getLatLongForDistance "+ latilongFrom);
    	count++;
    	System.out.println(" count "+ count);

    	latlongHistory_map.put(count, latilongFrom);
    	
    	 Iterator<Entry<Integer, String>> itr1 = latlongHistory_map.entrySet().iterator(); 
         while (itr1.hasNext()) { 
             Map.Entry pair = itr1.next(); 
             System.out.println(pair.getKey() + " : " + pair.getValue());
         } 
		
    	System.out.println(" SIZE MAP "*+ latlongHistory_map.size());
    	if(latlongHistory_map.size() == 2) {
    		simulation.address_origin = latlongHistory_map.get(1);
    		simulation.address_destination = latlongHistory_map.get(2);
    		simulation.unity_measurement_distance_txt = simulationDto.unity_measurement_distance_txt;
    		try {
    			distanceResponse_dto = getDistanceGoogleParser(simulation);
    	    	System.out.println(" distance "+ distanceResponse_dto.distance);

    			latlongHistory_map = new HashMap<Integer, String>();
    			count = 0;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	return distanceResponse_dto;
	}
	
	public DistanceResponse_Dto getDistanceGoogleParser(SimulationRequest_Dto simulation) throws ParseException {
		DistanceResponse_Dto distanceResponse_dto = null;
		String typeAPI = GoogleAPI_Constants.API_DISTANCE;
		ResponseEntity<String> distanceResponse = connectionGoogle.connectionGoogleAPI(simulation, typeAPI);
		
		if (distanceResponse.getStatusCode() == HttpStatus.OK) {
			String jsonData = distanceResponse.getBody();
	    	JSONParser parser = new JSONParser();
	    	JSONObject jsonObject = (JSONObject) parser.parse(jsonData);
	    	
	    	if(jsonObject.get("status").equals("OK")) {
	    		distanceResponse_dto = analyzeDistance_Component.analyzeJsonDistance(jsonObject,simulation);
	    	}
		}
    	return distanceResponse_dto;
	}

	
	public boolean analyzeContain(String scheme, String parse) {
	  boolean bool = scheme.contains(parse);
	  return bool;
	}
	
	// Country change in JSON
	public String subStringCountry(String contain) {
		int startMatch = 0;
		int endMatch = 0;
		String new1 = StringUtils.substring(contain, 0, contain.length() - 6);
		
		Pattern pattern = Pattern.compile(ANALYSE_PATTERN_COUNTRY);
		Matcher matcher = pattern.matcher(new1);
		while(matcher.find()){
			startMatch = matcher.start();
			endMatch = matcher.end();
		}
		String new2 = StringUtils.substring(new1, startMatch, new1.length() + endMatch);
		return StringUtils.substringAfter(new2, ANALYSE_PATTERN_COUNTRY).trim();
	}

	// Country Origin destination
	public String subStringCountryOrigin(String contain) {
		
		int startMatch = 0;
		int endMatch = 0;
		
		Pattern pattern = Pattern.compile(",");
		Matcher matcher = pattern.matcher(contain);
		while(matcher.find()){
			startMatch = matcher.start();
			endMatch = matcher.end();
		}
		String new2 = StringUtils.substring(contain, startMatch, contain.length() + endMatch);
		String new3 = StringUtils.substringAfter(new2, ",").trim();
		return new3;
	}
}
