package com.packsendme.api.google.utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class SeparationElementTools {
	
	
	private final String ANALYSE_PATTERN_COUNTRY = "Entering";

	public String subStringCountryFrom(String contain) {
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


	public Double getDistanceParse(String contain) {
        String distanceS = StringUtils.substring(contain, 0, contain.length() - 2);
    	String formatDistance = distanceS.replace(",", ".");
        return Double.parseDouble(formatDistance);
	}
	
	
}
