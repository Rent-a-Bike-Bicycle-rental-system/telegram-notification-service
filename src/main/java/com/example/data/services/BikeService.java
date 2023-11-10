package com.example.data.services;

import com.example.data.data.Bike;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class BikeService {
    @Value("${rest-api.base}")
    private String baseUrl;

    @Value("${rest-api.bikes}")
    private String bikeMapping;

    private final RestTemplate restTemplate;

    public BikeService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Bike getBikeById(int id) {
        var list = getBikeList();
        Optional<Bike> bike = list.stream().filter(o -> o.getId() == id).findFirst();

        return bike.orElse(null);
    }

    public List<Bike> getBikeList() {
        String url = baseUrl + bikeMapping;
        ParameterizedTypeReference<Bike[]> typeRef = new ParameterizedTypeReference<>() {};

        Bike[] array = restTemplate.exchange(url, HttpMethod.GET, null, typeRef).getBody();
        if(array == null)
            return null;

        return Arrays.asList(array);
    }
}
