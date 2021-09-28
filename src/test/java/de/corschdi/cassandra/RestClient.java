package de.corschdi.cassandra;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class RestClient {

    private final static String LOCAL_ENDPOINT = "http://localhost:8080";

    private final RestTemplate restTemplate = new RestTemplate();

    public <T, U> ResponseEntity<U> executeRequest(HttpMethod method, String path, T body, Class<U> responseType) {
        var headers = new HttpHeaders();
        HttpEntity<T> httpEntity;

        if (method == HttpMethod.POST || method == HttpMethod.PUT) {
            httpEntity = new HttpEntity<>(body, headers);
        } else {
            httpEntity = new HttpEntity<>(headers);
        }

        return this.restTemplate.exchange(LOCAL_ENDPOINT + path, method, httpEntity, responseType);
    }

}
