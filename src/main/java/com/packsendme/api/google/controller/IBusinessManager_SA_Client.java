package com.packsendme.api.google.controller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="packsendme-businessrule-sa-server")
public interface IBusinessManager_SA_Client {

	@GetMapping("/businessrule/sa/tolls/{name_rule}")
	public ResponseEntity<?> getTollsBRE_SA(@Validated @PathVariable ("name_rule") String name_rule);
	
	@GetMapping("/businessrule/sa/fuel/{name_rule}")
	public ResponseEntity<?> getFuelBRE_SA(@Validated @PathVariable ("name_rule") String name_rule);	

}
