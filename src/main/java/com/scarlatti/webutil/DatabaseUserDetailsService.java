package com.scarlatti.webutil;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.util.List;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Wednesday, 11/14/2018
 */
@Service
public class DatabaseUserDetailsService implements UserDetailsService {

    private NamedParameterJdbcTemplate jdbcTemplate;

    public DatabaseUserDetailsService(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MapSqlParameterSource params = new MapSqlParameterSource("username", username);
        try {
            User user = jdbcTemplate.queryForObject("select * from users where username = :username", params, this::mapToUser);
            List<GrantedAuthority> authorities = jdbcTemplate.query("select * from user_roles where username = :username", params, this::mapToAuthority);

            return new AppUser(
                user.username,
                user.password,
                authorities,
                user.enabled
            );
        } catch (Exception e) {
            throw new RuntimeException("Error querying data for user " + username, e);
        }
    }

    private User mapToUser(ResultSet rs, int rowNum) {
        try {
            User user = new User();
            user.username = rs.getString("username");
            user.password = rs.getString("password");
            user.enabled = rs.getBoolean("enabled");

            return user;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private GrantedAuthority mapToAuthority(ResultSet rs, int rowNum) {
        try {
            return new SimpleGrantedAuthority(rs.getString("role"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static class User {
        private String username;
        private String password;
        private boolean enabled;
    }
}
