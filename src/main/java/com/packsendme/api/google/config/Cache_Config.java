package com.packsendme.api.google.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Cache_Config {

	@Value(value = "${redis.cache.tollsfuelBRE_SA}")
	public String tollsfuelBRE_SA;

	@Value(value = "${redis.cache.tollsfuelBRE_EURO}")
	public String tollsfuelBRE_EURO;

	@Value(value = "${redis.cache.tollsfuelBRE_NA}")
	public String tollsfuelBRE_NA;

}

