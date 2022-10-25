package com.example.ergasia3;

public class CitizenModel {

    String firstName, lastName, email, telephone, dateOfBirth, type, status;

    public CitizenModel() {

    }

    public CitizenModel(String firstName, String lastName, String email, String telephone, String dateOfBirth, String type, String status) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.telephone = telephone;
        this.dateOfBirth = dateOfBirth;
        this.type = type;
        this.status = status;
    }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

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

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
}
