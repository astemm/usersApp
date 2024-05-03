package com.koblan.usersapp.tests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.koblan.usersapp.controller.UsersController;
import com.koblan.usersapp.exceptions.NoSuchUserException;
import com.koblan.usersapp.service.UserService;
import com.koblan.usersapp.model.User;


import org.springframework.http.MediaType;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import  static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import  static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Optional;

import org.junit.jupiter.api.TestInstance.Lifecycle;

//@RunWith(SpringRunner.class)
@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
@WebMvcTest(UsersController.class)
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    User newUser;

    @BeforeAll
    public void setUp() throws Exception {
        newUser=new User("MyName","MySurname","my@mysite.com",LocalDate.of(1985,11,12));
    }

    @Test
    public void checkCreateUser() throws Exception {
         
          given(userService.createUser(Mockito.any())).willReturn(newUser);
          mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON).content(JsonConverter.mapToJson(newUser))).andExpect(status().isOk())
          .andExpect(jsonPath("$.firstName", is("MyName")))
          .andExpect(jsonPath("$.lastName", is("MySurname")))
          .andExpect(jsonPath("$.email", is("my@mysite.com")))
          .andExpect(jsonPath("$.birthDate", is("12/11/1985")))
          ;

          verify(userService, VerificationModeFactory.times(1)).createUser(Mockito.any());
    }

    @Test
    public void checkGetUsersByDateRange() throws Exception {

        User user1=new User("Name1","Surname1","name1@mysite.com",LocalDate.of(1985,11,12));
        User user2=new User("Name2","Surname2","name2@mysite.com",LocalDate.of(1986,11,12));
        User user3=new User("Name3","Surname3","name3@mysite.com",LocalDate.of(1987,11,12));
        user2.setAddress("Zelena str, 7");
        user3.setAddress("Shevchenka str, 10");
        user3.setPhoneNumber("0998889977");

        List<User> users = List.of(user1, user2, user3);
        given(userService.getUsers(LocalDate.of(1985, 01, 02), LocalDate.of(1995, 12, 30))).willReturn(users);

        mvc.perform(get("/users/range").contentType(MediaType.APPLICATION_JSON).param("from","02/01/1985").param("to","30/12/1995"))
        .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[0].firstName", is("Name1"))).andExpect(jsonPath("$[1].firstName", is("Name2")))
                .andExpect(jsonPath("$[2].lastName", is("Surname3")));
        
        verify(userService, VerificationModeFactory.times(1)).getUsers(LocalDate.of(1985, 01, 02), LocalDate.of(1995, 12, 30));

    }

    
    @Test
    public void checkUpdateUser() throws Exception {
        User updatedUser=new User("Name7","Surname7","name7@mysite.com",LocalDate.of(1990,11,12));
        doThrow(new NoSuchUserException("No such user")).when(userService).updateUser(Mockito.any(User.class), Mockito.anyInt());
        mvc.perform(put("/users/7").contentType(MediaType.APPLICATION_JSON)
        .content(JsonConverter.mapToJson(updatedUser))).andExpect(status().isNotFound()).
        andExpect(result -> assertTrue(result.getResolvedException() instanceof NoSuchUserException));
        reset(userService);

    }

    @Test
    public void checkPatchUser() throws Exception {
        User patchedUser=new User();
        patchedUser.setLastName("Surname55");
        patchedUser.setEmail("name55@my.com");
        User user5=new User("Name5","Surname55","name55@mysite.com",LocalDate.of(1990,11,12));
        doNothing().when(userService).patchUser(patchedUser, 5);
        Mockito.when(userService.getUser(5)).thenReturn(Optional.of(user5));
        mvc.perform(patch("/users/5").contentType(MediaType.APPLICATION_JSON)
        .content(JsonConverter.mapToJson(patchedUser))).andExpect(status().isOk());
        verify(userService, VerificationModeFactory.times(1)).patchUser(Mockito.any(User.class), Mockito.anyInt());
    }

    @Test
    public void checkDeleteUser() throws Exception {

        doNothing().when(userService).deleteUser(Mockito.anyInt());
        mvc.perform(delete("/users/2")).andExpect(status().isOk());
        verify(userService, VerificationModeFactory.times(1)).deleteUser(2);
    
    }

}
