package com.packsendme.api.google.controller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="packsendme-businessrule-sa-server")
public interface IBusinessManager_SA_Client {

	@GetMapping("/businessrule/sa/tollsfuel")
	public ResponseEntity<?> getTollsFuelBRE_SA(
			@RequestHeader("isoLanguageCode") String isoLanguageCode, 
			@RequestHeader("isoCountryCode") String isoCountryCode,
			@RequestHeader("isoCurrencyCode") String isoCurrencyCode,
			@RequestHeader("originApp") String originApp,
			@Validated @RequestParam ("name_rule") String name_rule);
	
}
