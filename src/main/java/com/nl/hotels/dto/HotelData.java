package com.nl.hotels.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class HotelData implements Serializable, Comparable {
    String name;
    private List<String> addressLines;
    private String postalCode;
    private String cityName;
    private String countryCode;
    private String stateCode;
    String phone;
    String rate;

    @Override
    public int compareTo(Object o) {
        HotelData other = (HotelData) o;
        Double thisRate = Double.valueOf(rate);
        Double otherRate = Double.valueOf(other.getRate());
        return (int)(thisRate - otherRate);
    }
}
