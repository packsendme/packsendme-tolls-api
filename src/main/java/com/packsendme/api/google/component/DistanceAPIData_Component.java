package com.packsendme.api.google.component;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.packsend.api.google.dto.GoogleAPIDistanceResponse_Dto;
import com.packsendme.api.google.utility.SeparationElementTools;
import com.packsendme.lib.simulation.request.dto.SimulationRequest_Dto;

@Component
public class DistanceAPIData_Component {
	
	
	private final String ANALYSE_NODE_ROWS = "rows";
	private final String ANALYSE_NODE_ELEMENTS = "elements";
	private final String ANALYSE_ELEMENTS_STATUS = "status";
	private final String ANALYSE_ELEMENTS_DISTANCE = "distance";
	private final String ANALYSE_ELEMENTS_TEXT = "text";

	
	public GoogleAPIDistanceResponse_Dto getDistanceDataByJson (JSONObject jsonObject, SimulationRequest_Dto simulation) {
		GoogleAPIDistanceResponse_Dto distanceResponse_dto = new GoogleAPIDistanceResponse_Dto();
		SeparationElementTools separationElementObj = new SeparationElementTools();

		
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
			            distanceResponse_dto.origin = simulation.address_origin;
			            distanceResponse_dto.destination = simulation.address_destination;
			            String distanceS = distanceNode.path(ANALYSE_ELEMENTS_TEXT).asText();
			            distanceResponse_dto.distance = separationElementObj.getDistanceParse(distanceS);
			            distanceResponse_dto.measureUnit = simulation.unity_measurement_distance_txt;
			            distanceResponse_dto.status = status;
					}
					else {
			            distanceResponse_dto.origin = simulation.address_origin;
			            distanceResponse_dto.destination = simulation.address_destination;
			            distanceResponse_dto.measureUnit = simulation.unity_measurement_distance_txt;
			            distanceResponse_dto.distance = 0;
			            distanceResponse_dto.status = status;
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
	
}
