package com.koblan.usersapp.service;

import java.time.Period;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.koblan.usersapp.exceptions.IncorrectDateRangeException;
import com.koblan.usersapp.exceptions.NoSuchUserException;
import com.koblan.usersapp.exceptions.NotHaveMinimumAgeException;
import com.koblan.usersapp.exceptions.PatchUserException;
import com.koblan.usersapp.model.User;
import com.koblan.usersapp.service.validator.EmailValidator;

import java.time.LocalDate;

import java.util.Optional;

@Service
public class UserService {

    private List<User> usersList= new ArrayList<>();

    @Value( "${default.minAge}" )
    private int minAge;
    
    public Collection<User> getAllUsers() {
        return usersList.stream()
          .filter(Objects::nonNull)
          .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    public Optional<User> getUser(int id) {
        return Optional.ofNullable(usersList.get(id-1));
    }

    public User createUser(User user) throws NotHaveMinimumAgeException {
        Period period=Period.between(user.getBirthDate(), LocalDate.now());
        int age=period.getYears();
        if (age<minAge) 
        throw new NotHaveMinimumAgeException("User should have at least 18 years to register");
        User.doIncrementId();
        int incr=(int)User.getIncrementId();
        user.setId(incr);
        usersList.add(user);
        int i=(int)User.getIncrementId();
        User newUser=usersList.get(i-1);
        return newUser;
    }

    public void patchUser(User partialUser, int id) throws NoSuchUserException, PatchUserException {
        if (id>User.getIncrementId() || id<1) {
            throw new NoSuchUserException("There is no user with such id");
        }
        User existingUser=usersList.get(id-1);
        if (existingUser==null) {
            throw new NoSuchUserException("There is no user with such id");
        }

        if(partialUser.getFirstName()!=null){
            existingUser.setFirstName(partialUser.getFirstName());
        }

        if(partialUser.getLastName()!=null){
            existingUser.setLastName(partialUser.getLastName());
        }

        if(partialUser.getEmail()!=null){
            if (!EmailValidator.isValidEmail(partialUser.getEmail())) {
               throw new PatchUserException("Email should be valid");
            }
            existingUser.setEmail(partialUser.getEmail());
        }

       if(partialUser.getBirthDate()!=null){
            if (!partialUser.getBirthDate().isBefore(LocalDate.now())) {
               throw new PatchUserException("BirtDate should be earlier than current date");
            }
            existingUser.setBirthDate(partialUser.getBirthDate());
        }

       if(partialUser.getAddress()!=null){
            existingUser.setAddress(partialUser.getAddress());
        }

       if(partialUser.getPhoneNumber()!=null){
            existingUser.setPhoneNumber(partialUser.getPhoneNumber());
        }

        usersList.set(id-1, existingUser);
    }


    public void updateUser(User user, int id) throws NoSuchUserException {
        if (id>User.getIncrementId() || id<1) {
            throw new NoSuchUserException("There is no user with such id");
        }
        User existingUser=usersList.get(id-1);
        if (existingUser==null || id>User.getIncrementId() || id<1) {
            throw new NoSuchUserException("There is no user with such id");
        }
        user.setId(id);
        usersList.set(id-1, user);
    }


    public void deleteUser(int id) throws NoSuchUserException {
        if (id>User.getIncrementId() || id<1) {
            throw new NoSuchUserException("There is no user with such id");
        }
        User existingUser=usersList.get(id-1);
        if (existingUser==null) {
            throw new NoSuchUserException("There is no user with such id");
        }
        usersList.set(id-1, null);
    }

    public List<User> getUsers(LocalDate fromDate, LocalDate toDate) throws IncorrectDateRangeException {
        if (!toDate.isAfter(fromDate)) {
            throw new IncorrectDateRangeException("FromDate parameter should be later than ToDate parameter"); 
        }
        List<User> users = usersList.stream()
        .filter(Objects::nonNull)
        .filter(user -> user.getBirthDate().isAfter(fromDate))
        .filter(user -> user.getBirthDate().isBefore(toDate))
        .collect(Collectors.toList());
        return users;
    }
}
