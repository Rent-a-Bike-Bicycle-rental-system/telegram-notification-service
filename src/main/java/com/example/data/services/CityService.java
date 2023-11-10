package com.example.data.services;

import com.example.data.data.City;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class CityService {
    @Value("${rest-api.base}")
    private String baseUrl;

    @Value("${rest-api.cities}")
    private String cityMapping;

    private final RestTemplate restTemplate;

    public CityService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public City getCityById(int id) {
        var list = getCityList();
        Optional<City> object = list.stream().filter(o -> o.getId() == id).findFirst();

        return object.orElse(null);
    }

    public List<City> getCityList() {
        String url = baseUrl + cityMapping;
        ParameterizedTypeReference<City[]> typeRef = new ParameterizedTypeReference<>() {};

        City[] array = restTemplate.exchange(url, HttpMethod.GET, null, typeRef).getBody();
        if(array == null)
            return null;

        return Arrays.asList(array);
    }
}
