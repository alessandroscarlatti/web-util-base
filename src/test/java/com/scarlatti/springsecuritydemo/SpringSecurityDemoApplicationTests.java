package com.scarlatti.springsecuritydemo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SpringSecurityDemoApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void contextLoads() {
    }

    @Test
    public void canAccessPublicPages() throws Exception {
        mockMvc
            .perform(get("/"))
            .andDo(print())
            .andExpect(status().isOk());

        mockMvc
            .perform(get("/login"))
            .andDo(print())
            .andExpect(status().isOk());
    }


    @Test
    public void shouldReturnLoginPageWhenNotLoggedOn() throws Exception {
        mockMvc
            .perform(get("/secret"))
            .andDo(print())
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    public void canLoginAndAccessSecretPage() throws Exception {

        mockMvc
            .perform(get("/secret"))
            .andDo(print())
            .andExpect(redirectedUrl("http://localhost/login"));

        mockMvc
            .perform(
                formLogin().user("guest").password("wrong password")
            )
            .andDo(print())
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login?error"));

        MvcResult login = mockMvc
            .perform(
                formLogin().user("guest").password("guest")
            )
            .andDo(print())
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"))
            .andReturn();

        mockMvc
            .perform(
                get("/secret").session((MockHttpSession) login.getRequest().getSession())
            )
            .andDo(print())
            .andExpect(status().isOk());

        mockMvc
            .perform(logout())
            .andDo(print())
            .andExpect(redirectedUrl("/login?logout"));

        mockMvc
            .perform(get("/secret"))
            .andDo(print())
            .andExpect(redirectedUrl("http://localhost/login"));
    }

}
