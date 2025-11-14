package com.example.demo.controllers;

import com.example.demo.dtos.UserDto;
import com.example.demo.entities.User;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate rest;

    private String url(String path) {
        return "http://localhost:" + port + "/api/users" + path;
    }

    @Test
    void createUser_andGetUser() {
        // CREATE user
        User user = new User();
        user.setUsername("leo");
        user.setEmail("leo@example.com");
        user.setPassword("pass1234");

        ResponseEntity<UserDto> postResponse =
                rest.postForEntity(url(""), user, UserDto.class);

        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        UserDto created = postResponse.getBody();
        assertThat(created).isNotNull();

        Long id = created.getId();

        // GET user
        ResponseEntity<UserDto> getResponse =
                rest.getForEntity(url("/" + id), UserDto.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().getUsername()).isEqualTo("leo");
        assertThat(getResponse.getBody().getEmail()).isEqualTo("leo@example.com");
    }

}
