package com.packsendme.api.google.component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.packsendme.lib.distance.response.dto.DistanceResponse_Dto;
import com.packsendme.lib.simulation.request.dto.SimulationRequest_Dto;

@Component
public class AnalyzeDistanceData_Component {
	
	
	private final String ANALYSE_NODE_ROWS = "rows";
	private final String ANALYSE_NODE_ELEMENTS = "elements";
	private final String ANALYSE_ELEMENTS_STATUS = "status";
	private final String ANALYSE_ELEMENTS_DISTANCE = "distance";
	private final String ANALYSE_ELEMENTS_TEXT = "text";

	
	public DistanceResponse_Dto analyzeJsonDistance(JSONObject jsonObject, SimulationRequest_Dto simulation) {
		DistanceResponse_Dto distanceResponse_dto = new DistanceResponse_Dto();
		try {
			//create ObjectMapper instance
			ObjectMapper objectMapper = new ObjectMapper();
			//read JSON like DOM Parser
			JsonNode rootNode = objectMapper.readTree(jsonObject.toString());
			JsonNode rowsNode = rootNode.path(ANALYSE_NODE_ROWS);
			for (JsonNode rowObj : rowsNode) {
				JsonNode elementsNode = rowObj.path(ANALYSE_NODE_ELEMENTS);
				for (JsonNode elementObj : elementsNode) {
					String status = elementObj.path(ANALYSE_ELEMENTS_STATUS).asText();
					
					if(status.equals("OK")) {
			            JsonNode distanceNode = elementObj.path(ANALYSE_ELEMENTS_DISTANCE);
			            distanceResponse_dto.setOrigin(simulation.address_origin);
			            distanceResponse_dto.setDestination(simulation.address_destination);
			            String distanceS = distanceNode.path(ANALYSE_ELEMENTS_TEXT).asText();
			            distanceResponse_dto.setDistance(getDistanceParse(distanceS));
			            distanceResponse_dto.setMeasureUnit(simulation.unity_measurement_distance_txt);
			            distanceResponse_dto.setStatus(status);
					}
					else {
			            distanceResponse_dto.setOrigin(simulation.address_origin);
			            distanceResponse_dto.setDestination(simulation.address_destination);
			            distanceResponse_dto.setMeasureUnit(simulation.unity_measurement_distance_txt);
			            distanceResponse_dto.setDistance(0);
			            distanceResponse_dto.setStatus(status);
					}
				}	
			}
            return distanceResponse_dto;
		}
		catch (Exception e ) {
			e.printStackTrace();
			return null;
		}
	}
	
	public int getDistanceParse(String contain) {
		int result = 0;
        String distanceS =StringUtils.substring(contain, 0, contain.length() - 2);
        result = Integer.parseInt(distanceS);
        return result;
	}
	
}
