package com.scarlatti.springsecuritydemo;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Saturday, 11/10/2018
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private UserDetailsService userDetailsService;

    public WebSecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        auth.authenticationProvider(authProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            .antMatchers("/").permitAll()
            .antMatchers("/error").permitAll()
            .antMatchers("/resources/**").permitAll()
            .antMatchers("/secret").hasRole("USER")
            .antMatchers("/admin").hasRole("ADMIN")
            .anyRequest().authenticated();

        http
            .formLogin()
            .loginPage("/login")
            .failureUrl("/error")
            .permitAll();

        http
            .logout()
            .permitAll();
    }

//    @Bean  // this must be a bean
//    @Override
//    protected UserDetailsService userDetailsService() {
//        UserDetails user =
//            User.withUsername("guest")
//                .password("guest")
//                .roles("USER")
//                .build();
//
//        UserDetails admin = User.withUsername("admin")
//            .password("admin")
//            .roles("USER", "ADMIN")
//            .build();
//
//        return new InMemoryUserDetailsManager(user, admin);
//    }
}
