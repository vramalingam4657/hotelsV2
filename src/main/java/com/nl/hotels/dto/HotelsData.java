package com.nl.hotels.dto;

import com.amadeus.resources.HotelOffer;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class HotelsData implements Serializable {
    String name;
    HotelOffer.AddressType address;
    String phone;
    Double rate;
}
