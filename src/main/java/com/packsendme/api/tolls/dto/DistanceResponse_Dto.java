package com.packsendme.api.tolls.dto;

import java.io.Serializable;

public class DistanceResponse_Dto implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String destination;
	private String origin;
	private int distanceInt;
	private String distanceText;
	private String tolls;
	private String status;
	
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	public int getDistanceInt() {
		return distanceInt;
	}
	public void setDistanceInt(int distanceInt) {
		this.distanceInt = distanceInt;
	}
	public String getDistanceText() {
		return distanceText;
	}
	public void setDistanceText(String distanceText) {
		this.distanceText = distanceText;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTolls() {
		return tolls;
	}
	public void setTolls(String tolls) {
		this.tolls = tolls;
	}

}
