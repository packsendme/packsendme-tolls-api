package com.packsendme.api.tolls.component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import com.packsendme.api.tolls.dao.Tolls_DAO;
import com.packsendme.api.tolls.repository.TollsCosts_Model;
import com.packsendme.lib.tolls.response.dto.TollsCostsResponse_Dto;
import com.packsendme.lib.tolls.response.dto.TollsResponse_Dto;

@Component
@ComponentScan("com.packsendme.api.tolls.dao")
public class AnalyzeTollsData_Component {
	
	private final String ANALYSE_PATTERN_TOLLS = "Toll";
	private final String ANALYSE_PATTERN_COUNTRY = "Entering";
	
	private final String ANALYSE_ARRAY_ROUTES = "routes";
	private final String ANALYSE_ARRAY_LEGS = "legs";
	private final String ANALYSE_ELEMENT_ADDRESS = "start_address";
	private final String ANALYSE_ARRAY_STEPS = "steps";
	private final String ANALYSE_ELEMENT_HTML = "html_instructions";



	
	private TollsResponse_Dto tollsCosts_Dto = new TollsResponse_Dto();
	
	@Autowired
	private Tolls_DAO toll_dao;
	
	public TollsResponse_Dto analyzeJsonTolls(JSONObject jsonObject){
		int tolls = 0;
        Map<String, Integer> countryTolls_map = new HashMap<String, Integer>();
        String countryName = null;

        try {
	        JSONArray jsonRoutes = (JSONArray) jsonObject.get(ANALYSE_ARRAY_ROUTES);
			Iterator<JSONObject> itRoutes = jsonRoutes.iterator();
			JSONArray jsonSteps = null;
			while (itRoutes.hasNext()) {
				JSONObject jsonLegs = itRoutes.next();
				JSONArray jsonArrayLegs = (JSONArray) jsonLegs.get(ANALYSE_ARRAY_LEGS);  
			    
			    for (Iterator itLegs = jsonArrayLegs.iterator(); itLegs.hasNext();) {
			    	JSONObject jsonStepsX = (JSONObject) itLegs.next();
        	    	String countryOrigin = jsonStepsX.get(ANALYSE_ELEMENT_ADDRESS).toString();
        	    	countryName = subStringCountryOrigin(countryOrigin);
			    	jsonSteps = (JSONArray) jsonStepsX.get(ANALYSE_ARRAY_STEPS);  //steps   
				}
			    
				for (Iterator itSteps = jsonSteps.iterator(); itSteps.hasNext();) {
				    JSONObject jsonHtmlInst = (JSONObject) itSteps.next();
				    String scheme = ((String) jsonHtmlInst.get(ANALYSE_ELEMENT_HTML)).trim();
				    
				    // Analyze Change Country in Direction JSON-GOOGLE
				    if (analyzeContain(scheme,ANALYSE_PATTERN_COUNTRY) == true){
				    	if(tolls > 0) {
				    		countryTolls_map.put(countryName, tolls);
				    		tolls = 0;
				    	}
			    		countryName = subStringCountry(scheme);
				    }
				    // Analyze Tolls in Direction JSON-GOOGLE
				    if (analyzeContain(scheme,ANALYSE_PATTERN_TOLLS) == true){
				    	tolls++;
				    }
				}
		    	if(tolls > 0) {
		    		countryTolls_map.put(countryName, tolls);
		    	}
			}
			if (countryTolls_map.size() > 0) {
				tollsCosts_Dto.status_tolls = true;
				tollsCosts_Dto.countryTolls = countryTolls_map;
				//Analyze-TollCosts by Country / Find Costs Tolls
				tollsCosts_Dto.costsTolls = toll_dao.find(tollsCosts_Dto);
			}
			return tollsCosts_Dto;
        }
        catch (Exception e) {
        	e.printStackTrace();
        	return null;
		}
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
