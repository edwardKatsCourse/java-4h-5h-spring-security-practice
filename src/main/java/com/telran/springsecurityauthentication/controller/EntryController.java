package com.telran.springsecurityauthentication.controller;

import com.telran.springsecurityauthentication.entity.Person;
import com.telran.springsecurityauthentication.entity.PersonSession;
import com.telran.springsecurityauthentication.repository.PersonRepository;
import com.telran.springsecurityauthentication.repository.PersonSessionRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;


@RestController
public class EntryController {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PersonSessionRepository personSessionRepository;

    @PostMapping("/register")
    public void registerPerson(@RequestBody PersonRequest personRequest) {
        //select * from Person where username =
        Person person = personRepository.findByUsername(personRequest.getUsername());
        if (person == null) {
            personRepository.save(
                    Person.builder()
                            .username(personRequest.getUsername())
                            .password(personRequest.getPassword())
                    .createdDate(LocalDateTime.now())
                    .build());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Person with such username already exists"); //400
        }

        /**
         * Else
         *
         * 1. Ignore existing user - always 200
         * 2. throw exception, describing that such user already exists
         */

    }

    //public static final String LOGIN_URL = "/login";

    @PostMapping("/login")
    public String loginPerson(@RequestBody PersonRequest personRequest) {
        //username + password
        //select * from Person where username = {} and password = {}
        Person person = personRepository.findByUsernameAndPassword(personRequest.getUsername(), personRequest.getPassword());
        if (person == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or password is incorrect");
        }
        PersonSession personSession = PersonSession
                .builder()
                .sessionId(UUID.randomUUID().toString())
                .person(person)
                .createdOn(LocalDateTime.now())
                .build();

        personSessionRepository.save(personSession);

        return personSession.getSessionId();
    }

    @PutMapping("/logout")
    @Transactional
    public void logout(@AuthenticationPrincipal PersonSession personSession) {
        personSessionRepository.deleteBySessionId(personSession.getSessionId());
    }

    @GetMapping("/person-info")
    public Person getPersonInfo(@AuthenticationPrincipal PersonSession personSession) {
        return personSession.getPerson();
    }
}

@AllArgsConstructor
@NoArgsConstructor
@Data
class PersonRequest {
    private String username;
    private String password;
}