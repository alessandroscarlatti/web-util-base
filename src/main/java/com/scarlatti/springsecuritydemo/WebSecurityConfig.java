package com.scarlatti.springsecuritydemo;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.encoding.PlaintextPasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.sql.DataSource;

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
    private DataSource dataSource;

    public WebSecurityConfig(UserDetailsService userDetailsService, DataSource dataSource) {
        this.userDetailsService = userDetailsService;
        this.dataSource = dataSource;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(userDetailsService);
//        auth.authenticationProvider(authProvider);

//        auth.inMemoryAuthentication()
//            .withUser("qwer")
//            .password("asdf")
//            .roles("USER");

        // we can just use this for jdbc stuff...
        // the multiple columns are read by spring using an assumed column order.
//          auth.jdbcAuthentication()
////              .dataSource(dataSource)
////              .usersByUsernameQuery("select username,password,enabled from users where username = ?")
////              .authoritiesByUsernameQuery("select username,role from user_roles where username = ?");

        // embedded ldap server
        auth.ldapAuthentication()
            .userDnPatterns("uid={0},ou=people")  // "list users by username"
            .groupSearchBase("ou=groups")  // the "groups" will be translated to ROLE_group1, ROLE_group2, etc.
            .contextSource()
                .url("ldap://localhost:8389/dc=springframework,dc=org")  // database url
                .and()
            .passwordCompare()
                .passwordEncoder(new PlaintextPasswordEncoder())  // everybody BUT ben works
//                .passwordEncoder(new LdapShaPasswordEncoder())  // ben works (because his password is hashed)
                .passwordAttribute("userPassword");

        // local docker server using classpath:schema2.ldif
//        auth.ldapAuthentication()
//            .userDnPatterns("uid={0},ou=people")
//            .groupSearchBase("ou=groups")
//            .contextSource().url("ldap://docker:389/dc=example,dc=org")
//            .managerDn("cn=admin,dc=example,dc=org") // need these credentials since the database requires a password to view anything.
//            .managerPassword("admin")
//            .and()
//            .passwordCompare()
//                .passwordEncoder(new PlaintextPasswordEncoder())
//                .passwordAttribute("userPassword");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            .antMatchers("/").permitAll()
            .antMatchers("/error").permitAll()
            .antMatchers("/resources/**").permitAll()
            .antMatchers("/secret").hasRole("USER")
            .antMatchers("/task1").hasRole("USER")
            .antMatchers("/admin").hasRole("ADMIN")
            .anyRequest().authenticated();  // as opposed to fully authenticated.

        http
            .formLogin()
            .loginPage("/login")  // must be specified to make Spring call our page, otherwise, it will generate one on its own, and it won't be as pretty as ours.
            .permitAll();

        http
            .logout()
            .permitAll();
    }
}
