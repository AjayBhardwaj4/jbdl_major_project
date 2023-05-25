package com.bhardwaj.services.epay.configs;

import com.bhardwaj.services.epay.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import static com.bhardwaj.services.epay.constants.UserConstants.*;

@Configuration
public class UserSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserService userService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic()
                .and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/user/**").permitAll() //sign up of new account
                .antMatchers("/user/**").hasAuthority(USER_AUTHORITY) // user driven actions
                .antMatchers("/**").hasAnyAuthority(ADMIN_AUTHORITY, SERVICE_AUTHORITY) //admin driven actions
                .and()
                .formLogin();
    }
}
