package com.koblan.usersapp.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.springframework.http.MediaType;
import static org.hamcrest.Matchers.hasSize;

import com.koblan.usersapp.UsersappApplication;
import com.koblan.usersapp.model.User;
import com.koblan.usersapp.service.UserService;

import org.junit.jupiter.api.TestInstance.Lifecycle;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = UsersappApplication.class)
@TestInstance(Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
public class UserSpringControllerIntegrationTest {

       @Autowired
       private MockMvc mvc;

       @Autowired
       private UserService userService;

       @BeforeAll
       public void setUp() throws Exception {

       }

       @Test
       public void checkCreateUserMethod() throws Exception {
            User newUser=new User("Name1","Surname1","name1@mysite.com",LocalDate.of(1995,01,02));
            mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(JsonConverter.mapToJson(newUser)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("Name1")))
            .andExpect(jsonPath("$.lastName", is("Surname1")));

            User user=userService.getUser(1).get();
            assertEquals(user.getEmail(),"name1@mysite.com");
            assertEquals(user.getBirthDate(),LocalDate.of(1995,01,02));
       }

       @Test
       public void checkGetUsersByDateRangeMethod() throws Exception {
            userService.createUser(new User("Name2","Surname3","name2@mysite.com",LocalDate.of(1999,01,02)));
            userService.createUser(new User("Name3","Surname3","name3@mysite.com",LocalDate.of(2001,01,02)));

            mvc.perform(get("/users/range").contentType(MediaType.APPLICATION_JSON).param("from","02/01/1998").param("to","30/12/2001"))
           .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2)))
           .andExpect(jsonPath("$[0].firstName", is("Name2"))).andExpect(jsonPath("$[1].firstName", is("Name3")));
       }

}
