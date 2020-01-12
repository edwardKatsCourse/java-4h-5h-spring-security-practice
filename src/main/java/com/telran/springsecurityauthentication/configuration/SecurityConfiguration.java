package com.telran.springsecurityauthentication.configuration;

import com.telran.springsecurityauthentication.entity.PersonSession;
import com.telran.springsecurityauthentication.repository.PersonSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /**
         * 1. csrf disable
         * 2. session management policy - stateless
         * 3. ant matchers (URLs: permit all, authorized)
         * 4. logout disable
         */

        http
                .csrf()
                    .disable() //.jsp, thymeleaf

                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                .authorizeRequests()
                //  /registration /login
                    .antMatchers("/register", "/login").permitAll()
                // /users
                // /users/1
                // /users/1/images
                // /users/1/images/1
                // /users/1/images/1/edit

                // /*
                // /*/*
                // /*/*/*
                // /*/*/*/*
                //
                // /**

                    .antMatchers("/**").authenticated()
                    .and()

                // /logout - to force Spring Security to intercept requests to /logout
                .logout()
                    .disable();

        http.addFilterBefore(securityFilter(), UsernamePasswordAuthenticationFilter.class);
        //http
            //color
            //  red
            //  blue
            //  and
            //brightness
            //  percentage
            //  and
            //sound
    }

    @Bean //java config
    public SecurityFilter securityFilter() {
        return new SecurityFilter();
    }
}

//@Component annotationConfig
class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private PersonSessionRepository personSessionRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,

            FilterChain filterChain) throws ServletException, IOException {

        //Authorization - our session ID from login

        String header = httpServletRequest.getHeader("Authorization");

        if (header != null) {
            PersonSession personSession = personSessionRepository.findBySessionId(header);
            if (personSession != null) {

                //Authentication!!!
                Authentication key = new UsernamePasswordAuthenticationToken(
                        //principal - user/person
                        personSession,
                        null,
                        new ArrayList<>()
                );

                SecurityContextHolder.getContext().setAuthentication(key);
            }
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}