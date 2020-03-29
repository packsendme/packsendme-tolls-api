package com.packsend.api.google.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TollsCosts_Dto {
	
	public Double average_price_toll;
	public String currency_price;
	public boolean status;
}
