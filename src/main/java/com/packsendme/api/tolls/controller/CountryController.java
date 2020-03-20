package com.packsendme.api.tolls.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.packsendme.api.tolls.dto.CountryDto;
import com.packsendme.api.tolls.service.CountryService;


@RestController
public class CountryController {

	
	@Autowired
	private CountryService countryService; 
	
	
	//** BEGIN OPERATION: PAYMENT METHOD *************************************************//

	@PostMapping("/country")
	public ResponseEntity<?> addCountry(@Validated @RequestBody CountryDto countryDto) throws Exception {
		return countryService.saveCountry(countryDto);
	}


	@GetMapping("/country")
	public ResponseEntity<?> loadCountries() throws Exception {
		return countryService.getCountriesAll();
	}

	@GetMapping("/country/{idcountry}")
	public ResponseEntity<?> changePaymentMethod(
			@Validated @PathVariable ("idcountry") String idcountry) throws Exception {
		return countryService.getCountryByCod(idcountry);
	}
	
	
	//** BEGIN OPERATION: PAYMENT  *************************************************//


}
