package com.telran.springsecurityauthentication.repository;

import com.telran.springsecurityauthentication.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {

    Person findByUsername(String username);
    Person findByUsernameAndPassword(String username, String password);
}
