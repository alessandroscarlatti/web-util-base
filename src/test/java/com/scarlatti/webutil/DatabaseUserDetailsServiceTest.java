package com.scarlatti.webutil;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Wednesday, 11/14/2018
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class DatabaseUserDetailsServiceTest {

    @Autowired
    DatabaseUserDetailsService userDetailsService;

    @Test
    public void selectUserFromDatabase() {
        UserDetails user = userDetailsService.loadUserByUsername("alex");

        Assert.assertEquals(user.getUsername(), "alex");
        Assert.assertEquals(user.getPassword(), "asdf");
        Assert.assertEquals(user.isEnabled(), true);
        Assert.assertEquals(user.getAuthorities(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
