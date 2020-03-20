package com.packsendme.api.tolls.component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import com.packsendme.api.tolls.dao.Tolls_DAO;
import com.packsendme.api.tolls.dto.TollsResponse_Dto;

@Component
@ComponentScan("com.packsendme.api.tolls.dto")
public class AnalyzeData_Component {
	
	public final String ANALYSE_PATTERN_TOLLS = "Toll";
	public final String ANALYSE_PATTERN_COUNTRY = "Entering";
	
	@Autowired
	private TollsResponse_Dto tollsAnalyzeDto;
	
	@Autowired
	private Tolls_DAO toll_dao;
	
	public TollsResponse_Dto analyzeJsonTolls(JSONObject jsonObject){
		int tolls = 0;
        Map<String, Integer> mapsAnalise = new HashMap<String, Integer>();
        String countryName = null;

        try {
	        JSONArray jsonRoutes = (JSONArray) jsonObject.get("routes");
			Iterator<JSONObject> itRoutes = jsonRoutes.iterator();
			JSONArray jsonSteps = null;
			while (itRoutes.hasNext()) {
				JSONObject jsonLegs = itRoutes.next();
				JSONArray jsonArrayLegs = (JSONArray) jsonLegs.get("legs");  
			    
			    for (Iterator itLegs = jsonArrayLegs.iterator(); itLegs.hasNext();) {
			    	JSONObject jsonStepsX = (JSONObject) itLegs.next();
        	    	String countryOrigin = jsonStepsX.get("start_address").toString();
        	    	countryName = subStringCountryOrigin(countryOrigin);
			    	jsonSteps = (JSONArray) jsonStepsX.get("steps");  //steps   
				}
			    
				for (Iterator itSteps = jsonSteps.iterator(); itSteps.hasNext();) {
				    JSONObject jsonHtmlInst = (JSONObject) itSteps.next();
				    String scheme = ((String) jsonHtmlInst.get("html_instructions")).trim();
				    
				    // Analyze Change Country in Direction JSON-GOOGLE
				    if (analyzeContain(scheme,ANALYSE_PATTERN_COUNTRY) == true){
				    	if(tolls > 0) {
				    		mapsAnalise.put(countryName, tolls);
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
		    		mapsAnalise.put(countryName, tolls);
		    	}
			}
			if (mapsAnalise.size() > 0) {
				tollsAnalyzeDto.status_tolls = true;
				tollsAnalyzeDto.tolls = mapsAnalise;
				//Analyze-TollCosts by Country / Find Costs Tolls
				tollsAnalyzeDto.costsTolls = toll_dao.find(tollsAnalyzeDto);
			}
			return tollsAnalyzeDto;
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
		return StringUtils.substringAfter(new2, ANALYSE_PATTERN_COUNTRY);
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
		String new3 = StringUtils.substringAfter(new2, ",");
		return new3;
	}
}
