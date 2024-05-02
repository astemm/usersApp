package com.koblan.usersapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

import java.time.LocalDate;
import java.util.Optional;

import com.koblan.usersapp.exceptions.NotHaveMinimumAgeException;
import com.koblan.usersapp.exceptions.IncorrectDateRangeException;
import com.koblan.usersapp.exceptions.NoSuchUserException;
import com.koblan.usersapp.model.User;
import com.koblan.usersapp.service.UserService;

import jakarta.validation.Valid;

@RestController
public class UsersController {

     @Autowired
     UserService userService;

     @PostMapping(value="/users")
	 public ResponseEntity<User> createUser(@Valid @RequestBody User user) throws NotHaveMinimumAgeException {
		return ResponseEntity.ok(userService.createUser(user));
	 }

     @PutMapping(value = "/users/{id}")
	    public  ResponseEntity<User> updateUser(@Valid @RequestBody User nuser, @PathVariable int id) throws NoSuchUserException {
	        userService.updateUser(nuser, id);
	        Optional<User> user = userService.getUser(id);
	        return new ResponseEntity<>(user.get(), HttpStatus.OK);
	 }

     @PatchMapping(value = "/users/{id}")
	    public  ResponseEntity<User> patchUser(@RequestBody User nuser, @PathVariable int id) throws NoSuchUserException {
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
        public List<User> getUsers(@RequestParam("from") @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate from,
                               @RequestParam("to") @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate to) 
                               throws IncorrectDateRangeException {
            List<User> users=userService.getUsers(from, to);
            System.out.println(users+">");
            return users;
     }

     @GetMapping(value="/users")
        public List<User> getUsers() {
            List<User> users=List.copyOf(userService.getAllUsers());
            return users;
     }


     @GetMapping(value="/users1")
     @ResponseBody
     public String sampleRequest() {
         return new String("Users Again");
     }
     
}