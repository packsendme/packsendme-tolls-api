package com.packsendme.api.tolls.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.mongodb.MongoClientException;
import com.packsendme.api.tolls.dao.CountryDAO;
import com.packsendme.api.tolls.dto.CountryDto;
import com.packsendme.api.tolls.dto.CountryListDTO;
import com.packsendme.api.tolls.repository.CountryModel;
import com.packsendme.lib.common.constants.HttpExceptionPackSend;
import com.packsendme.lib.common.response.Response;
 

@Service
@ComponentScan(basePackages = {"com.packsendme.microservice.country.dao"})
public class CountryService {
	
	@Autowired
	private CountryDAO countryDAO;

 
	public ResponseEntity<?> saveCountry(CountryDto countryDto) throws Exception {
		Response<CountryModel> responseObj = null;
		try {
			
			CountryModel country = new CountryModel(
					countryDto.getIdcountry(), 
					countryDto.getCodcountry(), 
					countryDto.getNamecountry(), 
					countryDto.getNameimagecountry(), 
					countryDto.getFormatnumbercountry()
					);
			
			country = countryDAO.add(country);
			responseObj = new Response<CountryModel>(0,HttpExceptionPackSend.COUNTRY_CREATED.getAction(), country);
			return new ResponseEntity<>(responseObj, HttpStatus.OK);
		}
		catch (MongoClientException e) {
			e.printStackTrace();
			return new ResponseEntity<>(responseObj, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public ResponseEntity<?> saveCountryList(List<CountryModel> country) throws Exception {
		Response<CountryModel> responseObj = null;
		try {
			countryDAO.addList(country);
			responseObj = new Response<CountryModel>(0,HttpExceptionPackSend.COUNTRY_CREATED.getAction(), null);
			return new ResponseEntity<>(responseObj, HttpStatus.OK);
		}
		catch (MongoClientException e) {
			e.printStackTrace();
			return new ResponseEntity<>(responseObj, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public ResponseEntity<?> getCountriesAll() throws Exception {
		Response<CountryListDTO> responseObj = null;
		try {
			CountryListDTO countryDto = new CountryListDTO();
			List<CountryModel> countryL = countryDAO.findAll();
			
			if(countryL != null){
				countryDto.setCountries(countryL);
				responseObj = new Response<CountryListDTO>(0,HttpExceptionPackSend.COUNTRY_FOUND.getAction(), countryDto);
				return new ResponseEntity<>(responseObj, HttpStatus.OK);
			}
			else {
				responseObj = new Response<CountryListDTO>(0,HttpExceptionPackSend.COUNTRY_NOT_FOUND.getAction(), null);
				return new ResponseEntity<>(responseObj, HttpStatus.NOT_FOUND);
			}
		}
		catch (MongoClientException e ) {
			e.printStackTrace();
			responseObj = new Response<CountryListDTO>(0,HttpExceptionPackSend.FAIL_EXECUTION.getAction(), null);
			return new ResponseEntity<>(responseObj, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public ResponseEntity<?> getCountryByCod(String idcountry) throws Exception {
		Response<CountryModel> responseObj = null;
		try {
			CountryModel countryFind = new CountryModel(idcountry, null, null, null, null);
			CountryModel country = countryDAO.find(countryFind);
			
			if(country != null){
				responseObj = new Response<CountryModel>(0,HttpExceptionPackSend.COUNTRY_FOUND.getAction(), country);
				return new ResponseEntity<>(responseObj, HttpStatus.OK);
			}
			else {
				responseObj = new Response<CountryModel>(0,HttpExceptionPackSend.COUNTRY_NOT_FOUND.getAction(), null);
				return new ResponseEntity<>(responseObj, HttpStatus.NOT_FOUND);
			}
		}
		catch (MongoClientException e ) {
			e.printStackTrace();
			responseObj = new Response<CountryModel>(0,HttpExceptionPackSend.FAIL_EXECUTION.getAction(), null);
			return new ResponseEntity<>(responseObj, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public ResponseEntity<?> updateCountrByName(CountryModel countryModel) throws Exception {
		CountryModel entity = new CountryModel();
		Response<CountryModel> responseObj = null;

		//Response<AccountModel> responseObj = new Response<AccountModel>(HttpExceptionPackSend.UPDATE_USERNAME.getAction(), entity);
		try {
			entity.setCodcountry(countryModel.getCodcountry());
			CountryModel country = countryDAO.find(entity);
			if(country != null) {
				country.setCodcountry(countryModel.getCodcountry());
				country.setFormatnumbercountry(countryModel.getFormatnumbercountry());
				country.setNameimagecountry(countryModel.getNameimagecountry());
				country = countryDAO.update(country);
				return new ResponseEntity<>(responseObj, HttpStatus.OK);
			}
			else {
				return new ResponseEntity<>(responseObj, HttpStatus.NOT_FOUND);
			}
		}
		catch (MongoClientException e ) {
			return new ResponseEntity<>(responseObj, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	// Method used to testing
	public int getTotalRegister() throws Exception {
		try {
			CountryListDTO countryDto = new CountryListDTO();
			List<CountryModel> countryL = countryDAO.findAll();
			
			if(countryL != null){
				countryDto.setCountries(countryL);
				return countryL.size();
			}
			else {
				return 0;
			}
		}
		catch (MongoClientException e ) {
			e.printStackTrace();
			return -1;
		}
	}
	
}
