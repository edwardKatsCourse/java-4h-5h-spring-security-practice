package com.telran.springsecurityauthentication.repository;

import com.telran.springsecurityauthentication.entity.PersonSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonSessionRepository extends JpaRepository<PersonSession, Long> {

    PersonSession findBySessionId(String sessionId);
    void deleteBySessionId(String sessionId);
}
