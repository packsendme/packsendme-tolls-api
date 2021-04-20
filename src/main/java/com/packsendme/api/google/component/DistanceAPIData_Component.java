package com.packsendme.api.google.component;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.packsendme.api.google.dto.SimulationRequestGoogle_Dto;
import com.packsendme.api.google.utility.SeparationElementTools;
import com.packsendme.lib.common.response.dto.api.GoogleAPIDistanceResponse_Dto;

@Component
public class DistanceAPIData_Component {
	
	
	private final String ANALYSE_NODE_ROWS = "rows";
	private final String ANALYSE_NODE_ELEMENTS = "elements";
	private final String ANALYSE_ELEMENTS_STATUS = "status";
	private final String ANALYSE_ELEMENTS_DISTANCE = "distance";
	private final String ANALYSE_ELEMENTS_TEXT = "text";
	private final String ANALYSE_ELEMENTS_VALUE = "value";

	
	public GoogleAPIDistanceResponse_Dto getDistanceDataByJson (JSONObject jsonObject, SimulationRequestGoogle_Dto simulationRequestGoogle_Dto) {
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
			            System.out.println(" ");
			            System.out.println(" ===========| JsonNode |============ "+ distanceNode);

			            distanceResponse_dto.origin = simulationRequestGoogle_Dto.origin_from;
			            distanceResponse_dto.destination = simulationRequestGoogle_Dto.destination_to;
			            String distanceF = distanceNode.path(ANALYSE_ELEMENTS_TEXT).asText();
			            String distanceM = distanceNode.path(ANALYSE_ELEMENTS_VALUE).asText();
			            distanceResponse_dto.distanceM = Double.parseDouble(distanceM);
			            distanceResponse_dto.distanceF = separationElementObj.getDistanceParse(distanceF);
			            distanceResponse_dto.measureUnit = simulationRequestGoogle_Dto.measurement;
			            distanceResponse_dto.status = status;
					}
					else {
			            distanceResponse_dto.origin = simulationRequestGoogle_Dto.origin_from;
			            distanceResponse_dto.destination = simulationRequestGoogle_Dto.destination_to;
			            distanceResponse_dto.measureUnit = simulationRequestGoogle_Dto.measurement;
			            distanceResponse_dto.distanceM = 0.0;
			            distanceResponse_dto.distanceF = 0.0;
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
