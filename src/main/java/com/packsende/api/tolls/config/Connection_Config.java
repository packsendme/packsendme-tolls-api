package com.packsende.api.tolls.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
//@ConfigurationProperties(prefix="packsendme-roadwaysa-server")
public class Connection_Config {

	@Value(value = "${google.api.direction}")
	public String direction_api_url;
	
	@Value(value = "${google.api.distance}")
	public String distance_api_url;
	
	@Value(value = "${google.api.key}")
	public String key_api;
	
	
}
