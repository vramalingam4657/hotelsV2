package com.nl.hotels.controller;


import net.bytebuddy.asm.Advice;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class HotelsControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testURLShouldGenerateTestData() {
        String url = "http://127.0.0.1:" + port + "/hotelsTest";
        System.out.println("Calling url: " + url);

        String testData = this.restTemplate.getForObject(url, String.class);

        assertThat(testData.contains("\"hotels\":[{\"name\":\"name1\",\"addressLines\":[\"address1\"],\"postalCode\":\"V5J 1P1\",\"cityName\":\"Vancouver\",\"countryCode\":\"CA\",\"stateCode\":\"BC\",\"phone\":\"1-604-555-5551\",\"rate\":\"55.00\"}"));
        assertThat(testData.contains("{\"name\":\"name2\",\"addressLines\":[\"address2\"],\"postalCode\":\"V5J 2P2\",\"cityName\":\"Vancouver\",\"countryCode\":\"CA\",\"stateCode\":\"BC\",\"phone\":\"1-604-555-5552\",\"rate\":\"65.00\"}"));
        assertThat(testData.contains("{\"name\":\"name3\",\"addressLines\":[\"address3\"],\"postalCode\":\"V5J 3P3\",\"cityName\":\"Vancouver\",\"countryCode\":\"CA\",\"stateCode\":\"BC\",\"phone\":\"1-604-555-5553\",\"rate\":\"55.00\"}"));

        System.out.println("All good with Controller!");
    }
}
