package ru.mygroup.isfilms.model;

public class FilterDTO {
    private String title;
    private Integer yearFrom;
    private Integer yearTo;
    private Integer genreId;
    private Integer countryId;
    private Double ratingFrom;
    private Double ratingTo;

    // Конструктор
    public FilterDTO() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getYearFrom() {
        return yearFrom;
    }

    public void setYearFrom(Integer yearFrom) {
        this.yearFrom = yearFrom;
    }

    public Integer getYearTo() {
        return yearTo;
    }

    public void setYearTo(Integer yearTo) {
        this.yearTo = yearTo;
    }

    public Integer getGenreId() {
        return genreId;
    }

    public void setGenreId(Integer genreId) {
        this.genreId = genreId;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public Double getRatingTo() {
        return ratingTo;
    }

    public void setRatingTo(Double ratingTo) {
        this.ratingTo = ratingTo;
    }

    public Double getRatingFrom() {
        return ratingFrom;
    }

    public void setRatingFrom(Double ratingFrom) {
        this.ratingFrom = ratingFrom;
    }

    public boolean isEmpty() {
        return (title == null || title.trim().isEmpty()) &&
                yearFrom == null && yearTo == null &&
                genreId == null && countryId == null &&
                ratingFrom == null && ratingTo == null;
    }
}
