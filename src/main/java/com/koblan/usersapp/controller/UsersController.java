package com.koblan.usersapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

import java.time.LocalDate;
import java.util.Optional;

import com.koblan.usersapp.exceptions.NotHaveMinimumAgeException;
import com.koblan.usersapp.exceptions.PatchUserException;
import com.koblan.usersapp.controller.annotation.ToIsMoreRecentThanFrom;
import com.koblan.usersapp.exceptions.IncorrectDateRangeException;
import com.koblan.usersapp.exceptions.NoSuchUserException;
import com.koblan.usersapp.model.User;
import com.koblan.usersapp.service.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

@RestController
public class UsersController {

     @Autowired
     UserService userService;

     @ToIsMoreRecentThanFrom
     public record GetTaskRequestParameters(@Past @NotNull @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate from,
     @Past @NotNull @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate to) {
}

     @PostMapping(value="/users")
     ResponseEntity<User> createUser(@Valid @RequestBody User user) throws NotHaveMinimumAgeException {
		return ResponseEntity.ok(userService.createUser(user));
     }

     @PutMapping(value = "/users/{id}")
     public  ResponseEntity<User> updateUser(@Valid @RequestBody User nuser, @PathVariable int id) throws NoSuchUserException {
	        userService.updateUser(nuser, id);
	        Optional<User> user = userService.getUser(id);
	        return new ResponseEntity<>(user.get(), HttpStatus.OK);
     }

     @PatchMapping(value = "/users/{id}")
     public  ResponseEntity<User> patchUser(@RequestBody User nuser, @PathVariable int id) throws NoSuchUserException, PatchUserException {
            userService.patchUser(nuser, id);
	        Optional<User> user = userService.getUser(id);
	        return new ResponseEntity<>(user.get(), HttpStatus.OK);
     }

     @DeleteMapping(value="/users/{id}")
     public ResponseEntity deleteUser(@PathVariable int id) throws NoSuchUserException {
			userService.deleteUser(id);
			return new ResponseEntity(HttpStatus.OK);
     }

     @GetMapping(value="/users/range")
     public ResponseEntity<List<User>> getUsersByDateRange(@RequestParam("from") @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate from,
                               @RequestParam("to") @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate to) 
                               throws IncorrectDateRangeException {
            List<User> users=userService.getUsers(from, to);
            return ResponseEntity.ok(users);
     }

     //Using custom validator to check that 'To' Date parameter value is after 'From' Date parameter value with Record type
     @GetMapping(value="/users/range1")
     public ResponseEntity<List<User>> getUsersByDateRange1(@Valid GetTaskRequestParameters parameters) 
                               throws IncorrectDateRangeException {
            List<User> users=null;
            try{
            users=userService.getUsers(parameters.from, parameters.to);
            }
            catch(Exception ex) {System.out.println(ex.getMessage()+"_"+(ex.getClass()));}
            return ResponseEntity.ok(users);
     }

     @GetMapping(value="/users")
     public List<User> getUsers() {
            List<User> users=List.copyOf(userService.getAllUsers());
            return users;
     }
}