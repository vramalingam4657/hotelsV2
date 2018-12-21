package com.nl.hotels.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nl.hotels.dto.HotelData;
import com.nl.hotels.dto.HotelsDataResponse;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;

@RestController
@ComponentScan
public class HotelsController {

    private static final String API_KEY = "gWLmb3p7fV7sns353qhNM0exRHp1gtWq";
    private static final String API_SECRET = "gbeKXu11DdU85GDC";
    private static final String TOKEN_URL = "https://test.api.amadeus.com/v1/security/oauth2/token";
    private static final String HOTELS_URL = "https://test.api.amadeus.com/v1/shopping/hotel-offers";

    @GET
    @Produces("text/json")
    @RequestMapping("/hotels")
    public HotelsDataResponse getHotels(
            @RequestParam(name = "airport", defaultValue = "YVR") String airport,
            @RequestParam(name = "date", defaultValue = "06/02/2019") String date) {
         try {
            // Get Amadeus api token using apikey and secret.
            String amadeusToken = getAmadeusToken();
            if (amadeusToken == null) {
                return null;
            }

            // Using the token and the request params, get hotels from Amadeus
            List<HotelData> hotelData = getHotels(amadeusToken, airport, date);

            // Sort the returned hotel list using the rate
            HotelData[] hotelsForSort = hotelData.toArray(new HotelData[0]);
            Arrays.sort(hotelsForSort);

            // Get the first 3 cheapest hotels
            List<HotelData> cheapestHotels = new ArrayList<>();
            for (int i = 0; i < 3 && i < hotelsForSort.length; i++) {
                cheapestHotels.add(hotelsForSort[i]);
            }

            HotelsDataResponse response = new HotelsDataResponse(cheapestHotels);
            return response;

        } catch (Exception e) {
            System.out.println("Exception " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    String getAmadeusToken() throws IOException {
        String tokenRequestBody = "grant_type=client_credentials&client_id=" + API_KEY
                + "&client_secret=" + API_SECRET;

        HttpHeaders tokenRequestHeaders = new HttpHeaders();
                tokenRequestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> request = new HttpEntity<>(tokenRequestBody, tokenRequestHeaders);

        ResponseEntity<String> response = new RestTemplate().postForEntity(
                TOKEN_URL, request, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            return null;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.getBody());
        return jsonNode.get("access_token").asText();
    }

    List<HotelData> getHotels(String amadeusToken, String airport, String date) throws IOException {
        String amadeusHotelSearchUrl = HOTELS_URL + "?cityCode=" + airport
                + "&checkInDate=" + date + "&checkOutDate=" + date;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + amadeusToken);
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

        ResponseEntity<String> response = new RestTemplate().
                exchange(amadeusHotelSearchUrl, HttpMethod.GET, requestEntity, String.class );
        if (response.getStatusCode() != HttpStatus.OK) {
            return null;
        }

        String hotelsJson = response.getBody();

        ArrayList<HotelData> hotels = new ArrayList<HotelData>();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(hotelsJson);
        JsonNode dataNode = jsonNode.get("data");
        for (final JsonNode hotelOffersNode: dataNode) {
            JsonNode hotelNode = hotelOffersNode.get("hotel");

            String hotelName = hotelNode.get("name").asText();

            JsonNode addressNode = hotelNode.get("address");
            ArrayList<String> addressLines = new ArrayList<>();
            for (final JsonNode addressLineNode: addressNode.get("lines")) {
                addressLines.add(addressLineNode.asText());
            }

            String postalCode = addressNode.get("postalCode").asText();
            String cityName = addressNode.get("cityName").asText();
            String countryCode = addressNode.get("countryCode").asText();
            String stateCode = addressNode.get("stateCode").asText();

            String rate = hotelOffersNode.get("offers").get(0).get("price").get("total").asText();

            String phone = hotelNode.get("contact").get("phone").asText();
            HotelData hotelData = new HotelData(
                    hotelName,
                    addressLines,
                    postalCode,
                    cityName,
                    countryCode,
                    stateCode,
                    phone,
                    rate);
            hotels.add(hotelData);
        }

        return hotels;
    }

    @GET
    @Produces("text/json")
    @RequestMapping("/hotelsTest")
    public HotelsDataResponse getFlightsTestData() {
        ArrayList<HotelData> testData = new ArrayList<HotelData>();

        ArrayList<String> addressLines1 = new ArrayList<String>();
        addressLines1.add("address1");
        testData.add(new HotelData("name1", addressLines1, "V5J 1P1", "Vancouver", "CA", "BC", "1-604-555-5551", "55.00"));

        ArrayList<String> addressLines2 = new ArrayList<String>();
        addressLines2.add("address2");
        testData.add(new HotelData("name2", addressLines2, "V5J 2P2", "Vancouver", "CA", "BC", "1-604-555-5552", "65.00"));

        ArrayList<String> addressLines3 = new ArrayList<String>();
        addressLines3.add("address3");
        testData.add(new HotelData("name3", addressLines3, "V5J 3P3", "Vancouver", "CA", "BC", "1-604-555-5553", "55.00"));

        HotelsDataResponse response = new HotelsDataResponse(testData);
        return response;
    }
}
