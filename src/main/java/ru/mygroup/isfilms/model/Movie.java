package ru.mygroup.isfilms.model;

import java.util.ArrayList;
import java.util.List;

public class Movie {

    private Integer id;
    private String title;
    private Integer releaseYear;
    private String description;
    private Integer duration;
    private String type;          // "movie" или "series"
    private Double rating;
    private String ageRating;     // "PG-13", "R", etc.
    private byte[] posterImage;

    // Связанные объекты
    private Country country;
    private Studio studio;
    private List<Genre> genres = new ArrayList<>();
    private List<Person> actors = new ArrayList<>();
    private List<Person> directors = new ArrayList<>();

    public Movie() {}
    public Movie(String title, Integer releaseYear, String description, Integer duration, String type, Double rating, String ageRating, byte[] posterImage) {
        this.title = title;
        this.releaseYear = releaseYear;
        this.description = description;
        this.duration = duration;
        this.type = type;
        this.rating = rating;
        this.ageRating = ageRating;
        this.posterImage = posterImage;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getAgeRating() {
        return ageRating;
    }

    public void setAgeRating(String ageRating) {
        this.ageRating = ageRating;
    }

    public byte[] getPosterImage() {
        return posterImage;
    }

    public void setPosterImage(byte[] posterImage) {
        this.posterImage = posterImage;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Studio getStudio() {
        return studio;
    }

    public void setStudio(Studio studio) {
        this.studio = studio;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public List<Person> getActors() {
        return actors;
    }

    public void setActors(List<Person> actors) {
        this.actors = actors;
    }

    public List<Person> getDirectors() {
        return directors;
    }

    public void setDirectors(List<Person> directors) {
        this.directors = directors;
    }
}
