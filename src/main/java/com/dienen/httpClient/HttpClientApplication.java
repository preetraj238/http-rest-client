package com.dienen.httpClient;

import com.dienen.httpClient.utils.HttpClientTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HttpClientApplication {


    public static void main(String[] args) {
        SpringApplication.run(HttpClientApplication.class, args);
    }
}
