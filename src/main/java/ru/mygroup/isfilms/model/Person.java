package ru.mygroup.isfilms.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Person {

    private Integer id;
    private String firstName;
    private String lastName;

    private String middleName;
    private LocalDate birthDate;
    private String profession;
    private String biography;
    private byte[] photoImage;

    private Studio studio;
    private List<Country> countries = new ArrayList<>();

    public Person() {}
    public Person(String firstName, String lastName, LocalDate birthDate, String profession) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.profession = profession;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public byte[] getPhotoImage() {
        return photoImage;
    }

    public void setPhotoImage(byte[] photoImage) {
        this.photoImage = photoImage;
    }

    public Studio getStudio() {
        return studio;
    }

    public void setStudio(Studio studio) {
        this.studio = studio;
    }

    public List<Country> getCountries() {
        return countries;
    }

    public void setCountries(List<Country> countries) {
        this.countries = countries;
    }

    public String getFullName() {
        return lastName + " " + firstName + (middleName != null ? " " + middleName : "");
    }

    @Override
    public String toString() {
        return getFullName();
    }
}
