package com.packsendme.api.tolls.test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest
public class APITollsApplication_Tests {

	private String url_json = "src/test/resources/tolls_request_v1.json";
	
	@Test
	public void inputJsonFileSouthAmerica() throws IOException, URISyntaxException {
		ObjectMapper mapper = new ObjectMapper();
		File file = new File(url_json);
	/*
		if (file.length() != 0) {
			String absolutePath = file.getAbsolutePath();
			TollsResponse_Dto obj = mapper.readValue(new File(absolutePath), TollsResponse_Dto.class);
			Assert.notNull(obj);
		}
	*/
	}

}
