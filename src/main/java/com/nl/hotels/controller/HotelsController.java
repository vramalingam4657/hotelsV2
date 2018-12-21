package com.nl.hotels.controller;
import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.resources.HotelOffer;
import com.amadeus.shopping.hotel.HotelOffers;
import com.nl.hotels.dto.HotelsData;
import com.nl.hotels.dto.HotelsDataResponse;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.*;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;

@RestController
@ComponentScan
public class HotelsController {

    private static final String apiKey = "gWLmb3p7fV7sns353qhNM0exRHp1gtWq";
    private static final String apiSecret = "gbeKXu11DdU85GDC";

    @GET
    @Produces("text/json")
    @RequestMapping("/hotels")
    public HotelsDataResponse getHotels(
            @RequestParam(name = "airport", defaultValue = "YVR") String airport,
            @RequestParam(name = "date", defaultValue = "06/02/2019") String date
            ) {
        HotelOffer[] offers;
        try {
            Amadeus amadeus = Amadeus
                    .builder(apiKey, apiSecret)
                    .build();

            offers = amadeus.shopping.hotelOffers.get(
                    Params.with("cityCode", airport)
                            .and("checkInDate", date)
                            .and("checkOutDate", date)
            );
        } catch (Exception e) {
            System.out.println("Exception in amadeus call " + e.getMessage());
            e.printStackTrace();
            return null;
        }

        ArrayList<HotelsData> hotels = new ArrayList<HotelsData>();
        for (HotelOffer ho: offers) {
            hotels.add(new HotelsData(
                    ho.getHotel().getName(),
                    ho.getHotel().getAddress(),
                    ho.getHotel().getContact().getPhone(),
                    Double.valueOf(ho.getOffers()[0].getPrice().getTotal())
                    ));
        }

        List<HotelsData> sortedHotels = (List<HotelsData>)
                (hotels.stream()
                .sorted(Comparator.comparingDouble(HotelsData::getRate))
                .collect(Collectors.toList()));
        int numHotels = sortedHotels.size();
        if (numHotels > 3) {
            // truncate to max 3 elements, removing elements from the end
            for (int i=numHotels-1; i>2; i--) {
                sortedHotels.remove(i);
            }
        }

        HotelsDataResponse response = new HotelsDataResponse(sortedHotels);
        return response;
    }

    @GET
    @Produces("text/json")
    @RequestMapping("/hotelsTest")
    public HotelsDataResponse getFlightsTestData() {
        ArrayList<HotelsData> testData = new ArrayList<HotelsData>();
        HotelOffer.AddressType addressType = null;

        testData.add(new HotelsData("name1", addressType, "phone1", 50.00));
        testData.add(new HotelsData("name2", addressType, "phone2", 70.99));
        testData.add(new HotelsData("name3", addressType, "phone3", 250.01));

        HotelsDataResponse response = new HotelsDataResponse(testData);
        return response;
    }
}
