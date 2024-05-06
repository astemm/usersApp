package com.koblan.usersapp.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.springframework.http.MediaType;
import static org.hamcrest.Matchers.hasSize;

import com.koblan.usersapp.UsersappApplication;
import com.koblan.usersapp.exceptions.NoSuchUserException;
import com.koblan.usersapp.exceptions.NotHaveMinimumAgeException;
import com.koblan.usersapp.model.User;
import com.koblan.usersapp.service.UserService;

import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;


@TestMethodOrder(MethodOrderer.MethodName.class)
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
          User firstUser=new User("Name0","Surname0","name0@mysite.com",LocalDate.of(1990,01,02));
          firstUser.setAddress("Franka str, 40, Lviv");
          userService.createUser(firstUser);
       }

       //First test method ordered by its alphabetical name
       @Test    
       public void checkCreateUserMethod() throws Exception {
            User newUser=new User("Name1","Surname1","name1@mysite.com",LocalDate.of(1995,01,02));
            mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(JsonConverter.mapToJson(newUser)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("Name1")))
            .andExpect(jsonPath("$.lastName", is("Surname1")))
            .andExpect(jsonPath("$.id", is(2)));

            User user=userService.getUser(2).get();
            assertEquals(user.getEmail(),"name1@mysite.com");
            assertEquals(user.getBirthDate(),LocalDate.of(1995,01,02));
       }

       //Second test method
       @Test
       public void checkCreateUserMethodAndThrowMinAgeException() throws Exception {
            User newUser=new User("Name1","Surname1","name1@mysite.com",LocalDate.of(2007,01,02));
            mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(JsonConverter.mapToJson(newUser)))
            .andExpect(status().isBadRequest())
            .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotHaveMinimumAgeException));
       }

       //Third test method
       @Test
       public void checkCreateUserMethodAndValidationException() throws Exception {
          User newUser=new User("Name1","Surname1","name1@mysite.com",LocalDate.of(2002,01,02));
          newUser.setLastName(null);
          mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(JsonConverter.mapToJson(newUser)))
          .andExpect(status().isBadRequest())
          .andExpect(result -> {
               assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException);
                  }
               );
       }

       //Seventh test method
       @Test
       public void checkUpdateUserMethod() throws Exception {
            User updatedUser=new User("Name00","Surname00","name00@mysite.com",LocalDate.of(1991,01,02));
            mvc.perform(put("/users/1").contentType(MediaType.APPLICATION_JSON).content(JsonConverter.mapToJson(updatedUser)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("Name00")))
            .andExpect(jsonPath("$.lastName", is("Surname00")));

            User user=userService.getUser(1).get();
            assertEquals(user.getEmail(),"name00@mysite.com");
            assertEquals(user.getBirthDate(),LocalDate.of(1991,01,02));
       }

       //Sixth test method
       @Test
       public void checkPatchUserMethod() throws Exception {
            User patchedUser=new User();
            patchedUser.setFirstName("Name000");
            patchedUser.setEmail("name000@mysite.com");
            mvc.perform(patch("/users/1").contentType(MediaType.APPLICATION_JSON).content(JsonConverter.mapToJson(patchedUser)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("Name000")))
            .andExpect(jsonPath("$.lastName", is("Surname0")));

            User user=userService.getUser(1).get();
            assertEquals(user.getEmail(),"name000@mysite.com");
            assertEquals(user.getBirthDate(),LocalDate.of(1990,01,02));
       }

       //Forth test method
       @Test
       public void checkDeleteUserMethod() throws Exception {
            userService.createUser(new User("Name31","Surname31","name31@mysite.com",LocalDate.of(1992,01,02)));
            int size=userService.getAllUsers().size();
            assertEquals(size,3);
            mvc.perform(delete("/users/3"))
            .andExpect(status().isOk());

            size=userService.getAllUsers().size();
            assertEquals(size,2);

            assertThrows(NoSuchUserException.class, () -> {
            userService.deleteUser(3);
            });
       }
       
       //Fifth test method
       @Test
       public void checkGetUsersByDateRangeMethod() throws Exception {
            userService.createUser(new User("Name2","Surname3","name2@mysite.com",LocalDate.of(1999,01,02)));
            userService.createUser(new User("Name3","Surname3","name3@mysite.com",LocalDate.of(2001,01,02)));

            mvc.perform(get("/users/range").contentType(MediaType.APPLICATION_JSON).param("from","02/01/1998").param("to","30/12/2001"))
           .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2)))
           .andExpect(jsonPath("$[0].firstName", is("Name2"))).andExpect(jsonPath("$[1].firstName", is("Name3")));
       } 

}
