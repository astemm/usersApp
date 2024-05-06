package com.koblan.usersapp.model;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

public class User {
    
    private static long ID;

    private int id;

    @NotBlank(message = "FirstName cannot be empty")
    private String firstName;

    @NotBlank(message = "LastName cannot be empty")
    private String lastName;
    
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email should be valid")
    private String email;
    
    @NotNull(message = "BirthDate cannot be empty")
    @Past(message="BirtDate should be earlier than current date")
    //@DateTimeFormat(pattern = "dd/MM/yyyy")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd/MM/yyyy")
    private LocalDate birthDate;

    private String address;

    private String phoneNumber;

    public User() {}

    public User(String firstName, String lastName, String email,
    LocalDate birthDate)      {
        this.firstName=firstName;
        this.lastName=lastName;
        this.email=email;
        this.birthDate=birthDate;
    }

    public User(String firstName, String lastName, String email,
    LocalDate birthDate, String field, String fieldName)      {
        this.firstName=firstName;
        this.lastName=lastName;
        this.email=email;
        this.birthDate=birthDate;
        setOptionalField(field, fieldName);
    }

    public void setOptionalField(String fieldValue, String fieldName) {
            if (fieldName.equals("address")) {
                  this.address=fieldValue;
            }

            else if (fieldName.equals("phoneNumber")) {
                this.phoneNumber=fieldValue;
            }
    }

    public static void doIncrementId() {
		ID++;
	}

    public static long getIncrementId() {
		return ID;
	}

    public int getId() {
		return id;
	}

    public void setId(int id) {
        this.id=id;
    }
    
    public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

    public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

    public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

    public LocalDate getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}

    public String getAddress() {
        return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

    public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

}
