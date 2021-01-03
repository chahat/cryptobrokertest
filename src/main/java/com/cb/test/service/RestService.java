package com.cb.test.service;

import com.cb.test.dto.PriceDTO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
public class RestService {

    private final RestTemplate restTemplate;

    public RestService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }
    
    @Value("${com.cb.test.btc-url}")
    private String url;

    public PriceDTO getPostsPlainJSON() {
       try {
        	HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity request = new HttpEntity(headers);
            ResponseEntity<PriceDTO> response = this.restTemplate.exchange(url, HttpMethod.GET, request, PriceDTO.class);
            if(response.getStatusCode() == HttpStatus.OK) {
            	PriceDTO result = response.getBody();
        		System.out.println(result);
                return result;
            } else {
                return null;
            }
        } catch (Exception e) {
        	System.out.println("Cannot connect to external url: "+url);
			return null;
		}
        
    }
}
